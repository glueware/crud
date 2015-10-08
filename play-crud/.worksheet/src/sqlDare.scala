object sqlDare {

  import java.sql._
  import play.api.mvc._
  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import play.api.libs.json.Json._
  import play.api.Play.current
  import org.joda.time.DateTime;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(277); 

  println("Welcome to the Scala worksheet");$skip(79); 
 
   def timestampToDateTime(t: Timestamp): DateTime = new DateTime(t.getTime);System.out.println("""timestampToDateTime: (t: java.sql.Timestamp)org.joda.time.DateTime""");$skip(82); 

  def dateTimeToTimestamp(dt: DateTime): Timestamp = new Timestamp(dt.getMillis);System.out.println("""dateTimeToTimestamp: (dt: org.joda.time.DateTime)java.sql.Timestamp""");$skip(238); 

  implicit val timestampFormat = new Format[Timestamp] {

    def writes(t: Timestamp): JsValue = toJson(timestampToDateTime(t))

    def reads(json: JsValue): JsResult[Timestamp] = fromJson[DateTime](json).map(dateTimeToTimestamp)

  }

  case class Event(id: Long, startTime: Option[Timestamp] = None, endTime: Option[Timestamp] = None);System.out.println("""timestampFormat  : play.api.libs.json.Format[java.sql.Timestamp] = """ + $show(timestampFormat ));$skip(233); 

  val eventString = """{
  "id": 1,
  "startTime": "2011-10-02 18:48:05.123456",
  "endTime": "2011-10-02 20:48:05.123456"
  }""";System.out.println("""eventString  : String = """ + $show(eventString ));$skip(52); 

  val eventVaue: JsValue = Json.parse(eventString);System.out.println("""eventVaue  : play.api.libs.json.JsValue = """ + $show(eventVaue ));$skip(23); 
 
  println(eventVaue);$skip(231); 

  implicit val eventFormat: Format[Event] = (
    (JsPath \ "id").format[Long] and
    (JsPath \ "startTime").formatNullable[Timestamp] and
    (JsPath \ "endTime").formatNullable[Timestamp])(Event.apply _, unlift(Event.unapply));System.out.println("""eventFormat  : play.api.libs.json.Format[sqlDare.Event] = """ + $show(eventFormat ));$skip(29); val res$0 = 

  eventVaue.validate[Event];System.out.println("""res0: play.api.libs.json.JsResult[sqlDare.Event] = """ + $show(res$0))}
}
