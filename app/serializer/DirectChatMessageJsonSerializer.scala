package serializer

import com.google.gson.{Gson, GsonBuilder}
import model.DirectChatMessage

object DirectChatMessageJsonSerializer extends JsonSerializer[DirectChatMessage] {

  protected val gson: Gson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      .create()

  override def toJson(chatMessage: DirectChatMessage): String = {
    gson.toJson(chatMessage)
  }

  override def fromJson(chatMessageJson: String): DirectChatMessage = {
    gson.fromJson(chatMessageJson, classOf[DirectChatMessage])
  }
}
