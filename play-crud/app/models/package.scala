import models._
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json.JsPath
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime

package object models {
  implicit val carReads: Reads[Car] = (
    (JsPath \ "id").readNullable[Int] and
    (JsPath \ "title").read[String] and
    (JsPath \ "fuel").read[String] and
    (JsPath \ "price").read[Int] and
    (JsPath \ "new").read[Boolean] and
    (JsPath \ "mileage").readNullable[Int] and
    (JsPath \ "firstRegistration").readNullable[DateTime]
  )(Car.apply _)

  implicit val carWrites: Writes[Car] = (
    (JsPath \ "id").writeNullable[Int] and
    (JsPath \ "title").write[String] and
    (JsPath \ "fuel").write[String] and
    (JsPath \ "price").write[Int] and
    (JsPath \ "new").write[Boolean] and
    (JsPath \ "mileage").writeNullable[Int] and
    (JsPath \ "firstRegistration").writeNullable[DateTime]
  )(unlift(Car.unapply))
}