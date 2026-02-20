@(foobar: String, value: String)
@main("Temp") {
  <div>
    <p>
      <!-- VIOLAZ -->
      Hello world: @Html(value)
    </p>
    <p>
      <!-- VIOLAZ -->
      Hello world: @Html("<div>" + value + "</div>")
    </p>
    <p>
      <!-- VIOLAZ -->
      Hello world: @Html("<br />" + value)
    </p>
    <p>
      <!-- VIOLAZ -->
      Hello world: @Html(value + "<br />")
    </p>
    <p>
      <!-- ok -->
      Hello world: @Html("<div>Hardcoded text</div>")
    </p>
    <p>
      <!-- ok -->
      Hello world: @Html("<div>" + "Hardcoded text" + "</div>")
    </p>
  </div>
}

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext
object Smth {
  def call1(wsClient: WSClient, url: String): Future[Unit] = {
    // VIOLAZ
    wsClient.url(url).get().map { response =>
      val statusText: String = response.statusText
      println(s"Got a response $statusText")
    }
  }
  def call2(wsClient: WSClient): Future[Unit] = {
    // ok
    wsClient.url("https://www.google.com").get().map { response =>
      val statusText: String = response.statusText
      println(s"Got a response $statusText")
    }
  }
}
object FooBar {
  def call1(url: String): Future[Unit] = {
    val wsClient = AhcWSClient()
    // VIOLAZ
    wsClient.url(url).get().map { response =>
      val statusText: String = response.statusText
      println(s"Got a response $statusText")
    }
  }
  def call2(): Future[Unit] = {
    val wsClient = AhcWSClient()
    // ok
    wsClient.url("https://www.google.com").get().map { response =>
      val statusText: String = response.statusText
      println(s"Got a response $statusText")
    }
  }
}
@Singleton
class HomeController @Inject()(
  ws: WSClient,
  val controllerComponents: ControllerComponents,
  implicit val ec: ExecutionContext
) extends BaseController {
  def req1(url: String) = Action.async { implicit request: Request[AnyContent] =>
    // VIOLAZ
    val futureResponse = ws.url(url).get()
    futureResponse.map { response =>
      Ok(s"it works: ${response.statusText}")
    }
  }
  def req2(url: String) = Action.async { implicit request: Request[AnyContent] =>
    // ok
    val futureResponse = ws.url("https://www.google.com").get()
    futureResponse.map { response =>
      Ok(s"it works: ${url}")
    }
  }
}

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
object App {
    val secret = "secret"
}
class App {
    def bad1() = {
        try {
            // VIOLAZ
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                .withIssuer("auth0")
                .sign(algorithm);
        } catch (exception: JWTCreationException){
            //Invalid Signing configuration / Couldn't convert Claims.
        }
    }
    def ok1(secretKey: String) = {
        try {
            // VIOLAZ
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                .withIssuer("auth0")
                .sign(algorithm);
        } catch (exception: JWTCreationException){
            //Invalid Signing configuration / Couldn't convert Claims.
        }
    }
}

import slick.jdbc.H2Profile.api._
class FooBar {
  def something(name: String) = {
    val db = Database.forConfig("h2mem1")
    lazy val people = TableQuery[People]
        people.map(p => (p.id,p.name,p.age)) // ok
      .result
      .overrideSql("SELECT id, name, age FROM Person")
        val query = "SELECT id, name, age FROM Person"
        people.map(p => (p.id,p.name,p.age)) // ok
      .result
      .overrideSql(query)
    // VIOLAZ
    people.map(p => (p.id,p.name,p.age))
      .result
      .overrideSql(s"SELECT id, name, age FROM Person WHERE $name")
  }
  
  def something(name: String) = {
    val db = Database.forConfig("h2mem1")
    // VIOLAZ
    val action = sql"select ID, NAME, AGE from #$name".as[(Int,String,Int)]
    db.run(action)
    // ok
    val action2 = sql"select ID, NAME, AGE from $name".as[(Int,String,Int)]
    db.run(action2)
  }

  // post /bean1599 -> jackson databind vulnerability (CVE-2017-7525),
  // see https://github.com/FasterXML/jackson-databind/commit/60d459cedcf079c6106ae7da2ac562bc32dcabe1
  def createBean1599() = Action { implicit request =>
    val json = request.body.asJson.get
    val mapper = new ObjectMapper
    mapper.enableDefaultTyping
    mapper.readValue(json.toString, classOf[Bean1599])
    Ok
  }
}

