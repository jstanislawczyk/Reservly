package serializer

import com.google.gson.{Gson, GsonBuilder}
import model.Match

object MatchJsonSerializer {
  private val gson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
      .create()

  def toJson(game: Match): String = {
    gson.toJson(game)
  }

  def fromJson(gameJson: String): Match = {
    gson.fromJson(gameJson, classOf[Match])
  }
}
