package controller

import javax.inject.{Inject, Singleton}
import model.{Match, Player}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}
import repository.MatchRepository

import scala.concurrent.ExecutionContext

@Singleton
class MatchController @Inject()
  (repository: MatchRepository, cc: MessagesControllerComponents )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def getAllMatches: Action[AnyContent] = Action.async { implicit request =>
    repository.getAllMatches().map { matches =>
      Ok(Json.toJson(matches))
    }
  }

  def getAllMatchesWithPlayers: Action[AnyContent] = Action.async { implicit request =>
    repository.getAllMatchesWithPlayers().map { matches =>
      Ok(Json.toJson(matches))
    }
  }

  def getMatchById(matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    repository.getMatchById(matchId).map {
      case None => NotFound(s"Match [id = $matchId] not found")
      case Some(game) => Ok(Json.toJson(game))
    }
  }

  def getMatchByIdWithPlayer(matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    repository.getMatchByIdWithPlayer(matchId).map {
      case None => NotFound(s"Match [id = $matchId] not found")
      case Some(game) => Ok(Json.toJson(game))
    }
  }

  def saveMatch(): Action[AnyContent] = Action.async { implicit request =>
    val matchJson = request.body.asJson.get.toString()
    val matchObject = Match.parseMatchJson(matchJson)

    repository.saveMatch(matchObject.playerId, matchObject.startDate, matchObject.endDate).map(game =>
      Ok(s"Match [$game] saved")
    )
  }

  def deleteMatchById(matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    repository.deleteMatchById(matchId).map {
      case 0 => NotFound(s"Match [id = $matchId] not found")
      case 1 => Ok(s"Match [id = $matchId] deleted")
    }
  }
}