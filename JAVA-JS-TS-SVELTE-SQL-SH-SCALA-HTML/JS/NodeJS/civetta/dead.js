import React;

var sanitize = require('mongo-sanitize');
var cn = require('sql-client');
var KNEX = require('knex');
var clean = sanitize(req.params.username);
var clean2 = clean;
var clean3 = clean2;
var vm = require('vm');
var vm2 = require('vm2');
var express = require('express');
var app = express();
const re = new RegExp("ab+c");
const re1 = /ab+c/;
var cn1 = require(clean3);
var eta = require('eta');
var ex = require('child_process');
var shell = require('shelljs');
var S = require('serialize-javascript');
var xml2json = require('xml2json');
var serialize_to_js =require('serialize-to-js');
var node_serialize =require('node-serialize');
var grpc =require('grpc');
var js_yaml =require('js-yaml');
var allowRunningInsecureContent = {webPreferences: {allowRunningInsecureContent: true}};
var sax =require('sax');
var wkhtmltopdf = require('wkhtmltopdf');
var wkhtmltoimage = require('wkhtmltoimage');
var phantom = require('phantom');
var playwright = require('playwright');
var puppeteer = require('puppeteer');
var jsonwebtoken = require("jsonwebtoken");
const jwt = require("jsonwebtoken");
const password = "hardcoded-secret-here"; // 
const path = require('path');
const hbs = require('hbs');

class JwtAuthentication {
  static sign(obj) {
	  
	  var pattern =  {
            host: $HOST,
            database: $DATABASE,
            dialect: 'mariadb',
            dialectOptions: {
              ssl: {
                rejectUnauthorized: false
              }
            }
           };
		   
	  var pattern2 = {
            host: $HOST,
            database: $DATABASE,
            dialect: 'mariadb'
           };
		   
	  var pattern3 =  {
            host: $HOST,
            database: $DATABASE,
            dialect: 'mariadb',
            dialectOptions: {
              ssl: {
                minVersion: 'TLSv1'
              }
            }
           };
		   
	var T = jsonwebtoken.verify(P, X, {algorithms:["",'none',""]},"");
	
    return jwt.sign(obj, password, {});
  }
}

