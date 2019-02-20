package actor

import actorRegister.GlobalChatActorRegister
import akka.actor._

import scala.concurrent.ExecutionContext

object GlobalChatActor {
  def props(out: ActorRef, actorSystem: ActorSystem)(implicit executionContext: ExecutionContext)
    = Props(new GlobalChatActor(out, actorSystem))
}

class GlobalChatActor (out: ActorRef, actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case msg: String =>
      out ! s"$msg"
  }

  override def postStop(): Unit = {
    val globalChatRegister = new GlobalChatActorRegister(actorSystem)
    globalChatRegister.unregisterClosedSockets()
  }
}