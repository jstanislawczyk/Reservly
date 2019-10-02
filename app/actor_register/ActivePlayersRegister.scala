package actor_register

import actor_register.ActivePlayersRegister.activePlayersRegister
import akka.actor.ActorSystem
import com.google.gson.Gson
import helper.{WebSocketResponseBuilder, WebSocketResponseType}
import helper.WebSocketResponseType.WebSocketResponseType

object ActivePlayersRegister {
  var activePlayersRegister: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map[String, String]()
}

class ActivePlayersRegister(actorSystem: ActorSystem) {

  def registerActivePlayer(playerId: String, actorPath: String): Unit = {
    if(activePlayerNotRegistered(playerId)) {
      activePlayersRegister += (playerId -> actorPath)
      broadcastMessage(buildMessage(WebSocketResponseType.ACTIVE_USER_REGISTER, playerId))
    }
  }

  def unregisterActivePlayer(playerId: String): Unit = {
    activePlayersRegister -= playerId
    broadcastMessage(buildMessage(WebSocketResponseType.ACTIVE_USER_UNREGISTER, playerId))
  }

  def getActivePlayers: collection.Set[String] = {
    activePlayersRegister.keySet
  }

  private def activePlayerNotRegistered(playerId: String): Boolean = {
    !activePlayersRegister.contains(playerId)
  }

  private def broadcastMessage(message: String): Unit = {
    activePlayersRegister.foreach(actor =>
      actorSystem.actorSelection(actor._2) ! message
    )
  }

  private def buildMessage(responseType: WebSocketResponseType, message: String): String = {
    val gson = new Gson

    WebSocketResponseBuilder.buildWebsocketResponse(responseType, gson.toJson(message))
  }
}
