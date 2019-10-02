package websocket

import actor.ActivePlayersListActor
import actor_register.ActivePlayersRegister
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, WebSocket}

import scala.concurrent.ExecutionContext

@Api("ActivePlayersWebsocket")
class ActivePlayersSocket @Inject()
  (cc: MessagesControllerComponents, actorSystem: ActorSystem )
  (implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer) extends MessagesAbstractController(cc) {

  @ApiOperation(
    value = "Open active players websocket connection",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Opens websocket connection which returns logged in/out players")
  ))
  def getActivePlayers(playerId: String): WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef {out => {
      registerActivePlayersActor(actorSystem, out.path.toString, playerId)
      ActivePlayersListActor.props(out, actorSystem, playerId)
    }}
  }

  private def registerActivePlayersActor(system: ActorSystem, actorPath: String, playerId: String): Unit = {
    val activePlayersRegister = new ActivePlayersRegister(actorSystem)
    activePlayersRegister.registerActivePlayer(playerId, actorPath)
  }
}
