package serializer

import com.google.gson.Gson
import model.GlobalChatMessage

object ChatMessageJsonSerializer extends JsonSerializer[GlobalChatMessage] {

  private val gson = new Gson

  override def toJson(globalChatMessage: GlobalChatMessage): String = {
    gson.toJson(globalChatMessage)
  }

  override def fromJson(json: String): GlobalChatMessage = {
    gson.fromJson(json, classOf[GlobalChatMessage])
  }
}
