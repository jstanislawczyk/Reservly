package controller

import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import model.ResponseMessage
import play.api.mvc._
import service.GlobalChatService
import validation.chatMessage.ChatMessageValidatorValues

@Api("GlobalChatController")
class GlobalChatController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem, chatService: GlobalChatService) extends AbstractController(cc) {

  @ApiOperation(
    value = "Broadcast given message to global chat",
    httpMethod = "POST",
    response = classOf[String]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Message broadcast success"),
    new ApiResponse(code = 400, message = "Message validation failed")
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

    ResponseMessage.createResponseMessageAsJson(
      "400",
      s"Size of the message should be between ${values.minimumMessageSize} and ${values.maximumMessageSize}"
    )
  }
}