package actor

import actorRegister.DirectChatActorRegister
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import service.DirectChatService

object DirectChatActor {
  def props(out: ActorRef, actorSystem: ActorSystem, userId: String, directChatService: DirectChatService) = Props(new DirectChatActor(out, actorSystem, userId, directChatService))

  var userId: String = _
}

class DirectChatActor (out: ActorRef, actorSystem: ActorSystem, userId: String, directChatService: DirectChatService) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case _: Unit => ()
  }

  override def preStart(): Unit = {
    if(directChatService.isGivenPlayerInvalid(userId)) {
      self ! PoisonPill
    }
  }

  override def postStop(): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    directChatActorRegister.unregisterClosedSocket(out.path.toString)
  }
}
