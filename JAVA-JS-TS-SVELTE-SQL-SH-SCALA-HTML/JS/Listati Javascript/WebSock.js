let userInput = document.location.hash.substring(1); // userInput untrusted
let socket = new WebSocket($userInput); // VIOLAZ
let socket = new WebSocket('ws://vulnerable-domain.com'); // VIOLAZ ws://
let socket = new WebSocket('wss://good-domain.com', ‘test.com’); // OK, c’è wss: invece di ws:
ws = new WebSocket("123.123.123.123", "test.com"); // VIOLAZ prima URL senza ://
ws = new WebSocket("http://123.123.123.123", "test.com"); // VIOLAZ http://
const ws = new WebSocket("wss://username:password@example.com") // VIOLAZ
let socket = new WebSocket('wss://good-domain.com'); // VIOLAZ manca la seconda URL
let socket = new WebSocket('wss://good-domain.com', ‘baseurl.com’); // VIOLAZ manca Authorization
const ws  = new WebSocket( // OK c’è Authorization
  "https://example.com/path", ‘baseurl.com’, 
  ["Authorization", "your_token_here"]
