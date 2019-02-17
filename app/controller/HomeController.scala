package controller

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}

@Api("HomeController")
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Default home endpoint. Returns 'home' message")
  ))
  def home(): Action[AnyContent] = Action {
    Ok("Home")
  }
}
