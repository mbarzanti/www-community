import play.api.mvc._
import javax.inject._
import java.sql.{Connection, ResultSet, DriverManager}
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{
  def oneAction() = Action { implicit request: Request[AnyContent] =>
    val bodyVals = request.body.asFormUrlEncoded
    val name = bodyVals.get("username").head
    // VIOLAZ se parametri untrusted
    val sql = "SELECT * FROM table WHERE name = " + name + ";"
    val conn = DriverManager.getConnection("jdbc:mysql://localhost:8080", "guest", "password")
    val stmt = conn.createStatement()
    val rs = stmt.execute(sql)
    Ok(rs.first())
  }
  def twoAction() = Action { implicit request: Request[AnyContent] =>
    val bodyVals = request.body.asFormUrlEncoded
    val name = bodyVals.get("username").head
    // VIOLAZ SC_80S se parametri untrusted
    val sql = "SELECT * FROM table WHERE name = " + name + ";"
    val conn = DriverManager.getConnection("jdbc:mysql://localhost:8080", "guest", "password")
    val stmt = conn.createStatement()
    val rs = stmt.execute(sql)
    Ok(rs.first())
  }
  def threeAction() = Action { implicit request: Request[AnyContent] =>
    val bodyVals = request.body.asFormUrlEncoded
    val name = bodyVals.get("username").head
    val conn = DriverManager.getConnection("jdbc:mysql://localhost:8080", "guest", "password")
    val stmt = conn.createStatement()
    // VIOLAZ non ancora rilevata (name untrusted da bodyVals, untrusted da request 
    val rs = stmt.execute(s"SELECT * FROM table WHERE name = $name;")
    Ok(rs.first())
  }
  def okAction1() = Action { implicit request: Request[AnyContent] =>
    val name = "hardcoded_value"
    val conn = DriverManager.getConnection("jdbc:mysql://localhost:8080", "guest", "password")
    val stmt = conn.createStatement()
    // ok
    val rs = stmt.execute(s"SELECT * FROM table WHERE name = $name;")
    Ok(rs.first())
  }
  def okAction2(name: String) = Action { implicit request: Request[AnyContent] =>
    // name untrusted dalla request
    val name = request.body.asFormUrlEncoded.get("username").head
	// VIOLAZ SC_80S play-Tainted SQL from HTML request (SCALA)
    val sql = "SELECT * FROM table WHERE name = " + name + ";"
    val conn = DriverManager.getConnection("jdbc:mysql://localhost:8080", "guest", "password")
    val stmt = conn.createStatement()
    val rs = stmt.execute(sql)
    Ok(rs.first())
  }
}
