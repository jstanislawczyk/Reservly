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
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def chatRoomId = column[String]("chat_room_id")
    def receiverId = column[String]("receiver_id")
    def senderId = column[String]("sender_id")
    def message = column[String]("message")
    def messageSendDate = column[Timestamp]("message_send_date")
    def * = (id, chatRoomId, receiverId, senderId, message, messageSendDate) <> ((DirectChatMessage.apply _).tupled, DirectChatMessage.unapply)
    def receiver = foreignKey("player", receiverId, playersRepository.players)(_.id)
    def sender = foreignKey("player", senderId, playersRepository.players)(_.id)
  }

  private val directChatMessages = TableQuery[DirectChatMessagesTable]
  private val players = playersRepository.players

  def saveMessage(directChatMessageToSave: DirectChatMessage): Future[DirectChatMessage] = {
    
    db.run {
      (
        directChatMessages.map(message =>
          (message.chatRoomId, message.receiverId, message.senderId, message.message, message.messageSendDate)
        )

        returning directChatMessages.map(_.id)
        into ((data, id) => DirectChatMessage(id, data._1, data._2, data._3, data._4, data._5))

      ) += (
        directChatMessageToSave.chatRoomId, directChatMessageToSave.receiverId, directChatMessageToSave.senderId,
        directChatMessageToSave.message, directChatMessageToSave.messageSendDate
      )
    }
  }

  def getMessagesByChatRoomId(chatRoomId: String): Future[Seq[DirectChatMessage]] = {
    db.run {
      directChatMessages
        .filter(_.chatRoomId === chatRoomId)
        .result
    }
  }
}