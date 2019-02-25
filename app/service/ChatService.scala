package service

import actorRegister.GlobalChatActorRegister
import akka.actor.ActorSystem
import model.ChatMessage
import play.api.mvc.{AnyContent, Request}
import validation.ChatMessageValidator

class ChatService {

  def handleGlobalChatMessageBroadcast(actorSystem: ActorSystem, request: Request[AnyContent]): Boolean = {
    val chatMessage = ChatMessage.parseErrorMessageJson(request.body.asJson.get.toString())
    val isMessageValid = ChatMessageValidator.validate(chatMessage)

    if(isMessageValid) {
      broadcastMessage(actorSystem, chatMessage)
    }

    isMessageValid
  }

  private def broadcastMessage(actorSystem: ActorSystem, chatMessage: ChatMessage): Unit = {
    val globalChatActorRegister = new GlobalChatActorRegister(actorSystem)
    val chatMessageAsJson = ChatMessage.createMessageJson(chatMessage)

    globalChatActorRegister.broadcastMessage(chatMessageAsJson)
  }
}
