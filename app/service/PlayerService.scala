package service

import actor_register.ActivePlayersRegister
import akka.actor.ActorSystem
import javax.inject.Inject
import model.Player
import repository.PlayerRepository

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class PlayerService @Inject() (playerRepository: PlayerRepository, activePlayersRegister: ActivePlayersRegister, actorSystem: ActorSystem)(implicit ec: ExecutionContext) {

  def getAllPlayers: Future[Seq[Player]] = {
    playerRepository.getAllPlayers
  }

  def getPlayersWithGivenIds(playersIds: ListBuffer[String]): Future[Seq[Player]] = {
    playerRepository.getPlayersWithGivenIds(playersIds)
  }

  def getPlayerById(playerId: String): Future[Option[Player]] = {
    playerRepository.getPlayerById(playerId)
  }

  def savePlayer(player: Player): Future[Int] = {
    playerRepository.savePlayer(player)
  }

  def deletePlayerById(playerId: String): Future[Int] = {
    playerRepository.deletePlayerById(playerId)
  }

  def checkIfPlayerExists(playerId: String): Future[Boolean] = {
    playerRepository.checkIfPlayerExists(playerId)
  }

  def checkIfPlayersPairExist(chatMembersIds: (String, String)): Future[Boolean] = {
    playerRepository.checkIfPlayersPairExist(chatMembersIds)
  }
}
