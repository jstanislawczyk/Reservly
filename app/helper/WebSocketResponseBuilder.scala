package helper

import helper.WebSocketResponseType.WebSocketResponseType

object WebSocketResponseBuilder {
  def buildWebsocketResponse(responseType: WebSocketResponseType, objectJson: String): String = {
    s"""
      {
        "responseType": "$responseType",
        "responseBody": $objectJson
      }
    """.stripMargin
  }
}
