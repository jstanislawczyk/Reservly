package service

import actorRegister.DirectChatActorRegister
import akka.actor.ActorSystem
import javax.inject.Inject
import model.DirectChatMessage
import repository.PlayerRepository
import serializer.DirectChatMessageJsonSerializer
import validation.direct_chat_message.DirectChatMessageValidator

import scala.concurrent.{ExecutionContext, Future}

class DirectChatService @Inject() (playerRepository: PlayerRepository, actorSystem: ActorSystem) (implicit ec: ExecutionContext) {

  def sendDirectMessage(actorSystem: ActorSystem, chatMessage: DirectChatMessage, directChatReceiverId: String): Boolean = {
    val isMessageValid = DirectChatMessageValidator.validate(chatMessage)

    if(isMessageValid) {
      sendMessage(actorSystem, chatMessage, directChatReceiverId)
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
    s"{type: \042DirectChat\042, object: $directChatMessageObjectAsJson}"
  }

  private def sendMessage(actorSystem: ActorSystem, chatMessage: DirectChatMessage, directChatReceiverId: String): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    val directChatMessageObjectAsJson = DirectChatMessageJsonSerializer.toJson(chatMessage)
    val chatMessageAsJson = buildResponseJson(directChatMessageObjectAsJson)

    directChatActorRegister.sendMessage(directChatReceiverId, chatMessageAsJson)
  }
}
