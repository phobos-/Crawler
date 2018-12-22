package timing

import java.util.concurrent.TimeUnit

final case class TimedResult[R](result: R, time: Long)

trait Timer {
  def profile[R](code: => R, t: Long = System.nanoTime): TimedResult[R] = TimedResult(code, System.nanoTime - t)

  def formatTime(nanos: Long): String = {
    val s  = TimeUnit.NANOSECONDS.toSeconds(nanos)
    val ms = TimeUnit.NANOSECONDS.toMillis(nanos) - TimeUnit.SECONDS.toMillis(s)
    val us = TimeUnit.NANOSECONDS.toMicros(nanos) - TimeUnit.MILLISECONDS.toMicros(ms) - TimeUnit.SECONDS.toMicros(s)
    "%d s, %d ms, %d us".format(s, ms, us)
  }
}
