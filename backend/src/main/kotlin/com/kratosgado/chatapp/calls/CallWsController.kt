package com.kratosgado.chatapp.calls

import com.kratosgado.chatapp.calls.dto.*
import com.kratosgado.chatapp.users.User
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class CallWsController(
    private val messagingTemplate: SimpMessagingTemplate
) {

    @MessageMapping("/call.offer")
    fun sendOffer(@Payload offer: CallOfferDto, principal: Principal) {
        val user = (principal as UsernamePasswordAuthenticationToken).principal as User
        val secureOffer = offer.copy(callerId = user.id!!)
        
        messagingTemplate.convertAndSendToUser(
            offer.targetUserId,
            "/queue/calls",
            secureOffer
        )
    }

    @MessageMapping("/call.answer")
    fun sendAnswer(@Payload answer: CallAnswerDto, principal: Principal) {
        val user = (principal as UsernamePasswordAuthenticationToken).principal as User
        val secureAnswer = answer.copy(responderId = user.id!!)
        
        messagingTemplate.convertAndSendToUser(
            answer.callerId,
            "/queue/calls",
            secureAnswer
        )
    }

    @MessageMapping("/call.ice")
    fun sendIceCandidate(@Payload candidate: IceCandidateDto, principal: Principal) {
        messagingTemplate.convertAndSendToUser(
            candidate.targetUserId,
            "/queue/calls",
            candidate
        )
    }

    @MessageMapping("/call.action")
    fun sendAction(@Payload action: CallActionDto, principal: Principal) {
        messagingTemplate.convertAndSendToUser(
            action.targetUserId,
            "/queue/calls",
            action
        )
    }
}
