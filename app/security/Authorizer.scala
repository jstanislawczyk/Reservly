package security

import javax.inject.Inject
import repository.PlayerRepository

import scala.concurrent.{ExecutionContext, Future}

class Authorizer @Inject()(playerRepository: PlayerRepository)(implicit ec: ExecutionContext) {

  def authorizePlayerAccess(playerId: Long): Future[Boolean] = {

    playerRepository
      .checkIfPlayerExists(playerId)
  }
}
