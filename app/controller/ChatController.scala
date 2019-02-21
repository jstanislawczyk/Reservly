package controller

import actorRegister.GlobalChatActorRegister
import akka.actor.ActorSystem
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

@Api("ChatController")
class ChatController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem) extends AbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Broadcast given message to global chat")
  ))
  def sendMessageToGlobalChat(): Action[AnyContent] = Action { implicit request =>
    val message = request.body.asJson.get.toString()
    val globalChatActorRegister = new GlobalChatActorRegister(actorSystem)

    globalChatActorRegister.broadcastMessage(message)

    Ok("")
  }
}
