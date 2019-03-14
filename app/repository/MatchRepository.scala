package repository


import java.sql.Timestamp

import helper.MatchStatus
import javax.inject.{Inject, Singleton}
import model.{Match, Player}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MatchRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val playersRepository = new PlayerRepository(dbConfigProvider)

  import dbConfig._
  import profile.api._

  private class MatchesTable(tag: Tag) extends Table[Match](tag, "matches") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def matchStatus = column[String]("match_status")
    def startDate = column[Timestamp]("start_date")
    def endDate = column[Timestamp]("end_date")
    def playerId = column[Long]("player_id")
    def * = (id, matchStatus, startDate, endDate, playerId) <> ((Match.apply _).tupled, Match.unapply)
    def player = foreignKey("player", playerId, playersRepository.players)(_.id)
  }

  private val matches = TableQuery[MatchesTable]
  private val players = playersRepository.players

  def getAllMatches: Future[Seq[Match]] = db.run {
    matches.result
  }

  def getAllMatchesWithPlayers: Future[Seq[(Match, Player)]] = {
    val getMatchTableWithPlayerTableQuery =
      matches
        .join(players)
        .on(_.id === _.id)

    db.run {
      getMatchTableWithPlayerTableQuery.result
    }
  }

  def getMatchById(matchId: Long): Future[Option[Match]] = db.run {
    matches.filter(_.id === matchId).result.headOption
  }

  def getMatchByIdWithPlayer(matchId: Long): Future[Option[(Match, Player)]] = {
    val getMatchTableWithPlayerTableQuery =
      matches
        .join(players)
        .on(_.id === _.id)
        .filter(game => game._1.id === matchId)

    db.run {
      getMatchTableWithPlayerTableQuery.result.headOption
    }
  }

  def saveMatch(matchToSave: Match, playerId: Long): Future[Match] = {

    val getReservedMatchesQuery =
      matches
        .filter(game => game.playerId === playerId)
        .filter(game => game.matchStatus === MatchStatus.RESERVED.toString)
        .result

    db.run {
      getReservedMatchesQuery
    }.flatMap(reservedMatches => {
      if(reservedMatches.isEmpty) {
        db.run {
          (
            matches.map(game =>
              (game.matchStatus, game.startDate, game.endDate, game.playerId)
            )

            returning matches.map(_.id)
              into ((data, id) => Match(id, data._1, data._2, data._3, data._4))

            ) += (MatchStatus.RESERVED.toString, matchToSave.startDate, matchToSave.endDate, playerId)
        }
      } else {
        Future{ null }
      }
    })
  }

  def deletePlayerMatchById(matchId: Long, playerId: Long): Future[Int] = db.run {
    matches
      .filter(_.id === matchId)
      .filter(_.playerId === playerId)
      .delete
  }
}

