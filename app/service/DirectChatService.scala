package service

import akka.actor.ActorSystem
import javax.inject.Inject
import repository.PlayerRepository

import scala.concurrent.ExecutionContext

class DirectChatService @Inject() (playerRepository: PlayerRepository, actorSystem: ActorSystem) (implicit ec: ExecutionContext) {

  def areGivenPlayersValid(senderId: String, receiverId: String): Boolean = {
    true
  }
}
