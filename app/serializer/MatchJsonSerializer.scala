package serializer

import com.google.gson.GsonBuilder
import model.Match

object MatchJsonSerializer extends JsonSerializer[Match] {
  private val gson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mmZ")
      .create()

  def toJson(game: Match): String = {
    gson.toJson(game)
  }

  def fromJson(gameJson: String): Match = {
    gson.fromJson(gameJson, classOf[Match])
  }
}
