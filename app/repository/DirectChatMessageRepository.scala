package repository

import java.sql.Timestamp

import javax.inject.{Inject, Singleton}
import model.DirectChatMessage
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DirectChatMessageRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val playersRepository = new PlayerRepository(dbConfigProvider)

  import dbConfig._
  import profile.api._

  private class DirectChatMessagesTable(tag: Tag) extends Table[DirectChatMessage](tag, "direct_chat_messages") {
    def id = column[Long]("id", O.PrimaryKey)
    def receiverId = column[String]("receiver_id")
    def senderId = column[String]("sender_id")
    def message = column[String]("message")
    def messageSendDate = column[Timestamp]("message_send_date")
    def * = (id, senderId, receiverId, message, messageSendDate) <> ((DirectChatMessage.apply _).tupled, DirectChatMessage.unapply)
    def receiver = foreignKey("player", receiverId, playersRepository.players)(_.id)
    def sender = foreignKey("player", senderId, playersRepository.players)(_.id)
  }

  private val directChatMessages = TableQuery[DirectChatMessagesTable]
  private val players = playersRepository.players

  def saveDirectChatMessage(directChatMessageToSave: DirectChatMessage): Future[DirectChatMessage] = {
    db.run {
      (
        directChatMessages.map(chatMessage =>
          (chatMessage.receiverId, chatMessage.senderId, chatMessage.message, chatMessage.messageSendDate)
        )

          returning directChatMessages.map(_.id)
          into ((data, id) => DirectChatMessage(id, data._1, data._2, data._3, data._4))

        ) += (directChatMessageToSave.receiverId, directChatMessageToSave.senderId, directChatMessageToSave.message, directChatMessageToSave.messageSendDate)
    }
  }
}