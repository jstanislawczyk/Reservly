package actor

import actorRegister.GlobalChatActorRegister
import akka.actor._

import scala.concurrent.ExecutionContext

object GlobalChatActor {
  def props(out: ActorRef)
    = Props(new GlobalChatActor(out))
}

class GlobalChatActor (out: ActorRef) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case msg: String =>
      out ! msg
  }

  override def postStop(): Unit = {
    GlobalChatActorRegister.unregisterClosedSocket(out.path.toString)
  }
}