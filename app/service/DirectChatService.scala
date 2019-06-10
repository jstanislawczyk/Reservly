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

    setupChatMessage(chatMessage)

    directChatMessageRepository
      .saveMessage(chatMessage)
      .foreach {
        case savedMessage: DirectChatMessage =>
          sendMessage(actorSystem, savedMessage)
        case null => ()
      }
  }

  def getMessagesByChatRoomId(chatRoomId: String): Future[Seq[DirectChatMessage]] = {
    directChatMessageRepository.getMessagesByChatRoomId(chatRoomId)
  }

  def buildResponseJson(directChatMessageObjectAsJson: String): String = {
    val responseType = WebSocketResponseType.DIRECT_CHAT

    WebSocketResponseBuilder.buildWebsocketResponse(responseType, directChatMessageObjectAsJson)
  }

  private def setupChatMessage(chatMessage: DirectChatMessage): Unit = {
    chatMessage.messageSendDate = new Timestamp(System.currentTimeMillis())
    chatMessage.chatRoomId = buildDirectChatRoomId(chatMessage.receiverId, chatMessage.senderId)
  }

  private def buildDirectChatRoomId(receiverId: String, senderId: String): String = {
    /*
      The direct chat room id is created using the identifiers of both participants in the conversation
      in a such way that both ids are ordered in alphabetical order and then they are concatenated
      E.g. some_id, scala_java.
    */

    if (senderId > receiverId) {
      s"${senderId}_$receiverId"
    } else {
      s"${receiverId}_$senderId"
    }
  }

  private def sendMessage(actorSystem: ActorSystem, chatMessage: DirectChatMessage): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    val directChatMessageObjectAsJson = DirectChatMessageJsonSerializer.toJson(chatMessage)
    val chatMessageAsJson = buildResponseJson(directChatMessageObjectAsJson)

    directChatActorRegister.sendMessage(chatMessage.receiverId, chatMessageAsJson)
    directChatActorRegister.sendMessage(chatMessage.senderId, chatMessageAsJson)
  }
}
