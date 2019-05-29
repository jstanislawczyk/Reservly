package controller

import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import model.ResponseMessage
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import serializer.DirectChatMessageJsonSerializer
import service.DirectChatService
import validation.chat_message.GlobalChatMessageValidatorValues
import validation.direct_chat_message.DirectChatMessageValidator

import scala.concurrent.{ExecutionContext, Future}

@Api("DirectChatController")
class DirectChatController @Inject()
  (cc: ControllerComponents, actorSystem: ActorSystem, directChatService: DirectChatService)
  (implicit ec: ExecutionContext) extends AbstractController(cc)  {

  @ApiOperation(
    value = "Send direct message",
    httpMethod = "POST",
    response = classOf[String]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Message sent successfully"),
    new ApiResponse(code = 400, message = "Message validation failed"),
    new ApiResponse(code = 404, message = "Given users are not valid")
  ))
  def sendDirectMessage(): Action[AnyContent] = Action.async { implicit request =>

    val directChatMessage = DirectChatMessageJsonSerializer.fromJson(request.body.asJson.get.toString())
    val chatMembersIds = (directChatMessage.receiverId, directChatMessage.senderId)
    val isMessageNotValid = !DirectChatMessageValidator.validate(directChatMessage)

    if(isMessageNotValid) {
      Future{BadRequest(createValidationFailErrorMessage)}
    } else {
      directChatService
        .areGivenPlayersInvalid(chatMembersIds)
        .flatMap {
          case false =>
            Future{NotFound(createNotFoundErrorMessage)}
          case true =>
            directChatService.sendDirectMessage(actorSystem, directChatMessage)
            Future{Ok("")}
        }
    }
  }

  private def createNotFoundErrorMessage: String = {
    ResponseMessage.createResponseMessageAsJson(
      "404",
      "At least one of given player ids is invalid"
    )
  }

  private def createValidationFailErrorMessage: String = {
    val values = GlobalChatMessageValidatorValues

    ResponseMessage.createResponseMessageAsJson(
      "400",
      s"Size of the message should be between ${values.minimumMessageSize} and ${values.maximumMessageSize}"
    )
  }
}
