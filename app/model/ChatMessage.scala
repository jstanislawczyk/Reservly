package model

import play.api.libs.json.{Json, OFormat}

case class ChatMessage(
  message: String
)

object ChatMessage {
  implicit val personFormat: OFormat[ChatMessage] = Json.format[ChatMessage]
}
