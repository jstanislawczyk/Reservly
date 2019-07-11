package controller

import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import javax.inject.Inject
import model.{DirectChatMessage, ResponseMessage}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import serializer.DirectChatMessageJsonSerializer
import service.{DirectChatService, PlayerService}
import validation.chat_message.GlobalChatMessageValidatorValues
import validation.common.ParamsValidator
import validation.direct_chat_message.DirectChatMessageValidator

import scala.concurrent.{ExecutionContext, Future}

@Api("DirectChatController")
class DirectChatController @Inject()
  (cc: ControllerComponents, actorSystem: ActorSystem, directChatService: DirectChatService, playerService: PlayerService)
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
      playerService
        .checkIfPlayersPairExist(chatMembersIds)
        .flatMap {
          case false =>
            Future{NotFound(createNotFoundErrorMessage)}
          case true =>
            directChatService.sendDirectMessage(actorSystem, directChatMessage)
            Future{Ok("")}
        }
    }
  }

  @ApiOperation(
    value = "Get messages for given receiver id and sender id",
    httpMethod = "GET",
    response = classOf[Seq[DirectChatMessage]]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Messages list received"),
  ))
  def getMessagesByChatRoomId(chatRoomId: String, firstElementNumber: Int, numberOfElements: Int): Action[AnyContent] = Action.async { implicit request =>

    if(ParamsValidator.areParametersNegative(firstElementNumber, numberOfElements)) {
      Future{BadRequest(createInvalidParamsErrorMessage)}
    } else {
      directChatService
        .getMessagesByChatRoomId(chatRoomId, firstElementNumber, numberOfElements)
        .map(messages => {
          Ok(Json.toJson(messages))
        })
    }
  }

  private def createNotFoundErrorMessage: String = {
    ResponseMessage.createResponseMessageAsJson(
      "404",
      "At least one of given player ids is invalid"
    )
  }

  private def createInvalidParamsErrorMessage: String = {
    ResponseMessage.createResponseMessageAsJson(
      "400",
      "Params can not be negative"
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
