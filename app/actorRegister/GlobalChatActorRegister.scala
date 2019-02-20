package actorRegister


object GlobalChatActorRegister {
  var actorRegister: scala.collection.mutable.Map[Int, String] = scala.collection.mutable.Map[Int, String]()

  def unregisterClosedSocket(actorPathForDelete: String): Unit = {
    actorRegister.foreach(actor =>
      if(actor._2 == actorPathForDelete) {
        actorRegister.remove(actor._1)
      }
    )
  }
}
