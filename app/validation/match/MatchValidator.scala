package validation.`match`

import java.sql.Timestamp

import model.Match
import validation.Validator

object MatchValidator extends Validator[Match] {

  private val minimumGameNameSize = MatchValidatorValues.minimumGameNameSize
  private val maximumGameNameSize = MatchValidatorValues.maximumGameNameSize

  private val maximumMatchLimitInMilliseconds = MatchValidatorValues.maximumMatchLimitInMilliseconds

  override def validate(game: Match): Boolean = {
    var isValid = true

    if(isStartDateAfterEndDate(game.startDate, game.endDate)) {
      isValid = false
    }

    if(isMatchDurationGreaterThanLimit(game.startDate, game.endDate)) {
      isValid = false
    }

    if(isCreatedBeforeCurrentDate(game.startDate)) {
      isValid = false
    }

    if(isValueNotInRange(game.gameName.length, minimumGameNameSize, maximumGameNameSize)) {
      isValid = false
    }

    isValid
  }

  private def isCreatedBeforeCurrentDate(startDate: Timestamp): Boolean = {
    val currentTime = new Timestamp(System.currentTimeMillis())

    startDate.before(currentTime)
  }

  private def isStartDateAfterEndDate(startDate: Timestamp, endDate: Timestamp): Boolean = {
    startDate.after(endDate)
  }

  private def isMatchDurationGreaterThanLimit(startDate: Timestamp, endDate: Timestamp): Boolean = {
    val maximumMatchLimit = this.maximumMatchLimitInMilliseconds
    val duration = endDate.getTime - startDate.getTime

    maximumMatchLimit < duration
  }

  private def isValueNotInRange = (value: Int, min: Int, max: Int) => value < min || value > max
}
