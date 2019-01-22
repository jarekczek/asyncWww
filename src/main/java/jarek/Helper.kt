package jarek

object Helper {
  fun sleepNoThrow(millis: Long) {
    try {
      Thread.sleep(millis)
    } catch (e: InterruptedException) {}
  }
}