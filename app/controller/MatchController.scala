package controller

import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import javax.inject.{Inject, Singleton}
import model.{ErrorMessage, Match}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}
import service.MatchService
import validation.`match`.MatchValidator

import scala.concurrent.{ExecutionContext, Future}

@Singleton
@Api("MatchController")
class MatchController @Inject() (matchService: MatchService, cc: MessagesControllerComponents)
  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns all matches list")
  ))
  def getAllMatches: Action[AnyContent] = Action.async { implicit request =>
    matchService
      .getAllMatches
      .map { matches =>
        Ok(Json.toJson(matches))
      }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns all matches list with players")
  ))
  def getAllMatchesWithPlayers: Action[AnyContent] = Action.async { implicit request =>
    matchService
      .getAllMatchesWithPlayers
      .map { matches =>
        Ok(Json.toJson(matches))
      }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns match by id"),
    new ApiResponse(code = 404, message = "Returns information about missing match with given id")
  ))
  def getMatchById(@ApiParam("The id used to search for the match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    matchService
      .getMatchById(matchId)
      .map {
        case None =>
          NotFound(s"Match [id = $matchId] not found")
        case Some(game) =>
          Ok(Json.toJson(game))
      }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returns match by id with player"),
    new ApiResponse(code = 404, message = "Returns information about missing match with given id")
  ))
  def getMatchByIdWithPlayer(@ApiParam("The id used to search for the match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    matchService
      .getMatchByIdWithPlayer(matchId)
      .map {
        case None =>
          NotFound(s"Match [id = $matchId] not found")
        case Some(game) =>
          Ok(Json.toJson(game))
      }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Saves given match. Match object is parsed from match request body (in JSON)")
  ))
  def saveMatch(): Action[AnyContent] = Action.async { implicit request =>
    val matchJson = request.body.asJson.get.toString()
    val matchToSave = Match.parseMatchJson(matchJson)

    if(isMatchValid(matchToSave)) {
      matchService
        .saveMatch(matchToSave)
        .map(savedMatch => {
          Ok(s"Match [$savedMatch] saved")
        })
    } else {
      Future {BadRequest(createErrorMessage)}
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Deletes match by id"),
    new ApiResponse(code = 404, message = "Returns information about missing match with given id")
  ))
  def deleteMatchById(@ApiParam("The id used to delete match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    matchService
      .deleteMatchById(matchId)
      .map {
        case 0 =>
          NotFound(s"Match [id = $matchId] not found")
        case 1 =>
          Ok(s"Match [id = $matchId] deleted")
      }
  }

  private def isMatchValid(game: Match): Boolean = {
    MatchValidator.validate(game)
  }

  private def createErrorMessage: String = {
    ErrorMessage.createErrorMessageJson(
      new ErrorMessage(
        "400",
        s"Match data invalid"
      )
    )
  }
}