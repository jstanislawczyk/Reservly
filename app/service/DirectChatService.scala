package service

import java.sql.Timestamp

import actor_register.DirectChatActorRegister
import akka.actor.ActorSystem
import helper.{WebSocketResponseBuilder, WebSocketResponseType}
import javax.inject.Inject
import model.DirectChatMessage
import repository.{DirectChatMessageRepository, PlayerRepository}
import serializer.DirectChatMessageJsonSerializer
import validation.direct_chat_message.DirectChatMessageValidator

import scala.concurrent.{ExecutionContext, Future}

class DirectChatService @Inject()
  (playerRepository: PlayerRepository, directChatMessageRepository: DirectChatMessageRepository, actorSystem: ActorSystem) (implicit ec: ExecutionContext) {

  def sendDirectMessage(actorSystem: ActorSystem, chatMessage: DirectChatMessage, directChatReceiverId: String): Boolean = {
    val isMessageValid = DirectChatMessageValidator.validate(chatMessage)

    if(isMessageValid) {
      chatMessage.messageSendDate = new Timestamp(System.currentTimeMillis())

      sendMessage(actorSystem, chatMessage, directChatReceiverId)
      directChatMessageRepository.saveDirectChatMessage(chatMessage)
    }

    isMessageValid
  }

  def isGivenPlayerValid(userId: String): Future[Boolean] = {
    playerRepository.checkIfPlayerExists(userId)
  }

  def areGivenPlayersInvalid(chatMembersIds: (String, String)): Boolean = {
    false
  }

  def buildResponseJson(directChatMessageObjectAsJson: String): String = {
    val responseType = WebSocketResponseType.DIRECT_CHAT

    WebSocketResponseBuilder.buildWebsocketResponse(responseType, directChatMessageObjectAsJson)
  }

  private def sendMessage(actorSystem: ActorSystem, chatMessage: DirectChatMessage, directChatReceiverId: String): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    val directChatMessageObjectAsJson = DirectChatMessageJsonSerializer.toJson(chatMessage)
    val chatMessageAsJson = buildResponseJson(directChatMessageObjectAsJson)

    directChatActorRegister.sendMessage(directChatReceiverId, chatMessageAsJson)
  }
}
