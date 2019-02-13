package controller

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}

@Api
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

    @ApiResponses(Array(
      new ApiResponse(code = 200, message = "Home endpoint -> ok")
    ))
    def home(): Action[AnyContent] = Action {
      Ok("Home")
    }
}
