package controllers

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
}