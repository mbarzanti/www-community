import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES;
import org.jboss.seam.log.Logging;
import org.jboss.seam.log.Log;
import java.security.Key;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import static javax.xml.stream.XMLInputFactory.SUPPORT_DTD;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.mapstruct.AfterMapping;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex;
import javax.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class NuoveRegole extends HttpServletRequestWrapper {
    private static Pattern[] patterns = new Pattern[]{
        // VIOLAZ Script fragments
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        // VIOLAZ src='...'
        Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // VIOLAZ lonely script tags
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // VIOLAZ eval(...)
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // VIOLAZ expression(...)
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // VIOLAZ javascript:...
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // VIOLAZ vbscript:...
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // VIOLAZ onload(...)=...
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };
	@Mapper
	public abstract class CarsMapper {
		@BeforeMapping
		protected void enrichDTOWithFuelType(Car car, @MappingTarget CarDTO carDto) {
			if (car instanceof ElectricCar) {
				carDto.setFuelType(FuelType.ELECTRIC);
			}
			if (car instanceof BioDieselCar) { 
				carDto.setFuelType(FuelType.BIO_DIESEL);
			}
		}

		@AfterMapping
		protected void convertNameToUpperCase(@MappingTarget CarDTO carDto) {
			carDto.setName(carDto.getName().toUpperCase());
		}

		// public abstract CarDTO toCarDto(Car car);
	}
    private String stripXSS(String value) {
        if (value != null) {
            // ok: Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // ok: Avoid anything in a src='...' type of expression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            //ok	
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // ok: Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // ok: Avoid eval(...) expressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // ok: Avoid expression(...) expressions
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // ok: Avoid javascript:... expressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // ok: Avoid vbscript:... expressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // ok: Avoid onload= expressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;
    }
}


class Cls {

// VIOLAZ
    public static Object handleXml(InputStream in) {
        XMLDecoder d = new XMLDecoder(in);
        try {
            Object result = d.readObject(); //Deserialization happen here
            return result;
        }
        finally {
            d.close();
        }
    }
    // ok
    public static Object handleXml1() {
        XMLDecoder d = new XMLDecoder("<safe>XML</safe>");
        try {
            Object result = d.readObject();
            return result;
        }
        finally {
            d.close();
        }
    }
    // ok
    public static Object handleXml2() {
        String strXml = "<safe>XML</safe>";
        XMLDecoder d = new XMLDecoder(strXml);
        try {
            Object result = d.readObject();
            return result;
        }
        finally {
            d.close();
        }
    }



// VIOLAZ
    private String encodeURLRewrite(HttpServletResponse resp, String url) {
        return resp.encodeURL(url);
    }
    // ok
    public String encodeRedirectURLRewrite(SomeDifferentRequest resp, String url) {
        return resp.encodeURL(url);
    }
   // VIOLAZ
    public String encodeUrlRewrite(HttpServletResponse resp, String url) {
        return resp.encodeUrl(url); //Deprecated
    }
    // VIOLAZ
    public String encodeRedirectURLRewrite(HttpServletResponse resp, String url) {
        return resp.encodeRedirectURL(url);
    }
    // VIOLAZ
    public String encodeRedirectUrlRewrite(HttpServletResponse resp, String url) {
        return resp.encodeRedirectUrl(url); //Deprecated
    }
    // ok
    public String encodeRedirectUrlRewrite(HttpServletResponse resp, String url) {
        return resp.getHeader(url);
    }
	
	public static void notOk() throws IOException {
        // VIOLAZ
        Files.setPosixFilePermissions(Paths.get("/var/opt/app/init_script.sh"), PosixFilePermissions.fromString("rw-rw-rw-"));
        // VIOLAZ
        Files.setPosixFilePermissions(Paths.get("/var/opt/configuration.xml"), PosixFilePermissions.fromString("rw-rw-r--"));
    }
    public static void notOk2() throws IOException {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);  // ok
        perms.add(PosixFilePermission.OWNER_WRITE); // ok
        perms.add(PosixFilePermission.OWNER_EXECUTE); // ok
        perms.add(PosixFilePermission.GROUP_READ); // ok
        perms.add(PosixFilePermission.GROUP_WRITE); // ok
        perms.add(PosixFilePermission.GROUP_EXECUTE); // ok
        // VIOLAZ
        perms.add(PosixFilePermission.OTHERS_READ);
        // VIOLAZ
        perms.add(PosixFilePermission.OTHERS_WRITE);
        // VIOLAZ
        perms.add(PosixFilePermission.OTHERS_EXECUTE);
        Files.setPosixFilePermissions(Paths.get("/var/opt/app/init_script.sh"),perms);
    }
    public static void ok() throws IOException {
        // ok
        Files.setPosixFilePermissions(Paths.get("/var/opt/configuration.xml"), PosixFilePermissions.fromString("rw-rw----"));
        // ok
        Files.setPosixFilePermissions(Paths.get("/var/opt/configuration.xml"), PosixFilePermissions.fromString("rwxrwx---"));
    }


	// VIOLAZ
    public void unsafeOgnlReflectionProvider(String input, OgnlReflectionProvider reflectionProvider, Class type) throws IntrospectionException, ReflectionException {
        reflectionProvider.getGetMethod(type, input);
    }
    // VIOLAZ
    public void unsafeOgnlReflectionProvider1(String input, ReflectionProvider reflectionProvider) throws IntrospectionException, ReflectionException {
        reflectionProvider.getValue(input, null, null);
    }
    // VIOLAZ
    public void unsafeOgnlReflectionProvider2(String input, OgnlUtil reflectionProvider) throws IntrospectionException, ReflectionException {
        reflectionProvider.setValue(input, null, null,null);
    }
    // VIOLAZ
    public void unsafeOgnlReflectionProvider3(String input, OgnlTextParser reflectionProvider) throws IntrospectionException, ReflectionException {
        reflectionProvider.evaluate( input );
    }
    // ok
    public void safeOgnlReflectionProvider1(OgnlReflectionProvider reflectionProvider, Class type) throws IntrospectionException, ReflectionException {
        String input = "thisissafe";
        reflectionProvider.getGetMethod(type, input);
    }
    // ok
    public void safeOgnlReflectionProvider2(OgnlReflectionProvider reflectionProvider, Class type) throws IntrospectionException, ReflectionException {
        reflectionProvider.getField(type, "thisissafe");
    }

    public void ldapSearchEntryPoison(Environment env) {
        DirContext ctx = new InitialDirContext();
        // VIOLAZ
        ctx.search(query, filter, new SearchControls(scope, countLimit, timeLimit, attributes,
            true, //Enable object deserialization if bound in directory
            deref));
			
		String salt = RandomUtil.createSalt();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(user.getPassword());
        // VIOLAZ
        user.setPassword(md.digest(), salt);
        MessageDigest md2= MessageDigest.getInstance("SHA-256");
        Md2.update(user.getPassword());
        // ok
        user.setPassword(md2.digest(), salt);

    }
    public void ldapSearchEntryPoisonViaSetter(Environment env) {
        DirContext ctx = new InitialDirContext();
        // VIOLAZ meno di 5 parametri
        SearchControls ctrls = new SearchControls();
        ctrls.setReturningObjFlag(true);
    }
    public void ldapSearchSafe(Environment env) {
        DirContext ctx = new InitialDirContext();
        ctx.search(query, filter,
            new SearchControls(scope, countLimit, timeLimit, attributes,
            false, //OK
            deref));
    }
}


