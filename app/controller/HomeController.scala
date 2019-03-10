package controller

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}

@Api("HomeController")
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  @ApiOperation(
    value = "Get 'home' response",
    httpMethod = "GET",
    response = classOf[String]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Default home endpoint. Returns 'home' message")
  ))
  def home(): Action[AnyContent] = Action {
    Ok("Home")
  }
}
