package repository

import javax.inject.{Inject, Singleton}
import models.Player
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlayerRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PlayersTable(tag: Tag) extends Table[Player](tag, "players") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def * = (id, firstName, lastName) <> ((Player.apply _).tupled, Player.unapply)
  }

  val players = TableQuery[PlayersTable]

  def getAllPlayers(): Future[Seq[Player]] = db.run {
    players.result
  }
}
