package repository

import javax.inject.{Inject, Singleton}
import model.Player
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlayerRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  protected class PlayersTable(tag: Tag) extends Table[Player](tag, "players") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def * = (id, firstName, lastName) <> ((Player.apply _).tupled, Player.unapply)
  }

  val players = TableQuery[PlayersTable]

  def getAllPlayers(): Future[Seq[Player]] = db.run {
    players.result
  }

  def getPlayerById(playerId: Long): Future[Option[Player]] = db.run {
    players
      .filter(_.id === playerId)
      .result
      .headOption
  }

  def savePlayer(player: Player): Future[Player] = db.run {
    (
      players.map(player =>
        (player.firstName, player.lastName)
      )

      returning players.map(_.id)
        into ((data, id) => Player(id, data._1, data._2))
    ) += (player.firstName, player.lastName)
  }

  def deletePlayerById(playerId: Long): Future[Int] = db.run {
    players
      .filter(_.id === playerId)
      .delete
  }
}