class Cls {
	
	public void unsafe_jdbc_queryForObject_3(String paramName) {
        JdbcTemplate jdbc = new JdbcTemplate();
        System.out.println("Hello");
        // VIOLAZ
        StringBuilder query = new StringBuilder("select count(*) from Users");
        query.append( "where name = '"+paramName+"'");
        int count = jdbc.queryForObject(query, Integer.class);
    }
	
	public void unsafe_jdbc_queryForObject_1(String paramName) {
        JdbcTemplate jdbc = new JdbcTemplate();
        System.out.println("Hello");
        // VIOLAZ
        int count = jdbc.queryForObject("select count(*) from Users where name = '"+paramName+"'", Integer.class);
    }
    public void unsafe_jdbc_queryForObject_2(String paramName) {
        JdbcTemplate jdbc = new JdbcTemplate();
        System.out.println("Hello");
        // VIOLAZ
        String query = "select count(*) from Users where name = '"+paramName+"'";
        int count = jdbc.queryForObject(query, Integer.class);
    }

    public void unsafe_jdbc_queryForList_1(String paramName) {
        JdbcTemplate jdbc = new JdbcTemplate();
        System.out.println("Hello");
        List<Users> users = new ArrayList<>();
        // VIOLAZ
        String query = "select count(*) from Users where name = '"+paramName+"'";
        List<Map<String, Object>> rows =  jdbc.queryForList(query);
    }
    public void unsafe_jdbc_queryForList_2(String paramName) {
        JdbcTemplate jdbc = new JdbcTemplate();
        System.out.println("Hello");
        List<Users> users = new ArrayList<>();
        // VIOLAZ
        List<Map<String, Object>> rows =  jdbc.queryForList("select count(*) from Users where name = '"+paramName+"'");
    }
    public void unsafe_jdbc_update(String paramName, String paramSalary) {
        JdbcTemplate jdbc = new JdbcTemplate();
        System.out.println("Hello");
        // VIOLAZ
        String updateQuery = "update Users set salary = '"+paramSalary+"' where name = '"+paramName+"'";
        jdbc.update(updateQuery);
    }
    public void safe(String paramName) {
        JdbcTemplate jdbc = new JdbcTemplate();
        // ok
        int count = jdbc.queryForObject("select count(*) from Users where name = ?", Integer.class, paramName);
    }
	
