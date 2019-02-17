package controller

import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import javax.inject.{Inject, Singleton}
import model.Match
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}
import repository.MatchRepository

import scala.concurrent.ExecutionContext

@Singleton
@Api("MatchController")
class MatchController @Inject()
  (repository: MatchRepository, cc: MessagesControllerComponents, actorSystem: ActorSystem)
  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns all matches list")
  ))
  def getAllMatches: Action[AnyContent] = Action.async { implicit request =>
    repository.getAllMatches().map { matches =>
      Ok(Json.toJson(matches))
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns all matches list with players")
  ))
  def getAllMatchesWithPlayers: Action[AnyContent] = Action.async { implicit request =>
    repository.getAllMatchesWithPlayers().map { matches =>
      Ok(Json.toJson(matches))
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns match by id"),
    new ApiResponse(code = 404, message = "Returns information about missing match with given id")
  ))
  def getMatchById(@ApiParam("The id used to search for the match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    repository.getMatchById(matchId).map {
      case None => NotFound(s"Match [id = $matchId] not found")
      case Some(game) => Ok(Json.toJson(game))
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns match by id with player"),
    new ApiResponse(code = 404, message = "Returns information about missing match with given id")
  ))
  def getMatchByIdWithPlayer(@ApiParam("The id used to search for the match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    repository.getMatchByIdWithPlayer(matchId).map {
      case None => NotFound(s"Match [id = $matchId] not found")
      case Some(game) => Ok(Json.toJson(game))
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Saves given match. Match object is parsed from match request body (in JSON)")
  ))
  def saveMatch(): Action[AnyContent] = Action.async { implicit request =>
    val matchJson = request.body.asJson.get.toString()
    val matchObject = Match.parseMatchJson(matchJson)

    repository.saveMatch(matchObject.playerId, matchObject.startDate, matchObject.endDate).map(game => {
      actorSystem.actorSelection("/user/*") ! ""
      Ok(s"Match [$game] saved")
    })
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Deletes match by id"),
    new ApiResponse(code = 404, message = "Returns information about missing match with given id")
  ))
  def deleteMatchById(@ApiParam("The id used to delete match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>

    repository.deleteMatchById(matchId).map {
      case 0 => NotFound(s"Match [id = $matchId] not found")
      case 1 =>
        actorSystem.actorSelection("/user/*") ! ""
        Ok(s"Match [id = $matchId] deleted")
    }
  }
}