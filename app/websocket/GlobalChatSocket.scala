package websocket

import actor.GlobalChatActor
import actorRegister.GlobalChatActorRegister
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import helper.ExtendedActorFlow
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Api("GlobalChatSocket")
class GlobalChatSocket @Inject()
  (cc: MessagesControllerComponents)
  (implicit ec: ExecutionContext, actorSystem: ActorSystem, mat: Materializer) extends MessagesAbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Opens websocket connection and returns sent message")
  ))
  def globalChat(): WebSocket = WebSocket.accept[String, String] { _ =>

    val actorId = getFreeActorId

    ExtendedActorFlow.actorRef(out =>
      GlobalChatActor.props(out, actorSystem), 32, OverflowStrategy.dropNew, Some(s"GlobalChat-$actorId")
    )
  }

  private def getFreeActorId: Int = {
    var actorId = 0
    var actorNotCreated = true

    while(actorNotCreated) {
      actorId += 1

      if(actorWithGivenIdDoesNotExists(actorId)) {
        registerNewActor(actorId)
        actorNotCreated = false
      }
    }

    actorId
  }

  private def actorWithGivenIdDoesNotExists(actorId: Int): Boolean = {
    !GlobalChatActorRegister.actorRegister.contains(actorId)
  }

  private def registerNewActor(actorId: Int): Unit = {
    GlobalChatActorRegister.actorRegister += actorId
  }
}
