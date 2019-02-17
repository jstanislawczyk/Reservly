package actor

import akka.actor._
import akka.actor.{ActorRef, ActorSystem}
import akka.parboiled2.RuleTrace.Named
import play.api.libs.json.Json
import repository.MatchRepository

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object MatchActor {
  def props(repository: MatchRepository, out: ActorRef, actorSystem: ActorSystem)(implicit executionContext: ExecutionContext)
    = Props(new MatchActor(repository, out, actorSystem))
}

class MatchActor(repository: MatchRepository, @Named("match_actor") out: ActorRef, actorSystem: ActorSystem)
                (implicit executionContext: ExecutionContext) extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case _: String =>
      actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 60.seconds) {
        repository.getAllMatchesWithPlayers().map { matches =>
          out ! s"${Json.toJson(matches)}"
        }
      }
  }
}
