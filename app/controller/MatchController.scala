package controller

import io.swagger.annotations._
import javax.inject.{Inject, Singleton}
import model.{Match, Player, ResponseMessage}
import play.api.libs.json.Json
import play.api.mvc._
import play.libs.F.Tuple
import security.Authorizer
import serializer.MatchJsonSerializer
import service.MatchService
import validation.`match`.MatchValidator

import scala.concurrent.{ExecutionContext, Future}

@Singleton
@Api("MatchController")
class MatchController @Inject()
  (matchService: MatchService, authorizer: Authorizer, cc: MessagesControllerComponents)
  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  @ApiOperation(
    value = "Get all matches",
    httpMethod = "GET",
    response = classOf[Seq[Match]]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returned list of all matches")
  ))
  def getAllMatches: Action[AnyContent] = Action.async { implicit request =>
    matchService
      .getAllMatches
      .map { matches =>
        Ok(Json.toJson(matches))
      }
  }

  @ApiOperation(
    value = "Get all matches with players",
    httpMethod = "GET",
    response = classOf[Seq[Tuple[Match, Player]]]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returned list of all matches with players")
  ))
  def getAllMatchesWithPlayers: Action[AnyContent] = Action.async { implicit request =>
    matchService
      .getAllMatchesWithPlayers
      .map { matches =>
        Ok(Json.toJson(matches))
      }
  }

  @ApiOperation(
    value = "Get match by id",
    httpMethod = "GET",
    response = classOf[Match]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returned match by id"),
    new ApiResponse(code = 404, message = "Missing match with given id")
  ))
  def getMatchById(@ApiParam("The id used to search for the match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    matchService
      .getMatchById(matchId)
      .map {
        case None =>
          NotFound(ResponseMessage.createResponseMessageAsJson("404",s"Match [id = $matchId] not found"))
        case Some(game) =>
          Ok(Json.toJson(game))
      }
  }

  @ApiOperation(
    value = "Get match by id with player",
    httpMethod = "GET",
    response = classOf[Tuple[Match, Player]]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returned match by id with player"),
    new ApiResponse(code = 404, message = "Missing match with given id")
  ))
  def getMatchByIdWithPlayer(@ApiParam("The id used to search for the match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    matchService
      .getMatchByIdWithPlayer(matchId)
      .map {
        case None =>
          NotFound(ResponseMessage.createResponseMessageAsJson("404",s"Match [id = $matchId] not found"))
        case Some(game) =>
          Ok(Json.toJson(game))
      }
  }

  @ApiOperation(
    value = "Save match with given player id",
    httpMethod = "POST",
    response = classOf[Match]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Match saved successfully"),
    new ApiResponse(code = 400, message = "Match validation failed"),
    new ApiResponse(code = 403, message = "Access forbidden")
  ))
  def saveMatch(): Action[AnyContent] = Action.async { implicit request =>
    val matchJson = request.body.asJson.get.toString()
    val matchToSave = MatchJsonSerializer.fromJson(matchJson)
    val playerId = getPlayerAuthId(request)

    if(isMatchValid(matchToSave)) {
      authorizer.authorizePlayerAccess(playerId).flatMap {
        case true =>
          matchService
            .saveMatch(matchToSave, playerId)
            .map {
              case null =>
                BadRequest(ResponseMessage.createResponseMessageAsJson("400", "Can't reserve more than one match"))
              case savedMatch: Match =>
                Ok(MatchJsonSerializer.toJson(savedMatch))
            }
        case false =>
          Future {Forbidden(ResponseMessage.createResponseMessageAsJson("403", "Access forbidden"))}
      }
    } else {
      Future {BadRequest(ResponseMessage.createResponseMessageAsJson("400","Match data invalid"))}
    }
  }

  @ApiOperation(
    value = "Delete match by id",
    httpMethod = "DELETE",
    response = classOf[String]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Match deleted successfully"),
    new ApiResponse(code = 403, message = "Access forbidden"),
    new ApiResponse(code = 404, message = "Missing match or wrong player with given match id")
  ))
  def deleteMatchById(@ApiParam("The id used to delete match") matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    val playerId = getPlayerAuthId(request)

    authorizer.authorizePlayerAccess(playerId).flatMap {
      case true =>
        matchService
          .deletePlayerMatchById(matchId, playerId)
          .map {
            case 0 =>
              NotFound(ResponseMessage.createResponseMessageAsJson("200", s"Match [id = $matchId] not found or wrong player id"))
            case 1 =>
              Ok(ResponseMessage.createResponseMessageAsJson("200", s"Match [id = $matchId] deleted"))
          }
      case false =>
        Future {Forbidden(ResponseMessage.createResponseMessageAsJson("403", "Access forbidden"))}
    }
  }

  private def getPlayerAuthId(request: MessagesRequest[AnyContent]): String = {
    request.headers.get("Auth-Id").getOrElse("0").toString
  }

  private def isMatchValid(game: Match): Boolean = {
    MatchValidator.validate(game)
  }
}