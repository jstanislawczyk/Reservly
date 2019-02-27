package serializer

import com.google.gson.Gson
import model.Match

object MatchJsonSerializer {
  private val gson = new Gson

  def toJson(game: Match): String = {
    gson.toJson(game)
  }

  def fromJson(gameJson: String): Match = {
    gson.fromJson(gameJson, classOf[Match])
  }
}
