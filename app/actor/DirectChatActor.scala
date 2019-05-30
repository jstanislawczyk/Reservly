package actor

import actor_register.DirectChatActorRegister
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import service.PlayerService

import scala.concurrent.ExecutionContext

object DirectChatActor {
  def props(out: ActorRef, actorSystem: ActorSystem, userId: String, playerService: PlayerService)(implicit ec: ExecutionContext)
      = Props(new DirectChatActor(out, actorSystem, userId, playerService))
}

class DirectChatActor(out: ActorRef, actorSystem: ActorSystem, userId: String, playerService: PlayerService)(implicit ec: ExecutionContext) extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case _: Unit => ()
  }

  override def preStart(): Unit = {
    playerService
      .checkIfPlayerExists(userId)
      .foreach(exist => {
        if(!exist) {
          self ! PoisonPill
        }
      })
  }

  override def postStop(): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    directChatActorRegister.unregisterClosedSocket(out.path.toString)
  }
}
