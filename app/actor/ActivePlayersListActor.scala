package actor

import actor_register.ActivePlayersRegister
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.concurrent.ExecutionContext

object ActivePlayersListActor {
  def props(out: ActorRef, actorSystem: ActorSystem, playerId: String)(implicit executionContext: ExecutionContext)
    = Props(new ActivePlayersListActor(out, actorSystem, playerId))
}

class ActivePlayersListActor(out: ActorRef, actorSystem: ActorSystem, playerId: String)
                    (implicit executionContext: ExecutionContext) extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case _: Unit => ()
  }

  override def postStop(): Unit = {
    val activePlayersRegister = new ActivePlayersRegister(actorSystem)
    activePlayersRegister.unregisterActivePlayer(playerId)
  }
}

