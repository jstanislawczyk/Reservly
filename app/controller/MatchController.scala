package controller

import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import javax.inject.{Inject, Singleton}
import model.{ErrorMessage, Match}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}
import security.Authorizer
import serializer.{ErrorMessageJsonSerializer, MatchJsonSerializer}
import service.MatchService
import validation.`match`.MatchValidator

import scala.concurrent.{ExecutionContext, Future}

@Singleton
@Api("MatchController")
class MatchController @Inject()
  (matchService: MatchService, authorizer: Authorizer, cc: MessagesControllerComponents)
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
    val matchToSave = MatchJsonSerializer.fromJson(matchJson)
    val playerId = request.headers.get("Auth-Id").getOrElse("0").toLong

    if(isMatchValid(matchToSave)) {
      authorizer.authorizePlayerAccess(playerId).flatMap {
        case true =>
          matchService
            .saveMatch(matchToSave)
            .map(savedMatch => {
              Ok(s"Match [$savedMatch] saved")
            })
        case false =>
          Future {BadRequest(createErrorMessage("403"))}
      }
    } else {
      Future {BadRequest(createErrorMessage("400"))}
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

  private def createErrorMessage(httpCode: String): String = {
    ErrorMessageJsonSerializer.toJson(
      new ErrorMessage(
        httpCode,
        getMessageByCode(httpCode)
      )
    )
  }

  private def getMessageByCode(httpCode: String): String = {
    httpCode match {
      case "400" =>
        "Match data invalid"
      case "403" =>
        "Access forbidden"
    }
  }
}