package timing

final case class TimedResult[R](result: R, time: Long)

trait Timer {
  def profile[R](code: => R, t: Long = System.nanoTime): TimedResult[R] = TimedResult(code, System.nanoTime - t)
}
