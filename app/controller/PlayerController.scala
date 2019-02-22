package controller

import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import javax.inject._
import model.Player
import play.api.libs.json.Json
import play.api.mvc._
import service.PlayerService

import scala.concurrent.ExecutionContext

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
  def getPlayerById(playerId: Long): Action[AnyContent] = Action.async { implicit request =>
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

    playerService
      .savePlayer(player)
      .map(savedPlayer =>
        Ok(s"Player [$savedPlayer] saved")
      )
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Deletes player by id"),
    new ApiResponse(code = 404, message = "Returns information about missing player with given id")
  ))
  def deletePlayerById(playerId: Long): Action[AnyContent] = Action.async { implicit request =>
    playerService
      .deletePlayerById(playerId)
      .map {
        case 0 =>
          NotFound(s"Player [id = $playerId] not found")
        case 1 =>
          Ok(s"Player [id = $playerId] deleted")
      }
  }

  private def getPlayerFromRequest(request: MessagesRequest[AnyContent]): Player = {
    val playerJson = request.body.asJson.get.toString()
    Player.parsePlayerJson(playerJson)
  }
}