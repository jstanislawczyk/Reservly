package model

import play.api.libs.json.{Json, OFormat}

case class ChatMessage (
  playerId: String,
  message: String
)

object ChatMessage {
  implicit val messageFormat: OFormat[ChatMessage] = Json.format[ChatMessage]
}
