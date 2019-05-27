package serializer

import com.google.gson.GsonBuilder
import model.DirectChatMessage

object DirectChatMessageJsonSerializer extends JsonSerializer[DirectChatMessage] {

  private val gson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      .create()

  def toJson(chatMessage: DirectChatMessage): String = {
    gson.toJson(chatMessage)
  }

  def fromJson(chatMessageJson: String): DirectChatMessage = {
    gson.fromJson(chatMessageJson, classOf[DirectChatMessage])
  }
}
