package actorRegister

import actorRegister.DirectChatActorRegister.actorRegister
import akka.actor.ActorSystem

object DirectChatActorRegister {
  var actorRegister: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map[String, String]()
}

class DirectChatActorRegister(actorSystem: ActorSystem) {

  def unregisterClosedSocket(actorPathForDelete: String): Unit = {
    actorRegister.foreach(actor =>
      if(actor._2 == actorPathForDelete) {
        actorRegister.remove(actor._1)
      }
    )
  }

  def sendMessage(actorId: String, message: String): Unit = {
    actorRegister.foreach(actor => {
      if(actor._1 == actorId) {
        actorSystem.actorSelection(actor._2) ! message
      }
    })
  }

  def registerNewActor(actorId: String, actorPath: String): Unit = {
    if(actorWithGivenIdDoesNotExists(actorId)) {
      actorRegister.put(actorId, actorPath)
    }
  }

  private def actorWithGivenIdDoesNotExists(actorId: String): Boolean = {
    !actorRegister.contains(actorId)
  }
}
