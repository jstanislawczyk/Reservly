package service

import java.sql.Timestamp

import actor_register.MatchListActorRegister
import akka.actor.ActorSystem
import helper.UpdateType
import helper.UpdateType.UpdateType
import javax.inject.Inject
import model.{Match, Player}
import play.api.libs.json.Json
import repository.MatchRepository

import scala.concurrent.{ExecutionContext, Future}

class MatchService @Inject() (matchRepository: MatchRepository, actorSystem: ActorSystem) (implicit ec: ExecutionContext) {

  def getAllMatches: Future[Seq[Match]] = {
    matchRepository.getAllMatches
  }

  def getAllMatchesWithPlayers: Future[Seq[(Match, Player)]] =  {
    matchRepository.getAllMatchesWithPlayers
  }

  def getMatchById(matchId: Long): Future[Option[Match]] = {
    matchRepository.getMatchById(matchId)
  }

  def getMatchByIdWithPlayer(matchId: Long): Future[Option[(Match, Player)]] = {
    matchRepository.getMatchByIdWithPlayer(matchId)
  }

  def saveMatch(matchToSave: Match, playerId: String): Future[Match] = {
    val savedMatch = matchRepository.saveMatch(matchToSave, playerId)

    savedMatch.map(savedMatch =>
      broadcastMessageWithUpdateForMatchesList(UpdateType.SAVED, Json.toJson(savedMatch).toString, actorSystem)
    )

    savedMatch
  }

  def deletePlayerMatchById(matchId: Long, playerId: String): Future[Int] = {
    val deleteMatchStatus = matchRepository.deletePlayerMatchById(matchId, playerId)

    deleteMatchStatus.map {
      case 1 =>
        broadcastMessageWithUpdateForMatchesList(UpdateType.DELETED, matchId.toString, actorSystem)
    }

    deleteMatchStatus
  }

  def countMatchesInGivenTimePeriod(startMatchDate: Timestamp, endMatchDate: Timestamp): Future[Int] = {
    matchRepository.countMatchesInGivenTimePeriod(startMatchDate, endMatchDate)
  }

  private def broadcastMessageWithUpdateForMatchesList(typeOfUpdate: UpdateType, message: String, actorSystem: ActorSystem): Unit = {
    val matchListActorRegister = new MatchListActorRegister(actorSystem)

    matchListActorRegister.broadcastMessage(s"[${typeOfUpdate.toString}] $message")
  }
}
