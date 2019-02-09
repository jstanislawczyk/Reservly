package controller

import javax.inject.{Inject, Singleton}
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
    repository.getMatchByIdWithPlayer(matchId).map { game =>
      if(game.nonEmpty) {
        Ok(Json.toJson(game))
      } else {
        NotFound(s"Match [id = $matchId] not found")
      }
    }
  }
}