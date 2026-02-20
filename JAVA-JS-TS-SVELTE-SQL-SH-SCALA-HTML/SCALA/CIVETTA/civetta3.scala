import org.apache.commons.codec.digest.DigestUtils
import scala.util.parsing.json oppure import scala.util.parsing.json._
import java.util.Scanner

import java.util.{ Calendar, GregorianCalendar } // VIOLAZ
import java.util.Calendar // VIOLAZ
import java.util.Date // VIOLAZ
import dispatch._
import Defaults._
import javax.xml.parsers.SAXParserFactory
import java.io.{File, FileReader}
import javax.xml.stream.XMLInputFactory
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import scalaj.http.{Http, Token}
import scala.io.{Codec, Source}

sealed trait Foo

public class MyException extends Error { /* ... */ }       // VIOLAZ
public class MyException extends Exception { /* ... */ }   // OK

class RSACipher {
  def badRSACipher(): Void =
    try {
      // VIOLAZ
      val c = Cipher.getInstance("RSA/None/NoPadding")
      c.init(Cipher.ENCRYPT_MODE, k, iv)
      val cipherText = c.doFinal(plainText)
    } catch {
      case NonFatal(e) => throw new RuntimeException("Encrypt error", e)
    }
  def okRSACipher(): Void =
    try {
      // ok
      var c = Cipher.getInstance("RSA/ECB/OAEPWithMD5AndMGF1Padding")
      c.init(Cipher.ENCRYPT_MODE, k, iv)
      val cipherText = c.doFinal(plainText)
    } catch {
      case NonFatal(e) => throw new RuntimeException("Encrypt error", e)
    }
}


class Test {
  def bad1(value:String) = Action {
    if (!Files.exists(Paths.get("public/lists/" + value))) {
      NotFound("File not found")
    } else {
      // VIOLAZ
      val result = Source.fromFile("public/lists/" + value).getLines().mkString // Weak point
      Ok(result)
    }
  }
  def bad2(value:String) = Action {
    if (!Files.exists(Paths.get("public/lists/" + value))) {
      NotFound("File not found")
    } else {
      val filename1 = "public/lists"
      val filename = filename1 + value
      // VIOLAZ
      val result = Source.fromFile(filename).getLines().mkString // Weak point
      Ok(result)
    }
  }
  def bad3(value:String) = Action {
    if (!Files.exists(Paths.get("public/lists/" + value))) {
      NotFound("File not found")
    } else {
      // VIOLAZ
      val result = Source.fromFile("%s/%s".format("public/lists", value)).getLines().mkString // Weak point
      Ok(result)
    }
  }
  def bad4(value:String) = Action {
    if (!Files.exists(Paths.get("public/lists/" + value))) {
      NotFound("File not found")
    } else {
      var filename1 = "public/lists/"
      val filename = filename1.concat(value)
      // VIOLAZ
      val result = Source.fromFile(filename).getLines().mkString // Weak point
      Ok(result)
    }
  }
  def ok(value:String) = Action {
    val filename = "public/lists/" + FilenameUtils.getName(value)
    if (!Files.exists(Paths.get(filename))) {
      NotFound("File not found")
    } else {
      // ok
      val result = Source.fromFile(filename).getLines().mkString // Fix
      Ok(result)
    }
  }
}


object Smth {
  def call1(request_url: String) = {
    // VIOLAZ
    val html = Source.fromURI(request_url)
    val data = html.mkString
    data
  }
  def call2() = {
    // ok
    val html = Source.fromURI("https://www.google.com")
    val data = html.mkString
    data
  }
}
object FooBar {
  def call1(request_url: String, codec: Codec) = {
    // VIOLAZ
    val res = Source.fromURL(request_url)(codec).mkString
    res
  }
  def call2() = {
    // ok
    val res = Source.fromURL("https://www.google.com")(codec).mkString
    res
  }
}
@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents,
  implicit val ec: ExecutionContext
) extends BaseController {
  def req1(request_url: String) = Action.async { implicit request: Request[AnyContent] =>
    val codec = whatIsCodec()
    // VIOLAZ
    val res = Source.fromURL(request_url, codec).mkString
    Ok(res)
  }

  def req2() = Action.async { implicit request: Request[AnyContent] =>
    // ok
    val res = Source.fromURL("https://www.google.com").mkString
    Ok(res)
  }
}


