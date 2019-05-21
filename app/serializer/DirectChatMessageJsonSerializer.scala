package serializer

import com.google.gson.Gson
import model.DirectChatMessage

object DirectChatMessageJsonSerializer extends JsonSerializer[DirectChatMessage] {
  private val gson = new Gson

  def toJson(chatMessage: DirectChatMessage): String = {
    gson.toJson(chatMessage)
  }

  def fromJson(chatMessageJson: String): DirectChatMessage = {
    gson.fromJson(chatMessageJson, classOf[DirectChatMessage])
  }
}
