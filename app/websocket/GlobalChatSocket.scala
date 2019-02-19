package websocket

import actor.GlobalChatActor
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
    ExtendedActorFlow.actorRef( out =>
      GlobalChatActor.props(out), 32, OverflowStrategy.dropNew, Some("GlobalChat")
    )
  }
}
