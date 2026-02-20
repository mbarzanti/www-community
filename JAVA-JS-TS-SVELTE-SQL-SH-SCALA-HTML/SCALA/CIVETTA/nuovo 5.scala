
import slick.jdbc.H2Profile.api._
class FooBar {
  def createBean1599() = Action { implicit request =>
    val json = request.body.asJson.get
    val mapper = new ObjectMapper
    mapper.enableDefaultTyping
    mapper.readValue(json.toString, classOf[Bean1599])
  }
}

