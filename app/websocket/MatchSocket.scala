package websocket

import actor.MatchActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, _}
import repository.MatchRepository

import scala.concurrent.ExecutionContext

@Api("MatchSocket")
class MatchSocket @Inject()
  (repository: MatchRepository, cc: MessagesControllerComponents, actorSystem: ActorSystem )
  (implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer) extends MessagesAbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Opens websocket connection and returns last created matches in period of 60 seconds, or on match delete/save")
  ))
  def getMatches(): WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef { out => {
        MatchActor.props(repository, out, actorSystem)
      }
    }
  }
}
