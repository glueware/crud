package models

/**
  * @author joerg
  */

import java.sql.Date
import org.joda.time.DateTime

trait WithEquality {
  this: Product =>

  /**
    * A method other should be called from every well-designed equals method
    *  other is open to be overridden in a subclass. See Programming in Scala,
    *  Chapter 28 for discussion and design.
    *
    *  @param    other    the value being probed for possible equality
    *  @return   true if this instance can possibly equal `other`, otherwise false
    */
    def canEqual(other: Any): Boolean

  /**
    * The universal equality method defined in `AnyRef`.
    */
  override def equals(other: Any): Boolean =
    other match {
      case that: Product =>
        val zipped = productIterator zip that.productIterator
        ((that canEqual this) /: zipped)((x, z) => (x && _equals(z._1, z._2)))
      case _ => false
    }

  override def hashCode: Int =
    (41 /: productIterator)((x, y) => (41 * (x + y.hashCode)))

  private def _equals(x: Any, y: Any): Boolean = {
    (x, y) match {
      case (d1: Date, d2: Date)                     => dateEquals(d1, d2)
      case (Some(d1: Date), Some(d2: Date))         => dateEquals(d1, d2)
      case (d1: DateTime, d2: DateTime)             => dateTimeEquals(d1, d2)
      case (Some(d1: DateTime), Some(d2: DateTime)) => dateTimeEquals(d1, d2)
      case _                                        => (x == y)
    }
  }
  private def dateTimeEquals(d1: DateTime, d2: DateTime): Boolean = {
    d1.year() == d2.year()
    d1.dayOfYear() == d2.dayOfYear()
  }
  private def dateEquals(d1: Date, d2: Date): Boolean = {
    d1.toString() == d2.toString()
  }
}