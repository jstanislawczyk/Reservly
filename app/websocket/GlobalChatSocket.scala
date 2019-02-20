package websocket

import actor.GlobalChatActor
import actorRegister.GlobalChatActorRegister
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, _}

import scala.concurrent.ExecutionContext

@Api("GlobalChatSocket")
class GlobalChatSocket @Inject()
  (cc: MessagesControllerComponents)
  (implicit ec: ExecutionContext, actorSystem: ActorSystem, mat: Materializer) extends MessagesAbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Opens websocket connection which returns sent message to all connected clients")
  ))
  def globalChat(): WebSocket = WebSocket.accept[String, String] { _ =>

    val actorId = getFreeActorId

    ActorFlow.actorRef(out => {
        registerNewActor(actorId, out.path.toString)
        GlobalChatActor.props(out)
      }
    )
  }

  private def getFreeActorId: Int = {
    var actorId = 0
    var actorNotCreated = true

    while(actorNotCreated) {
      actorId += 1

      if(actorWithGivenIdDoesNotExists(actorId)) {
        actorNotCreated = false
      }
    }

    actorId
  }

  private def actorWithGivenIdDoesNotExists(actorId: Int): Boolean = {
    !GlobalChatActorRegister.actorRegister.contains(actorId)
  }

  private def registerNewActor(actorId: Int, actorPath: String): Unit = {
    GlobalChatActorRegister.actorRegister.put(actorId, actorPath)
  }
}
