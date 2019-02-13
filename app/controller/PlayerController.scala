package controller

import io.swagger.annotations.{ApiResponse, ApiResponses}
import javax.inject._
import model.Player
import play.api.libs.json.Json
import play.api.mvc._
import repository.PlayerRepository

import scala.concurrent.ExecutionContext

@Singleton
class PlayerController @Inject()
  (repository: PlayerRepository, cc: MessagesControllerComponents )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns all players list")
  ))
  def getAllPlayers: Action[AnyContent] = Action.async { implicit request =>
    repository.getAllPlayers().map { players =>
      Ok(Json.toJson(players))
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns player by id"),
    new ApiResponse(code = 404, message = "Returns information about missing player with given id")
  ))
  def getPlayerById(playerId: Long): Action[AnyContent] = Action.async { implicit request =>
    repository.getPlayerById(playerId).map {
      case None => NotFound(s"Player [id = $playerId] not found")
      case Some(player) => Ok(Json.toJson(player))
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Saves given player. Player object is parsed from player request body (in JSON)")
  ))
  def savePlayer(): Action[AnyContent] = Action.async { implicit request =>
    val playerJson = request.body.asJson.get.toString()
    val player = Player.parsePlayerJson(playerJson)

    repository.savePlayer(player.firstName, player.lastName).map(player =>
      Ok(s"Player [$player] saved")
    )
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Deletes player by id"),
    new ApiResponse(code = 404, message = "Returns information about missing player with given id")
  ))
  def deletePlayerById(playerId: Long): Action[AnyContent] = Action.async { implicit request =>
    repository.deletePlayerById(playerId).map {
      case 0 => NotFound(s"Player [id = $playerId] not found")
      case 1 => Ok(s"Player [id = $playerId] deleted")
    }
  }
}