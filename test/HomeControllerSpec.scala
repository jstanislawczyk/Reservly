import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.mvc
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class HomeControllerSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(app, FakeRequest(GET, "/wrong")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the home endpoint response" in new WithApplication {
      val home: Future[mvc.Result] = route(app, FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
    }
  }
}
