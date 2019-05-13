package repository

import java.sql.Timestamp

import javax.inject.{Inject, Singleton}
import model.DirectChatMessage
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class DirectChatMessageRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val playersRepository = new PlayerRepository(dbConfigProvider)

  import dbConfig._
  import profile.api._

  private class DirectChatMessagesTable(tag: Tag) extends Table[DirectChatMessage](tag, "direct_chat_messages") {
    def id = column[String]("id", O.PrimaryKey)
    def firstChatMemberId = column[String]("first_chat_member_id")
    def secondChatMemberId = column[String]("second_chat_member_id")
    def message = column[String]("message")
    def messageSendDate = column[Timestamp]("message_send_date")
    def * = (id, firstChatMemberId, secondChatMemberId, message, messageSendDate) <> ((DirectChatMessage.apply _).tupled, DirectChatMessage.unapply)
    def firstChatMember = foreignKey("player", firstChatMemberId, playersRepository.players)(_.id)
    def secondChatMember = foreignKey("player", secondChatMemberId, playersRepository.players)(_.id)
  }

  private val directChatMessages = TableQuery[DirectChatMessagesTable]
  private val players = playersRepository.players
}