object Smth {
  def call1(url: String): Future[Unit] = {
    // VIOLAZ
    val response: HttpResponse[String] = Http(url).param("q","monkeys").asString
    response.body
  }
  def call2(): Future[Unit] = {
    // ok
    val response: HttpResponse[String] = Http("https://www.google.com").param("q","monkeys").asString
    response.body
  }
}
object FooBar {
  def call1(url: String): Future[Unit] = {
    // VIOLAZ
    val request = Http(url).postForm(Seq("name" -> "jon", "age" -> "29"))
    request.asString
  }
  def call2(): Future[Unit] = {
    // ok
    val request = Http("https://www.google.com").postForm(Seq("name" -> "jon", "age" -> "29"))
    request.asString
  }
}
@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents,
  implicit val ec: ExecutionContext
) extends BaseController {
  def req1(url: String) = Action.async { implicit request: Request[AnyContent] =>
    val consumer = Token("key", "secret")
    // VIOLAZ
    val response = Http(url).postForm(Seq("oauth_callback" -> "oob")).oauth(consumer).asToken
    Ok(response.body.key)
  }
  def req2() = Action.async { implicit request: Request[AnyContent] =>
    val consumer = Token("key", "secret")
    // ok
    val response = Http("https://www.google.com").postForm(Seq("oauth_callback" -> "oob")).oauth(consumer).asToken
    Ok(response.body.key)
  }
}


class Foo {

  def run1(file: File) = {
    // VIOLAZ
    val docBuilderFactory = DocumentBuilderFactory.newInstance()
    val docBuilder = docBuilderFactory.newDocumentBuilder()
    val doc = docBuilder.parse(file)
    doc.getDocumentElement().normalize()
    val foobarList = doc.getElementsByTagName("Foobar")
    foobarList
  }
  def run2(file: File) = {
    // VIOLAZ
    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = docBuilder.parse(file)
    doc.getDocumentElement().normalize()
    val foobarList = doc.getElementsByTagName("Foobar")
    foobarList
  }
  def okRun1(file: File) = {
    // ok
    val docBuilderFactory = DocumentBuilderFactory.newInstance()
    val docBuilder = docBuilderFactory.newDocumentBuilder()
    docBuilder.setXIncludeAware(true)
    docBuilder.setNamespaceAware(true)
    docBuilder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    docBuilder.setFeature("http://xml.org/sax/features/external-general-entities", false)
    docBuilder.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    val doc = docBuilder.parse(file)
    doc.getDocumentElement().normalize()
    val foobarList = doc.getElementsByTagName("Foobar")
    foobarList
  }
  def okRun2(file: File) = {
    // ok
    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    docBuilder.setXIncludeAware(true)
    docBuilder.setNamespaceAware(true)
    docBuilder.setFeature("http://xml.org/sax/features/external-general-entities", false)
    docBuilder.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    docBuilder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    val doc = docBuilder.parse(file)
    doc.getDocumentElement().normalize()
    val foobarList = doc.getElementsByTagName("Foobar")
    foobarList
  }
}


