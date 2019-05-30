package service

import java.sql.Timestamp

import actor_register.DirectChatActorRegister
import akka.actor.ActorSystem
import helper.{WebSocketResponseBuilder, WebSocketResponseType}
import javax.inject.Inject
import model.DirectChatMessage
import repository.{DirectChatMessageRepository, PlayerRepository}
import serializer.DirectChatMessageJsonSerializer

import scala.concurrent.{ExecutionContext, Future}
class DirectChatService @Inject()
  (playerRepository: PlayerRepository, directChatMessageRepository: DirectChatMessageRepository, actorSystem: ActorSystem) (implicit ec: ExecutionContext) {

  def sendDirectMessage(actorSystem: ActorSystem, chatMessage: DirectChatMessage): Unit = {

    chatMessage.messageSendDate = new Timestamp(System.currentTimeMillis())

    directChatMessageRepository.saveMessage(chatMessage)
    sendMessage(actorSystem, chatMessage)
  }

  def getMessagesForGivenReceiverAndSender(receiverId: String, senderId: String): Future[Seq[DirectChatMessage]] = {
    directChatMessageRepository.getMessagesForGivenReceiverAndSender(receiverId, senderId)
  }

  def buildResponseJson(directChatMessageObjectAsJson: String): String = {
    val responseType = WebSocketResponseType.DIRECT_CHAT

    WebSocketResponseBuilder.buildWebsocketResponse(responseType, directChatMessageObjectAsJson)
  }

  private def sendMessage(actorSystem: ActorSystem, chatMessage: DirectChatMessage): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    val directChatMessageObjectAsJson = DirectChatMessageJsonSerializer.toJson(chatMessage)
    val chatMessageAsJson = buildResponseJson(directChatMessageObjectAsJson)

    directChatActorRegister.sendMessage(chatMessage.receiverId, chatMessageAsJson)
  }
}
