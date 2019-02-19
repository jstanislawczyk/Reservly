package actor

import akka.actor._
import scala.concurrent.ExecutionContext

object GlobalChatActor {
  def props(out: ActorRef)(implicit executionContext: ExecutionContext) = Props(new GlobalChatActor(out))
}

class GlobalChatActor (out: ActorRef)(implicit executionContext: ExecutionContext) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case msg: String =>
      out ! s"$msg"
  }
}