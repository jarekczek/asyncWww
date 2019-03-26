package jarek

object Pars {
  val count get() = System.getProperty("count", "100").toInt()
  val host get() = System.getProperty("host", "localhost")
  val port get() = System.getProperty("port", "8080").toInt()
  val delay get() = System.getProperty("delay", "10").toInt()
  val verbose get() = System.getProperty("verbose", "false").toBoolean()
  val serverUrl get() = "http://${Pars.host}:${Pars.port}/waitAsync?seconds=${Pars.delay}"
}