    public void sendEmail(String username, String password) {
        Email email = new SimpleEmail();
        email.setHostName("smtp.servermail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(username, password));
        email.setSSLOnConnect(true);
        email.setFrom("user@gmail.com");
        email.setSubject("TestMail");
        email.setMsg("This is a test mail ... :-)");
        email.addTo("foo@bar.com");
        // VIOLAZ
        email.send();
    }
    public void sendEmailSafe(String username, String password) {
        Email email = new SimpleEmail();
        email.setHostName("smtp.servermail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(username, password));
        email.setSSLOnConnect(true);
        email.setSSLCheckServerIdentity(true);
        email.setFrom("user@gmail.com");
        email.setSubject("TestMail");
        email.setMsg("This is a test mail ... :-)");
        email.addTo("foo@bar.com");
        email.send(); // ok
    }
    public void sendEmailSafe(String username, String password) {
        Email email = new SimpleEmail();
        email.setHostName("smtp.servermail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(username, password));
        email.setSSLOnConnect(true);
        email.setSSLCheckServerIdentity(false);
        email.setFrom("user@gmail.com");
        email.setSubject("TestMail");
        email.setMsg("This is a test mail ... :-)");
        email.addTo("foo@bar.com");
        email.send(); // VIOLAZ
    }
}


class ElExpressionSample {
    // VIOLAZ
    public void unsafeEL(String expression) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
        ELContext elContext = context.getELContext();
        ValueExpression vex = expressionFactory.createValueExpression(elContext, expression, String.class);
        String result = (String) vex.getValue(elContext);
        System.out.println(result);
    }
    // ok
    public void safeEL() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
        ELContext elContext = context.getELContext();
        ValueExpression vex = expressionFactory.createValueExpression(elContext, "1+1", String.class);
        String result = (String) vex.getValue(elContext);
        System.out.println(result);
    }
    // VIOLAZ
    public void unsafeELMethod(ELContext elContext,ExpressionFactory expressionFactory, String expression) {
        expressionFactory.createMethodExpression(elContext, expression, String.class, new Class[]{Integer.class});
    }
    //ok
    public void safeELMethod(ELContext elContext,ExpressionFactory expressionFactory) {
        expressionFactory.createMethodExpression(elContext, "1+1", String.class,new Class[] {Integer.class});
    }
    // VIOLAZ
    private void unsafeELTemplate(String message, ConstraintValidatorContext context) {
         context.disableDefaultConstraintViolation();
         context
             .someMethod()
             .buildConstraintViolationWithTemplate(message)
             .addConstraintViolation();
    }
    //ok
    private void safeELTemplate(String message, ConstraintValidatorContext context) {
         context.disableDefaultConstraintViolation();
         context
             .someMethod()
             .buildConstraintViolationWithTemplate("somestring")
             .addConstraintViolation();
    }
}


interface IBSidesService extends Remote {
   boolean registerTicket(String ticketID) throws RemoteException; // VIOLAZ
   void vistTalk(String talkname) throws RemoteException;
   void poke(Attendee attende) throws RemoteException;
   void poke1(Object attende) throws RemoteException; // VIOLAZ
}
interface IBSidesServiceOK extends Remote {
   boolean registerTicket(long ticketID) throws RemoteException; // OK
   void vistTalk(long talkID) throws RemoteException;
   void poke(int attende) throws RemoteException;
   void poke1(StringBuilder attende) throws RemoteException; // VIOLAZ
}


class MaybeBadXMLInputFactory {
    public void foobar() {
        // ruleid:xmlinputfactory-possible-xxe
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory(); // VIOLAZ
    }
	

}


class SnakeYamlTestCase {
	
