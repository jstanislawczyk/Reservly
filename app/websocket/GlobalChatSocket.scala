package websocket

import actor.GlobalChatActor
import actor_register.{ActivePlayersRegister, GlobalChatActorRegister}
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, _}

import scala.concurrent.ExecutionContext

@Api("GlobalChatSocket")
class GlobalChatSocket @Inject()
(cc: MessagesControllerComponents)
(implicit ec: ExecutionContext, actorSystem: ActorSystem, mat: Materializer) extends MessagesAbstractController(cc) {

  @ApiOperation(
    value = "Open global chat websocket connection",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Opens global chat websocket connection")
  ))
  def globalChat(playerId: String): WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef(out => {
      registerNewActor(actorSystem, out.path.toString, playerId)
      registerActivePlayer(playerId)

      GlobalChatActor.props(out, actorSystem)
    })
  }

  private def registerNewActor(actorSystem: ActorSystem, actorPath: String, playerId: String): Unit = {
    val globalChatActorRegister = new GlobalChatActorRegister(actorSystem)
    globalChatActorRegister.registerNewActor(actorPath, playerId)
  }

  private def registerActivePlayer(playerId: String): Unit = {
    val activePlayersRegister = new ActivePlayersRegister
    activePlayersRegister.registerActivePlayer(playerId)
  }
}
