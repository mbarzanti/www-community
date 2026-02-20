class XssController extends Controller {

  def variousSafe(value: String) = Action { implicit request: Request[AnyContent] =>
    val escapedValue = org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(value)
    // ok
    Ok("Hello " + escapedValue + " !")
    // ok: tainted-html-response
    Ok("Hello " + escapedValue + " !").as("text/html")

    val owaspEscapedValue = org.owasp.encoder.Encode.forHtml(value)
    // ok
    Ok("Hello " + owaspEscapedValue + " !").as("text/html")
    // ok
    Ok("Hello " + owaspEscapedValue + " !")
    // ok
    Ok("Hello "+value+" !")
    // ok
    Ok(s"Hello $value !").as("text/json")
    // ok
    Ok(views.html.xssHtml.render(Html.apply("<b>Hello !</b>")))

  }
  
  def vulnerable1(value: String) = Action { implicit request: Request[AnyContent] =>
    // VIOLAZ
    Ok(s"Hello $value !").as("text/html")
  }
  def vulnerable2(value: String) = Action.async { implicit request: Request[AnyContent] =>
    // VIOLAZ
    Ok("Hello " + value + " !").as("tExT/HtML")
  }
  def vulnerable3(value: String, contentType: String) = Action { implicit request: Request[AnyContent] =>
    val bodyVals = request.body.asFormUrlEncoded
    val smth = bodyVals.get("username").head
    // VIOLAZ
    Ok(s"Hello $smth !").as(contentType)
  }
  def vulnerable4(value: String) = Action.async(parse.json) { implicit request: Request[AnyContent] =>
    // VIOLAZ
    Ok("Hello " + value + " !").as(ContentTypes.HTML)
  }
  def vulnerable5(value: String) = Action(parse.json) {
    // VIOLAZ
    Ok(s"Hello $value !").as(HTML)
  }
  def vulnerable6(value:String) = Action { implicit request: Request[AnyContent] =>
    // VIOLAZ
    Ok(views.html.xssHtml.render(Html.apply("Hello "+value+" !")))
  }
    def vulnerable7(value:String) = Action {
    // VIOLAZ
    Ok(views.html.xssHtml.render(Html.apply("Hello "+value+" !")))
  }
  def safeJson(value: String) = Action.async { implicit request: Request[AnyContent] =>
    // ok
    Ok("Hello " + value + " !").as("text/json")
  }
  def safeTemplate(value:String) = Action {
    // ok
    Ok(views.html.template.render(value))
  }

}