	final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
	
	public static String badHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] resultBytes = md.digest(password.getBytes("UTF-8"));
			StringBuilder stringBuilder = new StringBuilder();
			for (byte b : resultBytes) {
				stringBuilder.append(Integer.toHexString(b & 0xFF));  // VIOLAZ
			}
			return stringBuilder.toString();
		}


    public void unsafeLoad(String toLoad) {
        Yaml yaml = new Yaml(); // VIOLAZ
        yaml.load(toLoad);
		xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", true); // VIOLAZ
		xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, true); // VIOLAZ
		xmlInputFactory.setProperty(SUPPORT_DTD, true); // VIOLAZ
		xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false); // OK
		xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false); // OK
		xmlInputFactory.setProperty(SUPPORT_DTD, false); // OK

    }
    public void safeConstructorLoad(String toLoad) {
        Yaml yaml = new Yaml(new SafeConstructor()); // OK
        yaml.load(toLoad);
    }
    public void customConstructorLoad(String toLoad, Class goodClass) { 
        Yaml yaml = new Yaml(new Constructor(goodClass)); // OK
        yaml.load(toLoad);
     }
}


class $JMS_LISTENER implements MessageListener {
        public void onMessage(Message $JMS_MSG) {
			TextMessage msg = (TextMessage) message;
			Object o = msg.getObject(); // VIOLAZ
			Income income = (Income) msg.getObject(); // VIOLAZ
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enableDefaultTyping(); // VIOLAZ

        }
		
	protected void danger(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			String input1 = req.getParameter("input1");
			resp.getWriter().write(input1); // VIOLAZ
		}
		protected void danger2(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			String input1 = req.getParameter("input1");
			PrintWriter writer = resp.getWriter();
			writer.write(input1); // VIOLAZ
		}
		protected void ok(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			String input1 = req.getParameter("input1");
			resp.getWriter().write(Encode.forHtml(input1)); // OK usata la Encode.forHtml
		}
		
   }


class MyAction implements PrivilegedAction<Void> {
        public Void run() {
           // VIOLAZ
            System.loadLibrary("awt");
            return null; // nothing to return
        }
		
