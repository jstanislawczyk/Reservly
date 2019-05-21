package service

import actorRegister.DirectChatActorRegister
import akka.actor.ActorSystem
import javax.inject.Inject
import model.DirectChatMessage
import play.api.mvc.{AnyContent, Request}
import repository.PlayerRepository
import serializer.DirectChatMessageJsonSerializer
import validation.direct_chat_message.DirectChatMessageValidator

import scala.concurrent.ExecutionContext

class DirectChatService @Inject() (playerRepository: PlayerRepository, actorSystem: ActorSystem) (implicit ec: ExecutionContext) {

  def sendDirectMessage(actorSystem: ActorSystem, request: Request[AnyContent], chatMembersIds: (String, String)): Boolean = {
    val chatMessage = DirectChatMessageJsonSerializer.fromJson(request.body.asJson.get.toString())
    val isMessageValid = DirectChatMessageValidator.validate(chatMessage)

    if(isMessageValid) {
      val directChatRoomId = buildDirectChatRoomId(chatMembersIds)
      sendMessage(actorSystem, chatMessage, directChatRoomId)
    }

    isMessageValid
  }

  def isGivenPlayerInvalid(userId: String): Boolean = {
    false
  }

  def areGivenPlayersInvalid(chatMembersIds: (String, String)): Boolean = {
    false
  }


  def buildDirectChatRoomId(chatMembersIds: (String, String)): String = {

    /*
      The direct chat room id is created using the identifiers of both participants in the conversation
      in a such way that both ids are ordered in alphabetical order and then they are concatenated
      E.g. some_id, scala_java.
    */

    var sortedChatMembersIds: (String, String) = chatMembersIds

    if (chatMembersIds._2 > chatMembersIds._1) {
      sortedChatMembersIds = chatMembersIds.swap
    }

    s"${sortedChatMembersIds._1}_${sortedChatMembersIds._2}"
  }

  private def sendMessage(actorSystem: ActorSystem, chatMessage: DirectChatMessage, directChatRoomId: String): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    val directChatMessageObjectAsJson = DirectChatMessageJsonSerializer.toJson(chatMessage)
    val chatMessageAsJson = s"[DirectChat: $directChatRoomId] $directChatMessageObjectAsJson"

    directChatActorRegister.sendMessage(chatMessageAsJson, chatMessageAsJson)
  }
}