class Foo {
  def run1(file: String) = {
    val factory = XMLInputFactory.newInstance()
    val fileReader = new FileReader(file)
    // VIOLAZ
    val xmlReader = factory.createXMLStreamReader(fileReader)
    doSmth(xmlReader)
  }
  def run2(file: String) = {
    val factory = XMLInputFactory.newFactory()
    val fileReader = new FileReader(file)
    // VIOLAZ
    val xmlReader = factory.createXMLStreamReader(fileReader)
    doSmth(xmlReader)
  }
  def okRun1(file: String) = {
    val factory = XMLInputFactory.newInstance
    factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false)
    val fileReader = new FileReader(file)
    // ok
    val xmlReader = factory.createXMLStreamReader(fileReader)
    doSmth(xmlReader)
  }
  def okRun2(file: String) = {
    val factory = XMLInputFactory.newFactory()
    factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false)
    val fileReader = new FileReader(file)
    // ok
    val xmlReader = factory.createXMLStreamReader(fileReader)
    doSmth(xmlReader)
  }
}


class Foo {

def okRun2(xmlFilePath:String) = {
    val file = new File(xmlFilePath)
    val factory = SAXParserFactory.newInstance()
    val saxReader = factory.newSAXParser()
    // ok
    saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false)
    saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    val doc = Try(saxReader.read(file))
    val result = doc match  {
      case Success(r) => r
      case Failure(exception) => println("getDocumentExcetion:" + exception.getMessage)
    }
    result.asInstanceOf[Document]
  }
  
  def run1(xmlFilePath:String) = {
    val file = new File(xmlFilePath)
    val saxReader = new SAXReader()
    // VIOLAZ
    val doc = Try(saxReader.read(file))
    val result = doc match  {
      case Success(r) => r
      case Failure(exception) => println("getDocumentExcetion:" + exception.getMessage)
    }
    result.asInstanceOf[Document]
  }
  def run2(xmlFilePath:String) = {
    val file = new File(xmlFilePath)
    val factory = SAXParserFactory.newInstance()
    val saxReader = factory.newSAXParser()
    // VIOLAZ
    val doc = Try(saxReader.read(file))
    val result = doc match  {
      case Success(r) => r
      case Failure(exception) => println("getDocumentExcetion:" + exception.getMessage)
    }
    result.asInstanceOf[Document]
  }
  def run4(xmlFilePath:String) = {

    val file = new File(xmlFilePath)
    val saxReader = SAXParserFactory.newInstance().newSAXParser()
    // VIOLAZ
    val doc = Try(saxReader.read(file))
    val result = doc match  {
      case Success(r) => r
      case Failure(exception) => println("getDocumentExcetion:" + exception.getMessage)
    }
    result.asInstanceOf[Document]
  }
  def okRun1(xmlFilePath:String) = {
    val file = new File(xmlFilePath)
    val saxReader = new SAXReader()
    saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false)
    saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
                        // ok
    val doc = Try(saxReader.read(file))
    val result = doc match  {
      case Success(r) => r
      case Failure(exception) => println("getDocumentExcetion:" + exception.getMessage)
    }
    result.asInstanceOf[Document]
  }
  
}


object Smth {
  def call1(request_url: String): Future[Unit] = { // url Untrusted se Opzioni Analisi-e Funzioni Public Untrusted
    // VIOLAZ
    val req = url(request_url)
    val data = Http.default(req OK as.String)
    data
  }
  def call2(): Future[Unit] = {
    // ok
    val req = url("https://www.google.com")
    val data = Http.default(req OK as.String)
    data
  }
}
object FooBar {
  def call1(request_url: String): Future[Unit] = { // url Untrusted se Opzioni Analisi-e Funzioni Public Untrusted
    // VIOLAZ
    val request = url(request_url).POST.setHeader("Content-Type", "application/json")
    val res = Http(request OK as.String)
    res
  }
  def call2(): Future[Unit] = {
    // ok
    val request = url("https://www.google.com").POST.setHeader("Content-Type", "application/json")
    val res = Http(request OK as.String)
    res
  }
}
@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents,
  implicit val ec: ExecutionContext
) extends BaseController {
  def req1(request_url: String) = Action.async { implicit request: Request[AnyContent] =>
    // VIOLAZ
    val request = url(request_url).POST.setHeader("Content-Type", "application/json")
    val res = Http(request OK as.String)
    Ok("ok")
  }
  def req2() = Action.async { implicit request: Request[AnyContent] =>
    // ok
    val request = url("https://www.google.com").POST.setHeader("Content-Type", "application/json")
    val res = Http(request OK as.String)
    Ok("ok")
  }
}


