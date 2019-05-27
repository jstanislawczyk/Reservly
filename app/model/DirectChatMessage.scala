package model

import java.sql.Timestamp
import java.text.SimpleDateFormat

import play.api.libs.json._

case class DirectChatMessage (
  chatId: String,
  senderId: String,
  receiverId: String,
  message: String,
  var messageSendDate: Timestamp
)

object DirectChatMessage {
  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    def reads(json: JsValue): JsSuccess[Timestamp] = {
      val dateAsString = json.as[String]

      JsSuccess(
        new Timestamp(format.parse(dateAsString).getTime)
      )
    }

    def writes(time: Timestamp) = JsString(format.format(time))
  }

  implicit val directMessageFormat: OFormat[DirectChatMessage] = Json.format[DirectChatMessage]
}