package controller

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

    def home(): Action[AnyContent] = Action {
      Ok("Home")
    }
}
