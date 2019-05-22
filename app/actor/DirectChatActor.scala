package actor

import actorRegister.DirectChatActorRegister
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import service.DirectChatService

import scala.concurrent.ExecutionContext

object DirectChatActor {
  def props(out: ActorRef, actorSystem: ActorSystem, userId: String, directChatService: DirectChatService)(implicit ec: ExecutionContext)
      = Props(new DirectChatActor(out, actorSystem, userId, directChatService))
}

class DirectChatActor (out: ActorRef, actorSystem: ActorSystem, userId: String, directChatService: DirectChatService)(implicit ec: ExecutionContext) extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case _: Unit => ()
  }

  override def preStart(): Unit = {
    self ! PoisonPill
  }

  override def postStop(): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    directChatActorRegister.unregisterClosedSocket(out.path.toString)
  }
}
