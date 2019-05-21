package model

import play.api.libs.json.{Json, OFormat}
import serializer.ErrorMessageJsonSerializer

case class ResponseMessage(
  httpCode: String,
  message: String
)

object ResponseMessage {
  implicit val errorFormat: OFormat[GlobalChatMessage] = Json.format[GlobalChatMessage]

  def createResponseMessageAsJson(httpCode: String, responseMessage: String): String = {
    ErrorMessageJsonSerializer.toJson(
      new ResponseMessage(
        httpCode,
        responseMessage
      )
    )
  }
}
