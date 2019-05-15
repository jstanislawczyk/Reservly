package actor

import actorRegister.DirectChatActorRegister
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object DirectChatActor {
  def props(out: ActorRef, actorSystem: ActorSystem) = Props(new GlobalChatActor(out, actorSystem))
}

class DirectChatActor (out: ActorRef, actorSystem: ActorSystem) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case _: Unit => ()
  }

  override def postStop(): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    directChatActorRegister.unregisterClosedSocket(out.path.toString)
  }
}
