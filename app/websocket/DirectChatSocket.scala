package websocket

import actor.DirectChatActor
import actorRegister.DirectChatActorRegister
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc._

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
  def directChat(id: String): WebSocket = WebSocket.accept[String, String] { implicit request =>
    ActorFlow.actorRef(out => {
      registerNewActor(actorSystem, id, out.path.toString)
      DirectChatActor.props(out, actorSystem)
    })
  }

  private def registerNewActor(actorSystem: ActorSystem, actorId: String, actorPath: String): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    directChatActorRegister.registerNewActor(actorId, actorPath)
  }

  private def getPlayerAuthId(request: MessagesRequest[AnyContent]): String = {
    request.headers.get("Auth-Id").getOrElse("0").toString
  }
}