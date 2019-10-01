package controller

import actor_register.ActivePlayersRegister
import io.swagger.annotations._
import javax.inject._
import model.{Player, ResponseMessage}
import play.api.libs.json.Json
import play.api.mvc._
import serializer.PlayerJsonSerializer
import service.PlayerService
import validation.player.PlayerValidator

import scala.concurrent.{ExecutionContext, Future}

@Singleton
@Api("PlayerController")
class PlayerController @Inject()
  (playerService: PlayerService, cc: MessagesControllerComponents )
  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  @ApiOperation(
    value = "Get all players",
    httpMethod = "GET",
    response = classOf[Seq[Player]]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returned list of all players")
  ))
  def getAllPlayers: Action[AnyContent] = Action.async { implicit request =>
    playerService
      .getAllPlayers
      .map(player =>
        Ok(Json.toJson(player))
      )
  }

  @ApiOperation(
    value = "Get single player by id",
    httpMethod = "GET",
    response = classOf[Player]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returned player by id"),
    new ApiResponse(code = 404, message = "Missing player with given id")
  ))
  def getPlayerById(@ApiParam("The id used to search for the player") playerId: String): Action[AnyContent] = Action.async { implicit request =>
    playerService
      .getPlayerById(playerId)
      .map {
        case None =>
          NotFound(ResponseMessage.createResponseMessageAsJson("404", s"Player [id = $playerId] not found"))
        case Some(player) =>
          Ok(Json.toJson(player))
      }
  }

  @ApiOperation(
    value = "Save player received in request body or updates if ID already exists in database",
    httpMethod = "POST",
    response = classOf[Player]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Given player saved"),
    new ApiResponse(code = 400, message = "Player validation failed")
  ))
  def savePlayer(): Action[AnyContent] = Action.async { implicit request =>
    val player = getPlayerFromRequest(request)

    if(isPlayerValid(player)) {
      playerService
        .savePlayer(player)
        .map(_ =>
          Ok(PlayerJsonSerializer.toJson(player))
        )
    } else {
      Future{
        BadRequest(
          ResponseMessage.createResponseMessageAsJson("400","Player data invalid")
        )
      }
    }
  }

  @ApiOperation(
    value = "Get active players",
    httpMethod = "GET",
    response = classOf[Seq[Player]]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Returned list of all active players")
  ))
  def getActivePlayers: Action[AnyContent] = Action.async { implicit request =>
    val activePlayersRegister = new ActivePlayersRegister
    val activePlayersList = activePlayersRegister.getActivePlayers

    playerService
      .getPlayersWithGivenIds(activePlayersList)
      .map(player =>
        Ok(Json.toJson(player))
      )
  }

  private def getPlayerFromRequest(request: MessagesRequest[AnyContent]): Player = {
    val playerJson = request.body.asJson.get.toString()
    PlayerJsonSerializer.fromJson(playerJson)
  }

  private def isPlayerValid(player: Player): Boolean = {
    PlayerValidator.validate(player)
  }
}