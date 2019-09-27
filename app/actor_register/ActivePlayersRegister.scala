package actor_register

import actor_register.ActivePlayersRegister.activePlayersRegister

object ActivePlayersRegister {
  var activePlayersRegister: scala.collection.mutable.ListBuffer[String] = scala.collection.mutable.ListBuffer[String]()
}

class ActivePlayersRegister() {

  def registerActivePlayer(playerId: String): Unit = {
    if(activePlayerNotRegistered(playerId)) {
      activePlayersRegister += playerId
    }
  }

  def unregisterActivePlayer(playerId: String): Unit = {
    activePlayersRegister -= playerId
  }

  private def activePlayerNotRegistered(playerId: String): Boolean = {
    !activePlayersRegister.contains(playerId)
  }
}
