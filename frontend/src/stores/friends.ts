import { defineStore } from "pinia";
import { ref } from "vue";
import {
  getFriends,
  getPendingRequests,
  respondToRequest,
  sendFriendRequest,
  removeFriend,
} from "@/services/friends";
import type { FriendRequest, User } from "@/types";

export const useFriendsStore = defineStore("friends", () => {
  const friends = ref<User[]>([]);
  const requests = ref<FriendRequest[]>([]);

  const fetchFriends = async () => {
    friends.value = await getFriends();
  };

  const fetchRequests = async () => {
    requests.value = await getPendingRequests();
  };

  const acceptRequest = async (requestId: string) => {
    await respondToRequest(requestId, true);
    await fetchFriends();
    await fetchRequests();
  };

  const rejectRequest = async (requestId: string) => {
    await respondToRequest(requestId, false);
    await fetchRequests();
  };

  const sendRequest = async (receiverId: string) => {
    await sendFriendRequest(receiverId);
    await fetchRequests(); // Wait, requests sent won't be in pending requests for sender (usually).
    // So this is fine.
  };

  const remove = async (friendId: string) => {
    await removeFriend(friendId);
    await fetchFriends();
  };

  return {
    friends,
    requests,
    fetchFriends,
    fetchRequests,
    acceptRequest,
    rejectRequest,
    sendRequest,
    remove,
  };
});
