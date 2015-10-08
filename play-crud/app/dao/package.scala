
/**
  * @author joerg
  */

package object dao {
  import slick.driver.H2Driver.api._
  import java.sql.Date
  import org.joda.time.DateTime

  implicit def date2dateTime = MappedColumnType.base[DateTime, Date](
    dateTime => new Date(dateTime.getMillis),
    date => new DateTime(date)
  )
}