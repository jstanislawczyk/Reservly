package websocket

import actor.DirectChatActor
import actorRegister.DirectChatActorRegister
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
  def directChat(senderId: String, receiverId: String): WebSocket = WebSocket.accept[String, String] { implicit request =>

    val chatRoomId = buildDirectChatRoomId(senderId, receiverId)

    ActorFlow.actorRef(out => {
      registerNewActor(actorSystem, chatRoomId, out.path.toString)
      DirectChatActor.props(out, actorSystem)
    })
  }

  private def buildDirectChatRoomId(senderId: String, receiverId: String): String = {

    /*
      The direct chat room id is created using the identifiers of both participants in the conversation
      in a such way that both ids are ordered in alphabetical order and then they are concatenated
      E.g. some_id, scala_java.
    */

    if (senderId > receiverId) {
      s"${senderId}_$receiverId"
    } else {
      s"${receiverId}_$senderId"
    }
  }

  private def registerNewActor(actorSystem: ActorSystem, actorId: String, actorPath: String): Unit = {
    val directChatActorRegister = new DirectChatActorRegister(actorSystem)
    directChatActorRegister.registerNewActor(actorId, actorPath)
  }
}