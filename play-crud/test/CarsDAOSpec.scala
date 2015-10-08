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
class CarsDAOSpec extends PlaySpecification with Results {

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

  "CarsDao" should {

    "add cars and then list the added cars" in new WithApplicationLoader {

      val carsDAO = new CarsDAO

      val carSet = Set(car1, car2)
      Await.result(Future.sequence(carSet.map(carsDAO.add)), 1 seconds)
      val storedCarSet = Await.result(carsDAO.list(), 1 seconds).toSet
      //      storedCarSet.subsetOf(carSet) must equalTo(true)
      storedCarSet.toSet.map((c: Car) => c.copy(id = None)) must equalTo(carSet)
    }

    "modify an added car and get an added car" in new WithApplicationLoader {

      val carsDAO = new CarsDAO

      val carSet = Set(car1, car2)
      Await.result(Future.sequence(carSet.map(carsDAO.add)), 1 seconds)

      var storedCarSet = Await.result(carsDAO.list(), 1 seconds).toSet
      val storedCar = storedCarSet.head
      val stored = storedCarSet.contains(storedCar)

      Await.ready(carsDAO.modify(storedCar.id.get, modifiedCar1), 1 seconds)

      val modifiedStoredCar = Await.result(carsDAO.get(storedCar.id.get), 1 seconds)

      stored && modifiedStoredCar == modifiedCar1.copy(id = storedCar.id) must equalTo(true)
    }

    // TODO test sort

    "delete an added car" in new WithApplicationLoader {
      val carsDAO = new CarsDAO

      val carSet = Set(car1, car2)
      Await.result(Future.sequence(carSet.map(carsDAO.add)), 1 seconds)

      var storedCarSet = Await.result(carsDAO.list(), 1 seconds).toSet
      val storedCar = storedCarSet.head
      val stored = storedCarSet.contains(storedCar)

      Await.ready(carsDAO.delete(storedCar.id.get), 1 seconds)

      storedCarSet = Await.result(carsDAO.list(), 1 seconds).toSet

      stored && !storedCarSet.contains(storedCar) must equalTo(true)
    }
  }
}
