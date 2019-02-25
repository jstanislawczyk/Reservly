package model

import com.google.gson.Gson
import play.api.libs.json.{Json, OFormat}

case class ChatMessage(
  message: String
)

object ChatMessage {
  def createMessageJson(chatMessage: ChatMessage): String = {
    val gson = new Gson
    gson.toJson(chatMessage)
  }

  def parseErrorMessageJson(chatMessage: String): ChatMessage = {
    val gson = new Gson
    gson.fromJson(chatMessage, classOf[ChatMessage])
  }

  implicit val messageFormat: OFormat[ChatMessage] = Json.format[ChatMessage]
}