		public void somemethod() {
        MyAction mya = new MyAction(); 
        AccessController.doPrivileged(mya); // VIOLAZ
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                System.loadLibrary("awt"); // VIOLAZ
                return null; // nothing to return
            }
        });
        AccessController.doPrivileged((PrivilegedAction<Void>)
            () -> { 
                System.loadLibrary("awt"); // VIOLAZ
                return null; // nothing to return
            }
        );
    }
	
    }
    


class PoC_resource {
  @POST
  @Path("/concat")
  @Produces(MediaType.APPLICATION_JSON)
  // ruleid: insecure-resteasy-deserialization
  @Consumes({ "*/*" }) // VIOLAZ
  public Map<String, String> doConcat(Pair pair) {
    HashMap<String, String> result = new HashMap<String, String>();
    result.put("Result", pair.getP1() + pair.getP2());
	HttpServletRequest httpRequest = (HttpServletRequest)request;
	log.info("request: method="+httpRequest.getMethod()+", URL="+httpRequest.getRequestURI()); // VIOLAZ

    return result;
  }
  
  private static void bad1() {
        // ruleid: jjwt-none-alg
        String jws = Jwts.builder() // VIOLAZ
                .setSubject("Bob")
                .compact();
		String jws2 = io.jsonwebtoken.Jwts.builder() // VIOLAZ
                .setSubject("John")
                .compact();
    }
	
	private static void ok1() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // ok: jjwt-none-alg
        String jws = Jwts.builder() //OK
                .setSubject("Bob")
                .signWith(key)
                .compact();
    }

}

class GroovyShellUsage {
    public static void test1(String uri, String file, String script, String args) throws URISyntaxException, FileNotFoundException {
        GroovyShell shell = new GroovyShell();
        // VIOLAZ
        shell.evaluate(new File(file));
		shell.evaluate(new File(args));
        // VIOLAZ
        shell.evaluate(new InputStreamReader(new FileInputStream(file)), "script1.groovy");
        // VIOLAZ
        shell.evaluate(script);
        // VIOLAZ
        shell.evaluate(script, "script1.groovy", "test");
        // VIOLAZ
        shell.evaluate(new URI(uri));
        // ok
        shell.evaluate("hardcoded script");
    }
    public static void test2(String uri, String file, String script) throws URISyntaxException, FileNotFoundException {
        GroovyShell shell = new GroovyShell();
        // VIOLAZ
        shell.parse(new File(file));
        // VIOLAZ
        shell.parse(new InputStreamReader(new FileInputStream(file)), "test.groovy");
        // VIOLAZ
        shell.parse(new InputStreamReader(new FileInputStream(file)));
        // VIOLAZ
        shell.parse(script);
        // VIOLAZ
        shell.parse(script, "test.groovy");
        // VIOLAZ
        shell.parse(new URI(uri));
        String hardcodedScript = "test.groovy";
        // ok
        shell.parse(hardcodedScript);
    }
    public static void test3(String uri, String file, String script, ClassLoader loader) throws URISyntaxException, FileNotFoundException {
        GroovyClassLoader groovyLoader = (GroovyClassLoader) loader;
        // VIOLAZ
        groovyLoader.parseClass(new GroovyCodeSource(new File(file)),false);
        // VIOLAZ
        groovyLoader.parseClass(new InputStreamReader(new FileInputStream(file)), "test.groovy");
        // VIOLAZ
        groovyLoader.parseClass(script);
        // VIOLAZ
        groovyLoader.parseClass(script,"test.groovy");
        String hardcodedScript = "test.groovy";
        // ok
        shell.parse(hardcodedScript);
    }
}

@Controller
@SessionAttributes("hello")  
class HelloWorld {  // VIOLAZ
  @RequestMapping("/greet", method = GET);
  public String greet(String greetee) {  
    return "Hello " + greetee;
  }
}
@Controller
@SessionAttributes("hello")
class HelloWorldOk { //OK il metodo goodbye ha la setComplete()
  @RequestMapping("/greet", method = GET);
  public String greet(String greetee) {
    return "Hello " + greetee;
  }
  @RequestMapping("/goodbye", method = POST);
  public String goodbye(SessionStatus status) {
        status.setComplete();
  }
  
