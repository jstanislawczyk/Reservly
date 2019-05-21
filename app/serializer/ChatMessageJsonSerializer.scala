package serializer

import com.google.gson.Gson
import model.GlobalChatMessage

object ChatMessageJsonSerializer extends JsonSerializer[GlobalChatMessage] {
  private val gson = new Gson

  def toJson(chatMessage: GlobalChatMessage): String = {
    gson.toJson(chatMessage)
  }

  def fromJson(chatMessageJson: String): GlobalChatMessage = {
    gson.fromJson(chatMessageJson, classOf[GlobalChatMessage])
  }
}