class Foo3 {
  def run1(message: String) = { // message Untrusted se Opzioni Analisi-e Funzioni Public Untrusted
    import sys.process._
    // VIOLAZ
    Seq("sh", "-c", message).!
  }
  def run2(message: String) = { // message Untrusted se Opzioni Analisi-e Funzioni Public Untrusted
    import sys.process._
    // VIOLAZ
    val result = Seq("bash", "-c", message).!!
    return result
  }
  def run3(message: String) = {
    import sys.process._
    // ok
    Seq("ls", "-la").!!
  }
  def run4(message: String) = {
    import sys.process._
    // ok
    Seq("sh", "-c", "ls").!!
  }
  def run5(message: String) = {
    import sys.process._
    // ok
    Seq("sh", "-c", message)
  }
  def run6(message: String) = {
    // ok
    val result = Seq("bash", "-c", message).!!
    return result
  }
}


class Bar extends Foo { // VIOLAZ la class deve essere final

def get(value: T): T = value
{
<specialized> class AllTypesSpecialized$mcB$sp extends AllTypesSpecialized {  // VIOLAZ
    override <specialized> def get(value: Byte): Byte = AllTypesSpecialized$mcB$sp.this.get$mcB$sp(value);
    override <specialized> def get$mcB$sp(value: Byte): Byte = value;
    override <bridge> <specialized> <artifact> def get(value: Object): Object = scala.Byte.box(AllTypesSpecialized$mcB$sp.this.get(scala.Byte.unbox(value)));
    <specialized> def <init>(): AllTypesSpecialized$mcB$sp = {  // VIOLAZ
      AllTypesSpecialized$mcB$sp.super.<init>();
      ()
    }
  }
  }

	private def isAdmin(auth: String): Boolean = try {
		val bis = new ByteArrayInputStream(Base64.getDecoder.decode(auth))
		val objectInputStream = new ObjectInputStream(bis)
		val authToken = objectInputStream.readObject.asInstanceOf[Bean1599]
		authToken.name.equals("root")
	  } catch {
		case ex: Exception =>
		  System.out.println(" cookie cannot be deserialized: " + ex.getMessage)
		  false
	  }
	  
	def createCustomer = Action { implicit request =>
		val customer1 = Customer.form.bindFromRequest.get
		customer1.save
		//response().setHeader("Location", String.format("%s/customers/%s",
		//    request().path(), customer1.getId()));

		Created(Json.toJson(customer1))
		//return created(new Html(Json.toJson(customer1).toString()));
		Cipher.getInstance("RSA/NONE/NoPadding");
		Cipher c = Cipher.getInstance("DESede/ECB/PKCS5Padding") // VIOLAZ
		val md = MessageDigest.getInstance("MD5") // VIOLAZ
		val md = MessageDigest.getInstance("SHA-1") // VIOLAZ
		val cipher = Cipher.getInstance("Blowfish") // VIOLAZ
		Socket soc = new Socket("www.google.com",80)
		
				if (strings.indexOf(color, 1) > 0) { // VIOLAZ
			// …
		}
		if (strings.indexOf(color) > 2) { // VIOLAZ
			// …
		}
		if (name.indexOf("ish", 1) >= 0) {  // OK
		  // …
		}
		if (name.indexOf("hma", 1) > -1) { // OK
		  // …
		}
		
		if (name.indexOf("ae") > -1) { // VIOLAZ
		  // ...
		}
		if (name.indexOf("ae", 2) > -1) {  // OK
		  // ...
		}

		password = config.get[String]("sfdc.password")
		val hashedPassword = DigestUtils.sha1Hex(password) // VIOLAZ

		val t: Traversable[_] = Map((1 to 10) map ((_, "x")): _*)
		val set = Set(1, 9, 10, 22)
		val list = List(3, 4, 5, 10)
		t.size // VIOLAZ
		t.view.slice(0,2).size // OK
		t.take(2).size //OK
		set.size // VIOLAZ
		list.size // VIOLAZ
		
		val nums = Array("1", "22", "3", "444")
		Console.println(String.format("This string will %s", nums:_*)) // VIOLAZ

		val s1 = Array(1, 2, 3, 4, 5)  
		val result = s1.toString  // VIOLAZ



	  }
  
