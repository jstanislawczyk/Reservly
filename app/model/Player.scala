package model

import com.google.gson.Gson
import play.api.libs.json._

case class Player(
  id: Long,
  firstName: String,
  lastName: String
)

object Player {

  def parsePlayerJson(playerJson: String): Player = {
    val gson = new Gson
    gson.fromJson(playerJson, classOf[Player])
  }

  implicit val personFormat: OFormat[Player] = Json.format[Player]
}
