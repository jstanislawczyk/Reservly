package validation

trait Validator[Object] {
  def validate(model: Object): Boolean
}
