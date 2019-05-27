package websocket

import actor.DirectChatActor
import actor_register.DirectChatActorRegister
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import service.DirectChatService

import scala.concurrent.ExecutionContext

@Api("DirectChatSocket")
class DirectChatSocket @Inject()
(cc: MessagesControllerComponents, directChatService: DirectChatService)
(implicit ec: ExecutionContext, actorSystem: ActorSystem, mat: Materializer) extends MessagesAbstractController(cc) {

  @ApiOperation(
    value = "Open direct chat websocket connection",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Opens direct chat websocket connection for given receiver id and sender id")
  ))
  def directChat(userId: String): WebSocket = WebSocket.accept[String, String] { implicit request =>

    ActorFlow.actorRef(out => {
      registerNewActor(actorSystem, userId, out.path.toString)
      DirectChatActor.props(out, actorSystem, userId, directChatService)
    })
  }

  private def registerNewActor(actorSystem: ActorSystem, userId: String, actorPath: String): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    directChatActorRegister.registerNewActor(userId, actorPath)
  }
}