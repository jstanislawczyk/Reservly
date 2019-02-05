package controllers

import javax.inject._
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
}