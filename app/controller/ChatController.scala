package controller

import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.mvc._
import service.ChatService

@Api("ChatController")
class ChatController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem) extends AbstractController(cc) {

  private val chatService = new ChatService()

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Broadcast given message to global chat")
  ))
  def sendMessageToGlobalChat(): Action[AnyContent] = Action { implicit request =>
    chatService.sendMessageToGlobalChat(actorSystem, request)

    Ok("")
  }
}