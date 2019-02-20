package actorRegister

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object GlobalChatActorRegister {
  var actorRegister: scala.collection.mutable.ListBuffer[Int] = scala.collection.mutable.ListBuffer[Int]()
}

class GlobalChatActorRegister(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {
  def unregisterClosedSockets(): Unit = {

    val globalChatActorRegister  = GlobalChatActorRegister.actorRegister

    globalChatActorRegister.foreach(actorId => {
      actorSystem.actorSelection(s"/user/GlobalChat-$actorId").resolveOne(1.millis).onComplete {
        case Success(_) => ()
        case Failure(_) =>
          val index = globalChatActorRegister.indexOf(actorId)
          globalChatActorRegister.remove(index)
      }
    })
  }
}