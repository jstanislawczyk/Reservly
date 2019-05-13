package websocket

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, WebSocket}

import scala.concurrent.ExecutionContext

@Api("DirectChatSocket")
class DirectChatSocket @Inject()
(cc: MessagesControllerComponents)
(implicit ec: ExecutionContext, actorSystem: ActorSystem, mat: Materializer) extends MessagesAbstractController(cc) {

  @ApiOperation(
    value = "Open direct chat websocket connection",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Opens direct chat websocket connection for given chat id")
  ))
  def directChat(id: String): WebSocket = WebSocket.accept[String, String] { _ =>

  }

  private def registerNewActor(actorSystem: ActorSystem, actorPath: String): Unit = {

  }
}