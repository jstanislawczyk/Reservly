package serializer

import com.google.gson.Gson
import model.Player

object PlayerJsonSerializer extends JsonSerializer[Player] {

  private val gson = new Gson

  def toJson(player: Player): String = {
    gson.toJson(player)
  }

  def fromJson(playerJson: String): Player = {
    gson.fromJson(playerJson, classOf[Player])
  }
}