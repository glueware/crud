package controllers

/**
  * @author joerg
  */
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models._
import dao._
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success
import play.api.libs.json.Writes
import dao.ServerException

class CarsCtrl() extends Controller {

  def carsDao = new CarsDAO

  def list(sort: String) = Action.async { implicit request =>
    getResult(carsDao.list(Some(sort)))
  }

  def get(id: Int) = Action.async { implicit request =>
    getResult(carsDao.get(id))
  }

  def add() = Action.async(BodyParsers.parse.json) { implicit request =>
    request.body.validate[Car] match {
      case JsSuccess(car, _) =>
        getResult(carsDao.add(car))
      case JsError(error) => Future(BadRequest(error.toString()))
    }
  }

  def modify(id: Int) = Action.async(BodyParsers.parse.json) { implicit request =>
    request.body.validate[Car] match {
      case JsSuccess(car, _) =>
        getResult(carsDao.modify(id, car))
      case JsError(error) => Future(BadRequest(error.toString()))
    }
  }

  def delete(id: Int) = Action.async {
    getResult(carsDao.delete(id))
  }

  private def getResult[R](result: Future[R])(implicit tjs: Writes[R]): Future[Result] = {
    val promise = Promise[Result]()
    result onComplete {
      case Success(r) => {
        promise.success(Ok(Json.toJson(r)))
      }
      case Failure(exception) => {
        exception match {
          case e: ServerException => promise.success(e.error)
          case _                  => promise.success(InternalServerError(exception.getMessage))
        }
      }
    }
    promise.future
  }
}
