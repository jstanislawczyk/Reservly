package helper

object WebSocketResponseType extends Enumeration {
  type WebSocketResponseType = Value

  val DIRECT_CHAT, GLOBAL_CHAT, MATCH_SAVED, MATCH_DELETED, ACTIVE_PLAYER_REGISTER, ACTIVE_PLAYER_UNREGISTER = Value
}