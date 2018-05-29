object Version {
  val baseVersion = "0.0.0"
  def apply(): String =
    baseVersion + "-" + scala.util.Properties.envOrElse("BUILD_NUMBER", "SNAPSHOT")
}