	def downloadXML(inputStream: InputStream): SAXParser = {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(inputStream, customHandler);  // VIOLAZ
		
		Logger.info(s"Password is ${config.get[String]("sfdc.password")}") // VIOLAZ
		val prop = new Properties();
		prop.load(new FileInputStream("config.properties"));
		print(prop.getProperty("sfdc.password ")); // VIOLAZ
		val pass = prop.getProperty("sfdc.password");
		sendRequest(pass); // VIOLAZ

		pass = config.get[String]("sfdc.password")
		if (pass == "myPassword123") { //VIOLAZ
			true
		  } else {
			false
		  }

		pass = "<password>";
		val username = "admin";
		val password = StdIn.readLine();
		val connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", username, password);

	}

	def downloadXML2(inputStream: InputStream): SAXParser = {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(inputStream, customHandler); // OK è presente la setFeature col parametro giusto
	}

	def downloadXML3(inputStream: InputStream): SAXParser = {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(inputStream, customHandler); // OK è presente la setFeature col parametro giusto
	}

	def downloadXML4(inputStream: InputStream): SAXParser = {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
		spf.setFeature(FEATURE, true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(inputStream, customHandler); // OK è presente la setFeature col parametro giusto
	}

	def displayMessage(message: String): NodeSeq = {  // VIOLAZ se metodi public sono dichiarati Untrustred
	  <div>{message}</div>
	  
	  
	}
	
	def displayMessage2(message: String): NodeSeq = {  //OK message validato da Text
	  <div>{Text(message)}</div>
	}

	def getUserData(userId: String): String = {
		s"SELECT * FROM users WHERE id = '$userId'"  // VIOLAZ solo se I metodi public sono configurati come untrusted
		
		val line = StdIn.readLine()
		printf("<p>%s</p>", line)  // VIOLAZ

		val scanner = new Scanner(System.in)
		println("Enter username:")
		val username = scanner.nextLine()
		println("Enter password:")
		val password = scanner.nextLine()
		val isAuthenticated = authenticate(username, password)
		
		val url = StdIn.readLine()
		data = open(url).read // VIOLAZ

		logger = Logger.new('application.log')  // logger è un oggetto di tipo Logger
		val user_id = StdIn.readLine() // user_id è untrusted
		logger.info("User #{user_id} performed activity: 2") // VIOLAZ 

		val data = StdIn.readLine()
		val stream = new ByteArrayInputStream(data)
		val objectInputStream = new ObjectInputStream(stream)
		val obj = objectInputStream.readObject()  // VIOLAZ manca la validazione di data


	}


   def display(var x: Int, var y: Int): Unit = {

   val line = scala.io.StdIn.readLine()
   db.run(sql"""SELECT * FROM Objects WHERE some_field = '#$line'""").as[Invoice] // VIOLAZ line untrusted da readLine
   db.run (line)
	
	val results = statement.executeQuery(line)  // VIOLAZ line untrusted da readLine

	logger = Logger.new('application.log')
	val pass = getPassword()
	logger.info("Login attempt - Password: #{pass}")  // VIOLAZ

	val password = Base64.decode(prop.getProperty("password"))
	ws.url(url).withAuth("john", password, WSAuthScheme.BASIC)  // VIOLAZ
	
	dbmsLog.println(id+":"+pass+":"+type+":"+tstamp)  // VIOLAZ
   
    println("Inside the Example class")
	
	printf("Pass = %s", pass) // VIOLAZ
	Files.write(Paths.get("file.txt"), pass.getBytes(StandardCharsets.UTF_8)) // VIOLAZ

	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID, maxAge = Some(60*60*24*365*10))) // VIOLAZ
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID, maxAge = 0)) //OK
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID)) //OK, senza maxAge
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID, path = "/")) //VIOLAZ
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID, domain = Some(".example.com"))) // VIOLAZ 
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID, domain = Some(".example.com"))) // VIOLAZ manca il SameSite
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID, domain = Some(".example.com"), sameSIte=null)) // VIOLAZ SameSite a null
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID, httpOnly = false)) // VIOLAZ
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID)) // VIOLAZ
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID, secure = false)) // VIOLAZ
	Ok(Html(command)).withCookies(Cookie("sessionID", sessionID)) // VIOLAZ
	connection.setHostnameVerifier((_, _) => true)  //VIOLAZ
	Http.postData(url, payload).options(HttpOptions.allowUnsafeSSL, HttpOptions.readTimeout(5000))  //VIOLAZ
	val myHttp = Http.withConfiguration(config => config.setAcceptAnyCertificate(true))  // VIOLAZ

	val xml = XML.loadString("""
	  <?xml version="1.0" encoding="UTF-8"?>
	  <!DOCTYPE foo [
		<!ENTITY xxe SYSTEM "file:///etc/passwd"> <!-- VIOLAZ -->
	  ]>
	  <root>&xxe;</root>
	""")
	
    }
	

}
 


