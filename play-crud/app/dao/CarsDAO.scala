package dao

import scala.concurrent.Future
import scala.concurrent.Promise
import models._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Results._
import slick.driver.JdbcProfile
import java.sql.Date
import scala.util.Success
import scala.util.Failure
import org.joda.time.DateTime
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.Try

class CarsDAO() extends HasDatabaseConfig[JdbcProfile] {
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import driver.api._

  private val cars = TableQuery[CarsTable]
  private val fuel = TableQuery[FuelTable]

  def list(sort: Option[String] = None): Future[Seq[Car]] = db.run(sortedCars(sort).result).map(_.toList)

  def get(id: Int): Future[Car] = {
    val carPromise = Promise[Car]
    val result = db.run(cars.filter(_.id === id).result.headOption)

    result onComplete {
      case Success(s) => {
        if (s.isDefined) {
          carPromise.success(s.get)
        } else {
          carPromise.failure(new ServerException(NotFound(s"""Id ($id) in "Car" table not found""")))
        }
      }
      case Failure(exception) => {
        carPromise.failure(exception)
      }
    }
    carPromise.future
  }

  def add(car: Car): Future[Car] =
    runValidatedCar[Car](car, cars returning cars.map(_.id) into ((c, id) => c.copy(id = id)) += car) // returned car contains now id

  def modify(id: Int, car: Car): Future[Boolean] =
    runValidatedCar[Int](car, cars.filter(_.id === id).update(car.copy(id = Some(id)))) map { validateFound(id) }

  def delete(id: Int): Future[Boolean] =
    db.run(cars.filter(_.id === id).delete) map { validateFound(id) }

  private def runValidatedCar[R](car: Car, runable: DBIOAction[R, NoStream, Nothing]): Future[R] = {
    val usedValid =
      if (!car.`new`)
        car.mileage.isDefined && car.mileage.isDefined
      else
        true

    val fuelValid = Await.result(db.run(fuel.filter(_.name === car.fuel).result.headOption.map(_.isDefined)), 1 seconds)

    if (usedValid && fuelValid)
      db.run(runable)
    else
      Future.failed(new ServerException(BadRequest(s"""Not valid""")))
  }

  private def validateFound(id: Int)(count: Int): Boolean = {

    if (count == 1)
      true
    else if (count == 0)
      throw new ServerException(NotFound(s"""Id ($id) in "Car" table not found"""))
    else
      throw new ServerException(InternalServerError(s"""More than one "Car" in table for id ($id)"""))
  }

  object Columns extends Enumeration {
    val id = "id"
    val title = "title"
    val fuel = "fuel"
    val price = "price"
    val `new` = "new"
    val mileage = "mileage"
    val firstRegistration = "firstRegistration"
  }

  private class CarsTable(tag: Tag) extends Table[Car](tag, "CAR") {

    def id = column[Option[Int]](Columns.id, O.AutoInc, O.PrimaryKey)
    def title = column[String](Columns.title)
    def fuel = column[String](Columns.fuel)
    def price = column[Int](Columns.price)
    def `new` = column[Boolean](Columns.`new`)
    def mileage = column[Option[Int]]("mileage")
    def firstRegistration = column[Option[DateTime]](Columns.firstRegistration)
    def * = (id, title, fuel, price, `new`, mileage, firstRegistration) <> (Car.tupled, Car.unapply)
  }

  private def sortedCars(sort: Option[String]) = {
    if (sort.isDefined) {
      sort.get match {
        case Columns.id                => cars.sortBy(_.id)
        case Columns.title             => cars.sortBy(_.title)
        case Columns.fuel              => cars.sortBy(_.fuel)
        case Columns.`new`             => cars.sortBy(_.`new`)
        case Columns.mileage           => cars.sortBy(_.mileage)
        case Columns.firstRegistration => cars.sortBy(_.firstRegistration)
        case _                         => cars.sortBy(_.id) // maybe an exception should be thrown
      }
    } else {
      cars
    }
  }

  private val Fuel = TableQuery[FuelTable]

  def fuelExists(name: String): Future[Boolean] = db.run(Fuel.filter(_.name === name).result).map { _.toList.nonEmpty }

  private class FuelTable(tag: Tag) extends Table[(String)](tag, "FUEL") {
    def name = column[String]("name", O.PrimaryKey)
    def * = (name)
  }
}
