package model

import play.api.libs.json.{Json, OFormat}

case class ErrorMessage(
  httpCode: String,
  message: String
)

object ErrorMessage {
  implicit val errorFormat: OFormat[ChatMessage] = Json.format[ChatMessage]
}