class Point(var x: Int, var y: Int):
	case class Person(name: String, age: Int)  // VIOLAZ
  def move(dx: Int, dy: Int): Unit =
    x = x + dx
    y = y + dy

  override def toString: String =
    s"($x, $y)"
end Point

class Example {
  case class Person(name: String, age: Int)  // VIOLAZ
{
  	def name: String
  	def age: Int
}
   def display(): Unit = {
    println("Inside the Example class")
    }
}

class Example1 {
def someFunction(param1: T1, param2: T2) = {  // VIOLAZ
  println(s"Hello, $T1, $T2")
}
def someFunction2(param1: T1, param2: T2): Result = {  //OK ha il codice di ritorno
  println(s"Hello, $T1, $T2")
}
def fortuneCookieJoke(message: String) = message + " in bed."
{
}  //OK ha l’=

var sum = 0
case class Sample(str: String, var number: Int)  // VIOLAZ
for (elem <- elements) {
  sum += elem.value  //VIOLAZ sum è una var cambiata dentro una for
}

def compute(x) = {
  var result = resultFrom(x)
  if(needToAddTwo) {
    result += 2 //VIOLAZ result è una var cambiata dentro una if
  }
  else {
    result += 1 //VIOLAZ result è una var cambiata dentro una if
  }
  result
}

def computeResult(x) = {
  val r = resultFrom(x)
  if (needToAddTwo)
    r + 2  // OK non c’è l’=
  else
    r + 1 // OK non c’è l’=
}

trait Foo {
  var value: String   //VIOLAZ value è astratta, non c’è l’= e sta dentro una trait
}
trait Foo2 {
  def value: String  //OK non è una var
}

abstract class myauthor 
{ 
    var details: String   //VIOLAZ details è astratta, non c’è l’= e sta dentro una classe astratta
}

trait PersonLike {
  def name: String
  def age: Int
}
case class Person(name: String, age: Int)
  extends PersonLike

try {
 something()
} catch {
 case ex: Throwable =>  // VIOLAZ
   blaBla()
}

def hello(name: String) =
  if (name != null)  // VIOLAZ
    println(s"Hello, $name")
  else
    println("Hello, anonymous")

val someValue: Option[Double] = ???
val result = someValue.get + 1  // VIOLAZ get su una variabile di tipo Option

val json: Any = ??? // VIOLAZ
class Example {
  def display(): Unit = {
    println("Inside the Example class")
  }
}
val example = new Example
println(example.isInstanceOf[AnyRef]) // VIOLAZ
val index = list.find(someTest).getOrElse(-1) // VIOLAZ

}

