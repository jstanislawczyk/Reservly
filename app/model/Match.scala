package model

import play.api.libs.json._
import java.sql.Timestamp
import java.text.SimpleDateFormat

case class Match(
  id: Long,
  matchStatus: String,
  startDate: Timestamp,
  endDate: Timestamp,
  playerId: String
)

object Match {
  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    def reads(json: JsValue): JsSuccess[Timestamp] = {
      val dateAsString = json.as[String]

      JsSuccess(
        new Timestamp(format.parse(dateAsString).getTime)
      )
    }

    def writes(time: Timestamp) = {
      JsString(format.format(time))
    }
  }

  implicit val personFormat: OFormat[Match] = Json.format[Match]
}