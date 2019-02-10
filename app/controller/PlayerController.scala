package controller

import javax.inject._
import model.Player
import play.api.libs.json.Json
import play.api.mvc._
import repository.PlayerRepository

import scala.concurrent.ExecutionContext

@Singleton
class PlayerController @Inject()
  (repository: PlayerRepository, cc: MessagesControllerComponents )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def getAllPlayers: Action[AnyContent] = Action.async { implicit request =>
    repository.getAllPlayers().map { players =>
      Ok(Json.toJson(players))
    }
  }

  def getPlayerById(playerId: Long): Action[AnyContent] = Action.async { implicit request =>
    repository.getPlayerById(playerId).map {
      case None => NotFound(s"Player [id = $playerId] not found")
      case Some(player) => Ok(Json.toJson(player))
    }
  }

  def savePlayer(): Action[AnyContent] = Action.async { implicit request =>
    val playerJson = request.body.asJson.get.toString()
    val player = Player.parsePlayerJson(playerJson)

    repository.savePlayer(player.firstName, player.lastName).map(player =>
      Ok(s"Player [$player] saved")
    )
  }
}