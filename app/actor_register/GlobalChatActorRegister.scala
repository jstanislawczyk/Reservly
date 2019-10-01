package actor_register

import actor_register.GlobalChatActorRegister.actorRegister
import akka.actor.ActorSystem

object GlobalChatActorRegister {
  var actorRegister: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map[String, String]()
}

class GlobalChatActorRegister(actorSystem: ActorSystem) {

  def unregisterClosedSocket(actorPathForDelete: String): String = {

    var unregisteredPlayerId = ""

    actorRegister.foreach(actor =>
      if(actor._2 == actorPathForDelete) {
        actorRegister.remove(actor._1)
        unregisteredPlayerId = actor._1
      }
    )

    unregisteredPlayerId
  }

  def broadcastMessage(message: String): Unit = {
    actorRegister.foreach(actor =>
      actorSystem.actorSelection(actor._2) ! message
    )
  }

  def registerNewActor(actorPath: String, playerId: String): Unit = {
    if(!playerId.isBlank) {
      actorRegister.put(playerId, actorPath)
    }
  }
}
