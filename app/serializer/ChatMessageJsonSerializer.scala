package serializer

import com.google.gson.Gson
import model.ChatMessage

object ChatMessageJsonSerializer extends JsonSerializer[ChatMessage] {
  private val gson = new Gson

  def toJson(chatMessage: ChatMessage): String = {
    gson.toJson(chatMessage)
  }

  def fromJson(chatMessageJson: String): ChatMessage = {
    gson.fromJson(chatMessageJson, classOf[ChatMessage])
  }
}
