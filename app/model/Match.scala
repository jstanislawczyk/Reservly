package model

import play.api.libs.json._
import java.sql.Timestamp
import java.text.SimpleDateFormat

case class Match(
  id: Long,
  startDate: Timestamp,
  endDate: Timestamp,
  gameName: String,
  playerId: String
)

object Match {
  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")

    def reads(json: JsValue): JsSuccess[Timestamp] = {
      val dateAsString = json.as[String]

      JsSuccess(
        new Timestamp(format.parse(dateAsString).getTime)
      )
    }

    def writes(time: Timestamp) = JsString(format.format(time))
  }

  implicit val matchFormat: OFormat[Match] = Json.format[Match]
}