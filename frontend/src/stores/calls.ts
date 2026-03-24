import { defineStore, storeToRefs } from "pinia";
import { ref, watch } from "vue";
import { useAuthStore } from "./auth";
import client, { sendMessage, subscribe, isConnected } from "@/services/websocket";
import type { StompSubscription } from "@stomp/stompjs";

interface CallOfferDto {
  callId: string;
  callerId: string;
  targetUserId: string;
  sdp: string;
  type: "offer";
}

interface CallAnswerDto {
  callId: string;
  callerId: string;
  responderId: string;
  sdp: string;
  type: "answer";
}

interface IceCandidateDto {
  callId: string;
  targetUserId: string;
  candidate: string;
  sdpMid: string | null;
  sdpMLineIndex: number | null;
  type: "ice-candidate";
}

interface CallActionDto {
  callId: string;
  targetUserId: string;
  action: "REJECT" | "END" | "BUSY";
  type: "call-action";
}

type CallState = "idle" | "calling" | "incoming" | "connected" | "ended";

export const useCallsStore = defineStore("calls", () => {
  const authStore = useAuthStore();
  const { user } = storeToRefs(authStore);

  const callState = ref<CallState>("idle");
  const remoteUserId = ref<string | null>(null); // The other person
  const currentCallId = ref<string | null>(null);
  const localStream = ref<MediaStream | null>(null);
  const remoteStream = ref<MediaStream | null>(null);
  const isAudioEnabled = ref(true);

  // RTCPeerConnection instance
  let peerConnection: RTCPeerConnection | null = null;
  let callSubscription: StompSubscription | null = null;

  // Cleanup function
  const resetCall = () => {
    if (localStream.value) {
      localStream.value.getTracks().forEach((track) => track.stop());
      localStream.value = null;
    }
    if (peerConnection) {
      peerConnection.close();
      peerConnection = null;
    }
    remoteStream.value = null;
    callState.value = "idle";
    remoteUserId.value = null;
    currentCallId.value = null;
    isAudioEnabled.value = true;
  };

  const toggleAudio = () => {
    if (localStream.value) {
      isAudioEnabled.value = !isAudioEnabled.value;
      localStream.value.getAudioTracks().forEach((track) => {
        track.enabled = isAudioEnabled.value;
      });
    }
  };

  const setupPeerConnection = () => {
    const config: RTCConfiguration = {
      iceServers: [{ urls: "stun:stun.l.google.com:19302" }],
    };
    peerConnection = new RTCPeerConnection(config);

    peerConnection.onicecandidate = (event) => {
      if (event.candidate && remoteUserId.value) {
        const candidateDto: IceCandidateDto = {
          callId: currentCallId.value || "unknown",
          targetUserId: remoteUserId.value,
          candidate: event.candidate.candidate,
          sdpMid: event.candidate.sdpMid,
          sdpMLineIndex: event.candidate.sdpMLineIndex,
          type: "ice-candidate",
        };
        sendMessage("/app/call.ice", candidateDto);
      }
    };

    peerConnection.ontrack = (event) => {
      remoteStream.value = event.streams[0] || null;
    };

    // Add local tracks
    if (localStream.value) {
      localStream.value.getTracks().forEach((track) => {
        peerConnection?.addTrack(track, localStream.value!);
      });
    }
  };

  const initialize = () => {
    if (!user.value || !isConnected.value) return;
    if (callSubscription) return;

    // Subscribe to signaling queue
    callSubscription = subscribe("/user/queue/calls", async (message: any) => {
      console.log("Received call signal:", message);

      switch (message.type) {
        case "offer":
          handleOffer(message);
          break;
        case "answer":
          handleAnswer(message);
          break;
        case "ice-candidate":
          handleIceCandidate(message);
          break;
        case "call-action":
          handleCallAction(message);
          break;
      }
    });
  };

  const startCall = async (targetId: string) => {
    try {
      callState.value = "calling";
      remoteUserId.value = targetId;
      currentCallId.value = `${user.value?.id}-${targetId}-${Date.now()}`;

      // Get user media
      localStream.value = await navigator.mediaDevices.getUserMedia({
        audio: true,
      });

      setupPeerConnection();

      const offer = await peerConnection!.createOffer();
      await peerConnection!.setLocalDescription(offer);

      const offerDto: CallOfferDto = {
        callId: currentCallId.value || `call-${Date.now()}`,
        callerId: user.value!.id,
        targetUserId: targetId,
        sdp: offer.sdp!,
        type: "offer",
      };

      sendMessage("/app/call.offer", offerDto);
    } catch (err) {
      console.error("Failed to start call", err);
      resetCall();
    }
  };

  const handleOffer = async (offer: CallOfferDto) => {
    if (callState.value !== "idle") {
      // Already in a call, send BUSY
      const action: CallActionDto = {
        callId: "unknown",
        targetUserId: offer.callerId,
        action: "BUSY",
        type: "call-action",
      };
      sendMessage("/app/call.action", action);
      return;
    }

    callState.value = "incoming";
    remoteUserId.value = offer.callerId;
    // We store the offer temporarily? Or just act on it when accepted.
    // For now, let's just initialize the PC when accepted, but we need the SDP.
    // Let's store the pending offer SDP.
    sessionStorage.setItem("pendingOfferSdp", offer.sdp);
  };

  const acceptCall = async () => {
    if (!remoteUserId.value) return;

    try {
      callState.value = "connected";
      currentCallId.value = "accepted-call"; // Should ideally come from offer

      // Get user media
      localStream.value = await navigator.mediaDevices.getUserMedia({
        audio: true,
      });

      setupPeerConnection();

      const sdp = sessionStorage.getItem("pendingOfferSdp");
      if (!sdp) return;

      await peerConnection!.setRemoteDescription(
        new RTCSessionDescription({ type: "offer", sdp }),
      );

      const answer = await peerConnection!.createAnswer();
      await peerConnection!.setLocalDescription(answer);

      const answerDto: CallAnswerDto = {
        callId: currentCallId.value!,
        callerId: remoteUserId.value,
        responderId: user.value!.id,
        sdp: answer.sdp!,
        type: "answer",
      };

      sendMessage("/app/call.answer", answerDto);
    } catch (err) {
      console.error("Failed to accept call", err);
      resetCall();
    }
  };

  const declineCall = () => {
    if (remoteUserId.value) {
      const action: CallActionDto = {
        callId: "unknown",
        targetUserId: remoteUserId.value,
        action: "REJECT",
        type: "call-action",
      };
      sendMessage("/app/call.action", action);
    }
    resetCall();
  };

  const endCall = () => {
    if (remoteUserId.value) {
      const action: CallActionDto = {
        callId: currentCallId.value || "unknown",
        targetUserId: remoteUserId.value,
        action: "END",
        type: "call-action",
      };
      sendMessage("/app/call.action", action);
    }
    resetCall();
  };

  const handleAnswer = async (answer: CallAnswerDto) => {
    if (callState.value === "calling") {
      callState.value = "connected";
      await peerConnection!.setRemoteDescription(
        new RTCSessionDescription({ type: "answer", sdp: answer.sdp }),
      );
    }
  };

  const handleIceCandidate = async (dto: IceCandidateDto) => {
    if (peerConnection) {
      try {
        await peerConnection.addIceCandidate(
          new RTCIceCandidate({
            candidate: dto.candidate,
            sdpMid: dto.sdpMid,
            sdpMLineIndex: dto.sdpMLineIndex,
          }),
        );
      } catch (e) {
        console.error("Error adding ICE candidate", e);
      }
    }
  };

  const handleCallAction = (action: CallActionDto) => {
    if (
      action.action === "END" ||
      action.action === "REJECT" ||
      action.action === "BUSY"
    ) {
      // Show notification if needed
      if (action.action === "BUSY") {
        alert("User is busy");
      }
      resetCall();
    }
  };

  // Watch for login/logout to subscribe/unsubscribe
  watch(
    [user, isConnected],
    ([newUser, connected]) => {
      if (newUser && connected) {
        initialize();
      } else {
        if (callSubscription) {
          callSubscription.unsubscribe();
          callSubscription = null;
        }
        resetCall();
      }
    },
    { immediate: true },
  );

  return {
    callState,
    remoteUserId,
    localStream,
    remoteStream,
    isAudioEnabled,
    startCall,
    acceptCall,
    declineCall,
    endCall,
    toggleAudio,
  };
});