package foo
package object bar {
  case object FooBar  //VIOLAZ
}

package object bar2 {
  case class Person(name: String, age: Int)  // VIOLAZ
{
  	def name: String
  	def age: Int
}
}

package object bar3 {
class Example {  // VIOLAZ
  		def display(): Unit = {
    		println("Inside the Example class")
  		}
}
}

package object dsl {
  implicit class DateTimeAugmenter(val date: Datetime) extends AnyVal {  // OK la class è implicit
    def yesterday: DateTime = date.plusDays(-1)
  }
}

val userList: List[User] = ???
val firstName = userList.head.firstName // VIOLAZ

case class Foo(v:Int)  // VIOLAZ
final case class User(name: String, id: Long) // OK
package object barx {
  case class Personbar(name: String, age: Int)  // case class nestata non dà questa VIOLAZ, dà già quella al punto 13
{
  	def name: String
  	def age: Int
}
}
class ExampleX {
  case class PersonX(name: String, age: Int)  // case class nestata non dà questa VIOLAZ, dà già quella al punto 12
{
  	def name: String
  	def age: Int
	
	object HelloWorldApp extends App {
		println("hello, world!")
	}


	def isEven(i: Int): Boolean = i % 2 == 1  //VIOLAZ
	if (x % 2 == 1)   { //VIOLAZ
	  println("Number is odd!")
	}

	val a: Array[Int] = Array(1,2,4,5)
	val b: Array[Int] = Array(1,2,4,5)
	a==b // VIOLAZ
	if (a(1).deep == b(1).deep)   { // OK, c’è la deep
	println("Array elements are equal !")
	}

	Seq.empty[Int].head  // VIOLAZ
	Seq.empty[Int].init // VIOLAZ
	Seq.empty[Int].last // VIOLAZ
	Seq.empty[Int].reduce(_ + _)  // VIOLAZ
	Seq.empty[Int].tail // VIOLAZ
	Left(1).right.get // VIOLAZ
	Right(1).left.get // VIOLAZ
	None.get //VIOLAZ
	scala.util.Failure(new Exception).get  // VIOLAZ
	(Failure(new Exception): Try[Int]).get(  // VIOLAZ
	e => s"Found an error: '${e.getMessage}'",
		i => s"Found an int: '$i'"
	)

	val prop = new Properties();
	prop.load(new FileInputStream("config.properties"));
	val password = Base64.decode(prop.getProperty("password"));
	ws.url(url).withAuth("john", password, WSAuthScheme.BASIC)  // VIOLAZ
	val pass = Base64.decode(getPassword())
	DriverManager.getConnection(url, usr, password); // VIOLAZ

	ws.url(url).withAuth("john", "", WSAuthScheme.BASIC)  // VIOLAZ
	DriverManager.getConnection(url, usr, ""); // VIOLAZ

	ws.url(url).withAuth("john", null, WSAuthScheme.BASIC) // VIOLAZ
	DriverManager.getConnection(url, usr, null); // VIOLAZ


	def foo1() = if(false) throw new Exception else 2 // VIOLAZ
	def foo2() = {
	  val a = throw new Exception // VIOLAZ
	  if (false) a  else 2
	}
	



	def doSomething() = {
		var name = ""
		name = name  // VIOLAZ
	}

	def doSomething1() = {
		var name = ""
		this.name = name  // OK
		if (name == name)
		{
		}
	}

		val url = Uri.from(scheme = "http", host = "192.0.2.16", port = 80, path = "/")
		val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))  //VIOLAZ
		val ftpSettings = FtpSettings //VIOLAZ
		  .create(InetAddress.getByName(HOSTNAME))
		  .withPort(PORT)
		  .withCredentials(CREDENTIALS)
		  .withBinary(true)
		  .withPassiveMode(true)
		  // only useful for debugging
		  .withConfigureConnection((ftpClient: FTPClient) => {
			ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true))
		  })


	val ip = "112.128.1.43" // VIOLAZ
	
	def foo(n: Int, m: Int): Unit = {
  n match {
    case 0 => m match {  // VIOLAZ
        case 0 =>
        // ...
      }
    case 1 =>
    // ...
  }
}


}
}

