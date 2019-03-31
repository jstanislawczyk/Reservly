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
    def id = column[String]("id", O.PrimaryKey)
    def displayName = column[String]("display_name")
    def email = column[String]("email")
    def photoUrl = column[String]("photo_url")
    def * = (id, displayName, email, photoUrl) <> ((Player.apply _).tupled, Player.unapply)
  }

  val players = TableQuery[PlayersTable]

  def getAllPlayers: Future[Seq[Player]] = db.run {
    players.result
  }

  def getPlayerById(playerId: String): Future[Option[Player]] = db.run {
    players
      .filter(_.id === playerId)
      .result
      .headOption
  }

  def savePlayer(player: Player): Future[Player] = db.run {
    (
      players.map(player =>
        (player.id, player.displayName, player.email, player.photoUrl)
      )

      returning players.map(_.id)
        into ((data, id) => Player(id, data._1, data._2, data._3))
    ) += (player.id, player.displayName, player.email, player.photoUrl)
  }

  def deletePlayerById(playerId: String): Future[Int] = db.run {
    players
      .filter(_.id === playerId)
      .delete
  }

  def checkIfPlayerExists(playerId : String): Future[Boolean] = db.run {
    players
      .filter(_.id === playerId)
      .exists
      .result
  }
}
