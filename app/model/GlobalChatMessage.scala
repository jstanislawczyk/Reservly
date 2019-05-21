package model

import play.api.libs.json.{Json, OFormat}

case class GlobalChatMessage(
  playerId: String,
  message: String
)

object GlobalChatMessage {
  implicit val messageFormat: OFormat[GlobalChatMessage] = Json.format[GlobalChatMessage]
}
