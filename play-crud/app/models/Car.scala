package models

import java.sql._
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.DefaultReads
import play.api.libs.json.Format
import play.api.libs.json.DefaultReads
import org.joda.time.DateTime

/**
  * @author joerg
  */
case class Car(
  id: Option[Int],
  title: String,
  fuel: String,
  price: Int,
  `new`: Boolean,
  mileage: Option[Int],
  firstRegistration: Option[DateTime])
    extends WithEquality {
  def canEqual(other: Any): Boolean = other.isInstanceOf[Car]
}
