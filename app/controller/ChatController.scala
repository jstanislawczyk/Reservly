package controller

import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import javax.inject.Inject
import model.ErrorMessage
import play.api.mvc._
import service.ChatService
import validation.ChatMessageValidatorValues

@Api("ChatController")
class ChatController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem, chatService: ChatService) extends AbstractController(cc) {
  
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Broadcast given message to global chat")
  ))
  def sendMessageToGlobalChat(): Action[AnyContent] = Action { implicit request =>
    val isMessageValid = chatService.handleGlobalChatMessageBroadcast(actorSystem, request)

    if (isMessageValid) {
      Ok("")
    } else {
      BadRequest(createErrorMessage)
    }
  }

  private def createErrorMessage: String = {
    val values = ChatMessageValidatorValues

    ErrorMessage.createErrorMessageJson(
      new ErrorMessage(
        "400",
        s"Size of the message should be between ${values.minimumMessageSize} and ${values.maximumMessageSize}"
      )
    )
  }
}