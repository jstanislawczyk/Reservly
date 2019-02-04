package models

import play.api.libs.json._

case class Player(id: Long, firstName: String, lastName: String)

object Player {
  implicit val personFormat: OFormat[Player] = Json.format[Player]
}
