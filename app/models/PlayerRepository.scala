package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class PlayerRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class PlayersTable(tag: Tag) extends Table[Player](tag, "players") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("firstName")
    def lastName = column[String]("lastName")
    def * = (id, firstName, lastName) <> ((Player.apply _).tupled, Player.unapply)
  }

  private val players = TableQuery[PlayersTable]

  def list(): Future[Seq[Player]] = db.run {
    players.result
  }
}
