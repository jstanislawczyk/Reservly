package controller

import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import model.ResponseMessage
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import serializer.DirectChatMessageJsonSerializer
import service.DirectChatService
import validation.chat_message.GlobalChatMessageValidatorValues

@Api("DirectChatController")
class DirectChatController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem, directChatService: DirectChatService) extends AbstractController(cc)  {

  @ApiOperation(
    value = "Send direct message",
    httpMethod = "POST",
    response = classOf[String]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Message sent successfully"),
    new ApiResponse(code = 400, message = "Message validation failed")
  ))
  def sendDirectMessage(): Action[AnyContent] = Action { implicit request =>

    val directChatMessage = DirectChatMessageJsonSerializer.fromJson(request.body.asJson.get.toString())
    val chatMembersIds = (directChatMessage.senderId, directChatMessage.receiverId)

    if(directChatService.areGivenPlayersInvalid(chatMembersIds)) {
      NotFound(createNotFoundErrorMessage)
    }

    val messageSentSuccessfully = directChatService.sendDirectMessage(actorSystem, directChatMessage, directChatMessage.receiverId)

    if (messageSentSuccessfully) {
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