app.get('/test1', function (req, res) {
	res.render(VIEW, clean2);
	res.header("=~/x-xss-protection/i", 0, "");
	res.set("=~/access-control-allow-origin/i",'*', "");
    const s = new Sandbox();
    // ruleid:express-sandbox-code-injection
	path.join("",clean2,"")
    s.run('lol('+clean2+')', cb);
	vm2.run('lol('+clean2+')', cb);
    res.send('Hello world');
	serialize_to_js.deserialize();
	node_serialize.unserialize();
	grpc.createInsecure();
	js_yaml.load();
	new BrowserWindow({webPreferences: {allowRunningInsecureContent:true}});
	sax.ondoctype = "";
	sax.on('doctype',"");
	OBJ.escapeMarkup = false;
	sf.SafeString("");
	new Handlebars.SafeString("");
	X.compile("", {noEscape: true}, "");
	X.autoEscaping(false);
	
})
function MyComponentXPath(req,res) {
	
	var XPATH.parse("=~/^[\/\/].+/" + clean2, res);
	var PARSER = new expat.Parser();
    PARSER.parse(INPUT01,res);
    PARSER.write(a1,LOCALVAR, a2);

	
}
function MyComponentParser(req,res) {
	{
		
		var PARSER_xxe = new libxmljs.SaxPushParser();
		PARSER_xxe.push(res, clean2, res);
		HELMET("", {frameguard: false}, "");
	}
	
function MyComponent(req,res) {
	
	puppeteer.goto (clean2,"");
	playwright.evaluateOnNewDocument(clean2,"");
	phantom.openUrl(clean2,"");
	wkhtmltoimage.generate(clean2, "");
	wkhtmltopdf(clean2);
	xml2json.toJson(clean2,res);
	chip.createCipheriv("=~/aes-256-ecb/i");
	chip.createCipheriv("=~/aes-/i");
	chip.createCipheriv("des");
	var LOCALVAR.push(clean2);
	var INPUT01 = clean2;
	res.write(a1,LOCALVAR, a2);
	var LIBXML.parseXmlString(res,clean2, res);
	rand.pseudoRandomBytes(number);
	S(clean3, {unsafe: true});
	node.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';
	node.env['NODE_TLS_REJECT_UNAUTHORIZED']= '0';
	test(res, {SSL_VERIFYPEER : 0}, res);
	new BrowserWindow({webPreferences: {allowRunningInsecureContent: true}});
	var p1 = {webPreferences: {allowRunningInsecureContent: true}};
	new BrowserWindow({webPreferences: {webSecurity: false}});
	var p = {webPreferences: {webSecurity: false}};
	new Function(clean2){
	};
	new BrowserWindow ({webPreferences:{enableBlinkFeatures:"dfsdfs"}});
	var pippo = {webPreferences: {enableBlinkFeatures: "asdas"}};
	new BrowserWindow({webPreferences: {nodeIntegration: true}});
	var pippo2={webPreferences: {nodeIntegration: true}};
	new BrowserWindow({webPreferences: {contextIsolation: false}});
	var pippo3 = {webPreferences: {contextIsolation: false}};
	new BrowserWindow({webPreferences: {experimentalFeatures: true}});
	var pippo4 = {webPreferences: {experimentalFeatures: true}};
	OBJ.readUInt8(clean2, true);
	var CONTEXT = clean2;
	ex.execSync(res, clean2,res);
	shell.exec(clean2, res);
	eta.compile(res, clean2, res);
    new NodeVM({sandbox: CONTEXT},res);
    vm.runInContext(CODE,CONTEXT,clean);
	var INPUT = clean2;
    vm.runInNewContext(INPUT);
	x.redirect(res, clean3, res);
	vm.compileFunction(INPUT,PARAMS,{parsingContext: clean2},res);
	eval(clean2);
	setTimeout(clean2);
	setInterval(clean2);
	require(clean3);
	//var Query = {$where: req};
	var Query["pippo"] = clean;
	Test.fun(Query);
	/ab+c/g.exec(clean);
	res1.header(res, "=~/location/i", clean3, res);
	re.exec(clean2);
	re.test(clean2);
	//validate(clean);
	Users.findOne({ name: clean }, function(err, doc) {});
	Test1.fun1({$where: clean}, var1);
	conn.query(clean, testQyery);
	KNEX.raw(clean, testQyery)
	app.use(express.bodyParser());
	re = new RegExp(clean);
	re1 = re.search(clean);
	re1 = re.split(clean);

	for (let i = 0; i < 9; i++) {
		str = str + i;
	}

  return a === password;
  return <div dangerouslySetInnerHTML={createMarkup()} />;
}

/** @type {import("next").NextConfig} */
require("dotenv").config();
pippo.createHash("md5");
pippo.createHash('md5');

test.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';
test.env['NODE_TLS_REJECT_UNAUTHORIZED']= '0';
test(..., {SSL_VERIFYPEER : 0}, ...);
new BrowserWindow({webPreferences: {webSecurity: false}});
var X = {webPreferences: {webSecurity: false}};

if (password === 'test')
{
}

if (password != test)
{
}

const nextConfig = {
  reactStrictMode: true,
  env: {
    ATLAS_URL: process.env.ATLAS_URL,
    JWT_SECRET_KEY: process.env.JWT_SECRET_KEY,
    API_URL: process.env.API_URL,
    BASE_URL: process.env.BASE_URL,
  },
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "images.unsplash.com",
        port: "*",
        pathname: "**",
      },
    ],
  },
};

module.exports = nextConfig;
const decodedRToken: JWTPayload = decodeJwt(refreshToken);  //VIOLAZ
const auth = basicAuth({
  users: {
    admin: '123',  // VIOLAZ
    user: '456', // VIOLAZ
  },
});



app.get('/authenticate', auth, (req, res) => {
  if (req.auth.user === 'admin') {  // VIOLAZ
    res.send('admin');
  } else if (req.auth.user === 'user') { // VIOLAZ
    res.send('user');
  }
  if (username !== 'john' || password !== '123') { // VIOLAZ
            return Promise.reject();
        }
});

const res = await axios.get('/authenticate', { auth: { username: 'admin', password: '123' } });

const { email, firstName, lastName } = req.body; // email anche se const è untrusted
const hashedPassword = await hashPassword(req.body.password);
const userData = {
        email: email.toLowerCase(),  //VIOLAZ
        firstName,
        lastName,
        password: hashedPassword,
        role: "admin",
      };

<!—HTML OK usa la replace-->
<script>
window.__PRELOADED_STATE__ = $(JSON.stringify(preloadedstate).replace(
/</g,
‘\\u003c’
)}
</script>
<!—HTML VIOLAZ-->
<script>window.__STATE__ = ${JSON.stringify({ data })}</script>

<!—JAVASCRIPT -->
