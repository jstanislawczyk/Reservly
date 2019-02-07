package repository


import java.sql.Timestamp

import javax.inject.{Inject, Singleton}
import models.Match
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MatchRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val playersRepository = new PlayerRepository(dbConfigProvider)

  import dbConfig._
  import profile.api._

  class MatchesTable(tag: Tag) extends Table[Match](tag, "matches") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def startDate = column[Timestamp]("start_date")
    def endDate = column[Timestamp]("end_date")
    def playerId = column[Long]("player_id")
    def * = (id, startDate, endDate, playerId) <> ((Match.apply _).tupled, Match.unapply)
    def player = foreignKey("player", playerId, playersRepository.players)(_.id)
  }

  val matches = TableQuery[MatchesTable]

  def getAllMatches(): Future[Seq[Match]] = db.run {
    matches.result
  }
}

