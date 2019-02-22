package service

import actorRegister.GlobalChatActorRegister
import akka.actor.ActorSystem
import play.api.mvc.{AnyContent, Request}

class ChatService {

  def sendMessageToGlobalChat(actorSystem: ActorSystem, request: Request[AnyContent]): Unit = {
    broadcastMessage(actorSystem, request)
  }

  private def broadcastMessage(actorSystem: ActorSystem, request: Request[AnyContent]): Unit = {
    val message = request.body.asJson.get.toString()
    val globalChatActorRegister = new GlobalChatActorRegister(actorSystem)

    globalChatActorRegister.broadcastMessage(message)
  }
}
