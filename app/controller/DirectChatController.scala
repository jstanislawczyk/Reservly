package controller

import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import model.ResponseMessage
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import service.DirectChatService
import validation.chatMessage.GlobalChatMessageValidatorValues

@Api("DirectChatController")
class DirectChatController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem, directChatService: DirectChatService) extends AbstractController(cc)  {

  @ApiOperation(
    value = "Broadcast given message to global chat",
    httpMethod = "POST",
    response = classOf[String]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Message broadcast success"),
    new ApiResponse(code = 400, message = "Message validation failed")
  ))
  def sendDirectMessage(senderId: String, receiverId: String): Action[AnyContent] = Action { implicit request =>

    val chatMembersIds = (senderId, receiverId)

    if(directChatService.areGivenPlayersInvalid(chatMembersIds)) {
      NotFound(createNotFoundErrorMessage)
    }

    val isMessageValid = directChatService.sendDirectMessage(actorSystem, request, chatMembersIds)

    if (isMessageValid) {
      Ok("")
    } else {
      BadRequest(createBadRequestErrorMessage)
    }
  }

  private def createNotFoundErrorMessage: String = {
    ResponseMessage.createResponseMessageAsJson(
      "404",
      "At least one of given player ids is invalid"
    )
  }

  private def createBadRequestErrorMessage: String = {
    val values = GlobalChatMessageValidatorValues

    ResponseMessage.createResponseMessageAsJson(
      "400",
      s"Size of the message should be between ${values.minimumMessageSize} and ${values.maximumMessageSize}"
    )
  }
}
