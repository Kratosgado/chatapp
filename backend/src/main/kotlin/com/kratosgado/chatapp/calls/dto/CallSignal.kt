package com.kratosgado.chatapp.calls.dto

data class CallOfferDto(
    val callId: String,
    val callerId: String,
    val targetUserId: String,
    val sdp: String,
    val type: String = "offer",
    val withVideo: Boolean = false,
)

data class CallAnswerDto(
    val callId: String,
    val callerId: String,
    val responderId: String,
    val sdp: String,
    val type: String = "answer",
)

data class IceCandidateDto(
    val callId: String,
    val targetUserId: String,
    val candidate: String,
    val sdpMid: String?,
    val sdpMLineIndex: Int?,
    val type: String = "ice-candidate",
)

data class CallActionDto(
    val callId: String,
    val targetUserId: String,
    val action: String, // REJECT, END, BUSY
    val type: String = "call-action",
)
