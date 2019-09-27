package actor_register

import actor_register.MatchListActorRegister.actorRegister
import akka.actor.ActorSystem

object MatchListActorRegister {
  var actorRegister: scala.collection.mutable.Map[Int, String] = scala.collection.mutable.Map[Int, String]()
}

class MatchListActorRegister(actorSystem: ActorSystem) {
  def unregisterClosedSocket(actorPathForDelete: String): Unit = {
    actorRegister.foreach(actor =>
      if(actor._2 == actorPathForDelete) {
        actorRegister.remove(actor._1)
      }
    )
  }

  def broadcastMessage(message: String): Unit = {
    actorRegister.foreach(actor =>
      actorSystem.actorSelection(actor._2) ! message
    )
  }

  def registerNewActor(actorPath: String): Unit = {
    val freeActorId = getFreeActorId
    actorRegister.put(freeActorId, actorPath)
  }

  private def getFreeActorId: Int = {
    var actorId = 0
    var actorNotCreated = true

    while(actorNotCreated) {
      actorId += 1

      if(actorWithGivenIdDoesNotExists(actorId)) {
        actorNotCreated = false
      }
    }

    actorId
  }

  private def actorWithGivenIdDoesNotExists(actorId: Int): Boolean = {
    !actorRegister.contains(actorId)
  }
}
