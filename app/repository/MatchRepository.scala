package repository


import java.sql.Timestamp
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
    def startDate = column[Timestamp]("start_date")
    def endDate = column[Timestamp]("end_date")
    def gameName = column[String]("game_name")
    def playerId = column[String]("player_id")
    def * = (id, startDate, endDate, gameName, playerId) <> ((Match.apply _).tupled, Match.unapply)
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
        .on(_.playerId === _.id)

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
        .on(_.playerId === _.id)
        .filter(game => game._1.id === matchId)

    db.run {
      getMatchTableWithPlayerTableQuery.result.headOption
    }
  }

  def saveMatch(matchToSave: Match, playerId: String): Future[Match] = {

    val getReservedMatchesQuery =
      sql"""
        SELECT COUNT(*) FROM matches
        WHERE end_date >= current_timestamp
        AND player_id = $playerId
      """

    db.run {
      getReservedMatchesQuery.as[Int].head
    }.flatMap(reservedMatches => {
      if(reservedMatches == 0) {
        db.run {
          (
            matches.map(game =>
              (game.startDate, game.endDate, game.gameName, game.playerId)
            )

            returning matches.map(_.id)
              into ((data, id) => Match(id, data._1, data._2, data._3, data._4))

            ) += (matchToSave.startDate, matchToSave.endDate, matchToSave.gameName, playerId)
        }
      } else {
        Future{ null }
      }
    })
  }

  def deletePlayerMatchById(matchId: Long, playerId: String): Future[Int] = db.run {
    matches
      .filter(_.id === matchId)
      .filter(_.playerId === playerId)
      .delete
  }

  def countMatchesInGivenTimePeriod(startMatchDate: Timestamp, endMatchDate: Timestamp): Future[Int] = {
    val countMatchesInGivenTimePeriod =
      sql"""
        SELECT COUNT(*) FROM matches
        WHERE start_date >= $startMatchDate AND start_date < $endMatchDate
        OR end_date > $startMatchDate AND end_date <= $endMatchDate
      """

    db.run {
      countMatchesInGivenTimePeriod.as[Int].head
    }
  }
}

