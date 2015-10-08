
/**
  * @author joerg
  */
package test

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.specs2.mutable.Specification
import play.api.test.WithApplicationLoader
import java.sql.Date
import java.util.UUID
import org.joda.time.DateTime
import play.api.test.PlaySpecification
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.mvc.Results
import play.api.libs.json._
import dao._
import models._
import play.api.test.FakeHeaders
import play.api.test.Helpers
import play.api.mvc.AcceptExtractors

/** test the kitty cat database */
class CarsCtrlSpec extends PlaySpecification with Results {

  val car1 = Car(
    id = None,
    title = "Car1",
    fuel = "gasoline",
    price = 10000,
    `new` = true,
    mileage = None,
    firstRegistration = None)

  val modifiedCar1 = Car(
    id = car1.id,
    title = "Audi A4 Avant",
    fuel = "gasoline",
    price = 10000,
    `new` = true,
    mileage = None,
    firstRegistration = None)

  val millis: Long = System.currentTimeMillis()

  val car2 = Car(
    id = None,
    title = "Car2",
    fuel = "diesel",
    price = 10000,
    `new` = false,
    mileage = Some(123456),
    firstRegistration = Some(new DateTime(millis)))

  val carJson1 = """{"title":"Car1","fuel":"gasoline","price":10000,"new":true}"""
  val carModifiedJason1 = """{"title":"Car1","fuel":"gasoline","price":10000,"new":true}"""
  val carJson2 = """{"title":"Car2","fuel":"diesel","price":10000,"new":false,"mileage":123456,"firstRegistration":"2015-09-27T00:00:00.000+02:00"}"""

  "CarsCtrl " should {
    val controller = new controllers.CarsCtrl()

    "list cars" in {
      implicit val app = FakeApplication()
      running(app) {

        val fakeRequest = FakeRequest(Helpers.POST, controllers.routes.CarsCtrl.add.url, FakeHeaders(), Json.parse(carJson1))
        controller.add.apply(fakeRequest)

        val result = controller.list("name")(FakeRequest())

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
      }
    }

    "get return not found for invalid car id" in {
      implicit val app = FakeApplication()
      running(app) {
        val result = controller.get(Int.MaxValue)(FakeRequest())

        status(result) must equalTo(404)
      }
    }

    "add a car" in {
      implicit val app = FakeApplication()
      running(app) {

        val fakeRequest = FakeRequest(Helpers.POST, controllers.routes.CarsCtrl.add.url, FakeHeaders(), Json.parse(carJson1))
        val result = controller.add.apply(fakeRequest)

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
      }
    }
  }
}
