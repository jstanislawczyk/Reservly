package actor

import actorRegister.GlobalChatActorRegister
import akka.actor._

object GlobalChatActor {
  def props(out: ActorRef, actorSystem: ActorSystem) = Props(new GlobalChatActor(out, actorSystem))
}

class GlobalChatActor (out: ActorRef, actorSystem: ActorSystem) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case _: Unit => ()
  }

  override def postStop(): Unit = {
    val globalChatActorRegister = new GlobalChatActorRegister(actorSystem)
    globalChatActorRegister.unregisterClosedSocket(out.path.toString)
  }
}