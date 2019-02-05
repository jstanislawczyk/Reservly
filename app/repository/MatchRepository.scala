package repository


import java.sql.Timestamp

import javax.inject.{Inject, Singleton}
import models.Match
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MatchRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class MatchesTable(tag: Tag) extends Table[Match](tag, "matches") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def startDate = column[Timestamp]("startDate")
    def endDate = column[Timestamp]("endDate")
    def * = (id, startDate, endDate) <> ((Match.apply _).tupled, Match.unapply)
  }

  private val matches = TableQuery[MatchesTable]

  def getAllMatches(): Future[Seq[Match]] = db.run {
    matches.result
  }
}

