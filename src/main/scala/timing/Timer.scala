package timing

trait Timer {
  def time[R](block: => R)(prefix: String, log: String => Unit): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    log(prefix + getReadableTime(t1 - t0))
    result
  }

  private def getReadableTime(nanos: Long) = {
    val msec = nanos / (1000 * 1000)
    val tempSec = msec / 1000
    val sec = tempSec % 60
    val min = (tempSec / 60) % 60
    "%dm %ds %dms".format(min, sec, msec)
  }
}
