package actorRegister

import actorRegister.GlobalChatActorRegister.actorRegister
import akka.actor.ActorSystem


object GlobalChatActorRegister {
  var actorRegister: scala.collection.mutable.Map[Int, String] = scala.collection.mutable.Map[Int, String]()
}

class GlobalChatActorRegister(actorSystem: ActorSystem) {

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
}