  @RequestMapping("/greet", method = GET);
	private String greet(String greetee) {  // VIOLAZ
	
	Algorithm algorithm = Algorithm.HMAC256("secret"); // VIOLAZ
	//…
	String secret = "secret";
	Algorithm algorithm = Algorithm.HMAC256(secret);  // VIOLAZ
	String secretOk = " XdNL4MnDRIM8keINJHw7KYg0CgNMlxnHwpHsL3gfgTM==";
	Algorithm algorithm = Algorithm.HMAC256(secretOk);  // OK


	} 

}

@Controller
@SessionAttributes("user")
@RequestMapping(value = "/orchestrator/NewRule")
class NewRule {

	@Controller
	@RequestMapping({ "/secret" })
	@PropertySource({ "/WEB-INF/default.properties" })
	public void assertThatThrownBy()
	{
		String secret = "secret";
		assertThatThrownBy(() -> shouldThrow()); // VIOLAZ
		assertThatThrownBy(() -> shouldThrow()).isInstanceOf(IOException.class); // ok
		assertThatThrownBy(() -> shouldThrow()).hasMessage("My exception"); // ok
		Algorithm algorithm = Algorithm.none(); // VIOLAZ
		algorithm = com.auth0.jwt.algorithms.Algorithm.none(); // VIOLAZ
		Algorithm algorithm = Algorithm.HMAC256("secret"); // VIOLAZ
		Algorithm algorithm = Algorithm.HMAC256(secret);  // VIOLAZ
		String secretOk = " XdNL4MnDRIM8keINJHw7KYg0CgNMlxnHwpHsL3gfgTM==";
		Algorithm algorithm = Algorithm.HMAC256(secret2);  // OK
	}
	@RequestMapping(value = { "/annullo" }, method = { RequestMethod.GET }, produces = { "application/json" })
    @ResponseBody
    private Map<String, String> annullo(@RequestParam(required = true, value = "idAddebito") final String id) {
        return this.makeMessage(this.dao.annullo(id));
    }
    
	@Async
	private Future<String> asyncMethodWithReturnType() { // VIOLAZ
		return "Hellow, world!";                         
	}
	@Async
	public Future<String> asyncMethodWithReturnType() { // ok
	
		c = db.getConnection();
		String prefQuery = "SELECT pLab from practicals where className = ? AND pFacName = ?";
		PreparedStatement stPref = c.prepareStatement(prefQuery);
		stPref.setString(1, className);
		stPref.setString(2, facName);
		ResultSet rsPref = stPref.executeQuery();
		while (rsPref.next()){
			MainClass.show(rsPref.getString(0)); // VIOLAZ
		}

		return "Hellow, world!";
	}
	@GET
    @Path("/images/{image}")
    @Produces("images/*")
    public Response getImage(@javax.ws.rs.PathParam("image") String image) {
        File file = new File("resources/images/", image); // VIOLAZ
        if (!file.exists()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok().entity(new FileInputStream(file)).build();
    }
	@GET
    @Path("/images/{image}")
    @Produces("images/*")
    public Response ok(@javax.ws.rs.PathParam("image") String image) {
        File file = new File("resources/images/", FilenameUtils.getName(image)); //OK
        if (!file.exists()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok().entity(new FileInputStream(file)).build();
    }


}

class Thread1 extends Thread
{
    Thread1(ThreadGroup tg, String name)
    {
    	super(tg, name);
    }
    @Override
    public void run()
    {
		class Thread1 extends Thread
		{
			Thread1(ThreadGroup tg, String name)
			{
				super(tg, name);
			}
			
			@Override
			public void run()
			{
				for (int i = 0; i < 10; i++)
				{
					ThreadGroup tg = getThreadGroup();
					System.out.println(getName() + "\t" + i + "\t" + getPriority()
						+ "\t" + tg.getName());
				}
			}
		}
    }
}
