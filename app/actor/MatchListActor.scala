package actor

import akka.actor._
import akka.actor.{ActorRef, ActorSystem}
import play.api.libs.json.Json
import repository.MatchRepository

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object MatchListActor {
  def props(repository: MatchRepository, out: ActorRef, actorSystem: ActorSystem)(implicit executionContext: ExecutionContext)
    = Props(new MatchListActor(repository, out, actorSystem))
}

class MatchListActor(repository: MatchRepository, out: ActorRef, actorSystem: ActorSystem)
                    (implicit executionContext: ExecutionContext) extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case _: String =>
      idleConnectionBreakPrevent()
  }

  def idleConnectionBreakPrevent(): Unit = {
    actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 60.seconds) {
      out ! ""
    }
  }
}
