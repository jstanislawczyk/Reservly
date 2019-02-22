package service

import akka.actor.ActorSystem
import javax.inject.Inject
import model.Player
import repository.PlayerRepository

import scala.concurrent.{ExecutionContext, Future}

class PlayerService @Inject() (playerRepository: PlayerRepository, actorSystem: ActorSystem)(implicit ec: ExecutionContext) {

  def getAllPlayers: Future[Seq[Player]] = {
    playerRepository.getAllPlayers()
  }

  def getPlayerById(playerId: Long): Future[Option[Player]] = {
    playerRepository.getPlayerById(playerId)
  }

  def savePlayer(player: Player): Future[Player] = {
    playerRepository.savePlayer(player)
  }

  def deletePlayerById(playerId: Long): Future[Int] = {
    playerRepository.deletePlayerById(playerId)
  }
}
