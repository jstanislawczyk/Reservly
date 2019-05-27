package websocket

import actor.MatchListActor
import actor_register.MatchListActorRegister
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, _}
import repository.MatchRepository

import scala.concurrent.ExecutionContext

@Api("MatchSocket")
class MatchListSocket @Inject()
  (repository: MatchRepository, cc: MessagesControllerComponents, actorSystem: ActorSystem )
  (implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer) extends MessagesAbstractController(cc) {

  @ApiOperation(
    value = "Open match list websocket connection",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Opens websocket connection which returns last created matches in period of 60 seconds or on match delete/save")
  ))
  def getMatches: WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef { out => {
      registerNewActor(actorSystem, out.path.toString)
      MatchListActor.props(repository, out, actorSystem)
    }}
  }

  private def registerNewActor(actorSystem: ActorSystem, actorPath: String): Unit = {
    val matchListActorRegister = new MatchListActorRegister(actorSystem)
    matchListActorRegister.registerNewActor(actorPath)
  }
}
