package model

import play.api.libs.json._

case class Player(
  id: String,
  displayName: String,
  email: String,
  photoUrl: String
)

object Player {
  implicit val personFormat: OFormat[Player] = Json.format[Player]
}