def downloadAndExecute(url: String): Unit = {
  val command = s"curl $url | bash"  
  val downloadedCode = s"curl $url | bash"  
  command.!  // VIOLAZ
  Process(command)!  // VIOLAZ
  val downloadedCode = command.!! // cattura l’output
  if (verifyIntegrity(downloadedCode, checksum)) {
    downloadedCode.!     // OK output curl verificato con funzione di validazione verify*
  } else {
    throw new SecurityException("Code integrity check failed")
  }
}

object Smth {
  def run1(token: String) = {
    val algo = JwtAlgorithm.HS256
    val secretKey = "foobar" // hardcoded
    // VIOLAZ
    JwtArgonaut.decodeJson(token, secretKey, algo)
  }
  def run2(token: String) = {
    val algo = JwtAlgorithm.HS256
    // ok
    JwtArgonaut.decodeJson(token, getSecretFromEnv(), algo)
  }
}
class NewRule {
  private val JWT_KEY = "foobar"
  def run1() = {
    val claim = Json.obj(("user", 1), ("nbf", 1431520421))
    val key1 = " K7aToyGnmyxFF+GHzMOsArYMibOqtWn3J9DxJGN2CczFIwc12/dAty4eV5tRtvPeLEC2cSiRtrtGBHuET8buSQ=="
    val algo = JwtAlgorithm.HS256
    // ok hardened finisce per ==
    val token = JwtJson.encode(claim, key1, algo)
    // ok keyFromEnv non è hardened
    JwtJson.decodeJson(token, keyFromEnv, Seq(JwtAlgorithm.HS256))
  }
  
  def run2() = {
    val claim = Json.obj(("user", 1), ("nbf", 1431520421))
    val key2 = getSecretFromEnv()
    val algo = JwtAlgorithm.HS256
    // VIOLAZ
    val token = JwtCirce.encode(claim, this.JWT_KEY, algo)
    // VIOLAZ
    JwtCirce.decodeJson(token, this.JWT_KEY, Seq(JwtAlgorithm.HS256))
    // VIOLAZ
    JwtCirce.decodeJson(token, “secretkey”, Seq(JwtAlgorithm.HS256))
  }
  
  def run3() = {
    val claim = Json.obj(("user", 1), ("nbf", 1431520421))
    val key2 = getSecretFromEnv()
    val algo = JwtAlgorithm.HS256
    // ok
    val token = JwtJson.encode(claim, key2, algo)
    // ok
    JwtJson.decodeJson(token, key2, Seq(JwtAlgorithm.HS256))
  }
  
}


class Foo {
  def run1(command: String, arg1: String) = {
    import sys.process._
    // VIOLAZ
    Seq(command, arg1).!
  }
  def run2(command: String) = {
    import sys.process._
    // VIOLAZ
    val result = Seq(command, "--some-arg").!!
    return result
  }
  def run3(message: String) = {
    import sys.process._
    // ok
    Seq("ls", "-la").!!
  }
  def run4(message: String) = {
    import sys.process._
    // ok
    Seq("sh", "-c", "ls").!!
  }
  def run5(message: String) = {
    import sys.process._
    // ok
    Seq(message, "123")
  }
  def run6(command: String) = {
    // ok non c’è la import
    val result = Seq(command, "--some-arg").!!
    return result
  }
}

