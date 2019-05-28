package serializer

import com.google.gson.{Gson, GsonBuilder}
import model.Match

object MatchJsonSerializer extends JsonSerializer[Match] {

  val gson: Gson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm")
      .create()

  override def toJson(game: Match): String = {
    gson.toJson(game)
  }

  override def fromJson(gameJson: String): Match = {
    gson.fromJson(gameJson, classOf[Match])
  }
}
