package controller

import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import javax.inject._
import model.{ErrorMessage, Player}
import play.api.libs.json.Json
import play.api.mvc._
import serializer.{ErrorMessageJsonSerializer, PlayerJsonSerializer}
import service.PlayerService
import validation.player.PlayerValidator

import scala.concurrent.{ExecutionContext, Future}

@Singleton
@Api("PlayerController")
class PlayerController @Inject()
  (playerService: PlayerService, cc: MessagesControllerComponents )
  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns all players list")
  ))
  def getAllPlayers: Action[AnyContent] = Action.async { implicit request =>
    playerService
      .getAllPlayers
      .map(player =>
        Ok(Json.toJson(player))
      )
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns player by id"),
    new ApiResponse(code = 404, message = "Returns information about missing player with given id")
  ))
  def getPlayerById(@ApiParam("The id used to search for the player") playerId: Long): Action[AnyContent] = Action.async { implicit request =>
    playerService
      .getPlayerById(playerId)
      .map {
        case None =>
          NotFound(s"Player [id = $playerId] not found")
        case Some(player) =>
          Ok(Json.toJson(player))
      }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Saves given player. Player object is parsed from player request body (in JSON)")
  ))
  def savePlayer(): Action[AnyContent] = Action.async { implicit request =>
    val player = getPlayerFromRequest(request)

    if(isPlayerValid(player)) {
      playerService
        .savePlayer(player)
        .map(savedPlayer =>
          Ok(s"Player [$savedPlayer] saved")
        )
    } else {
      Future{BadRequest(createErrorMessage)}
    }
  }

  private def getPlayerFromRequest(request: MessagesRequest[AnyContent]): Player = {
    val playerJson = request.body.asJson.get.toString()
    PlayerJsonSerializer.fromJson(playerJson)
  }

  private def isPlayerValid(player: Player): Boolean = {
    PlayerValidator.validate(player)
  }

  private def createErrorMessage: String = {
    ErrorMessageJsonSerializer.toJson(
      new ErrorMessage(
        "400",
        s"Player data invalid"
      )
    )
  }
}