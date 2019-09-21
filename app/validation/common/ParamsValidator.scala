package validation.common

object ParamsValidator {

  def areParametersNegative(parameters: Int*): Boolean = {
    var negativeFound: Boolean = false

    parameters
      .foreach(param => {
        if(param < 0 ) negativeFound = true
      })

    negativeFound
  }
}
