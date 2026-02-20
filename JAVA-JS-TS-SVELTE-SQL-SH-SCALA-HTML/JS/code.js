function InvalidBloc()
{
	const ws = new WebSocket("wss://username:password@example.com") // VIOLAZ
	let socket = new WebSocket('wss://good-domain.com'); // VIOLAZ manca la seconda URL
	
	let socket = new WebSocket('wss://good-domain.com', 'baseurl.com'); // VIOLAZ manca Authorization
	const ws  = new WebSocket( // OK c’è Authorization
	  "https://example.com/path", 'baseurl.com', 
	  ["Authorization", "your_token_here"]
	)
	const { sendMessage, lastMessage } = useWebSocket("wss://example.com/path", 'baseurl.com', { // OK c’è Authorization
	  protocols: ["Authorization", "your_token_here"]
	})


	let socket = new WebSocket('ws://vulnerable-domain.com'); // VIOLAZ ws://
	let socket1 = new WebSocket('wss://good-domain.com', 'test.com'); // OK, c’è wss: invece di ws:
	ws = new WebSocket("123.123.123.123", "test.com"); // VIOLAZ prima URL senza ://
	ws = new WebSocket("http://123.123.123.123", "test.com"); // VIOLAZ http://


	let maliciousInputComplex = JSON.parse('{"__proto__": {"constructor": {"prototype": {"isAdmin": true}}}}');
	deepMergeComplex({}, maliciousInputComplex); // VIOLAZ maliciousInputComplex untrusted

	let file = new Blob(["test"], { type: 'text/plain' });
	let url = URL.createObjectURL(file); // VIOLAZ
	let blob = new Blob(["test"], {type:'application/javascript'});
	let url1 = URL.createObjectURL(blob); // Ok il type è giusto
	
	let userInput = document.location.hash.substring(1); // userInput untrusted
	vulnerableButton.onclick = Function(userInput); // VIOLAZ
	element.addEventListener('click', myFunction.bind(null, userInput)); // VIOLAZ

	let wasmMemory = new WebAssembly.Memory({ initial: 1, maximum: 100 }); // VIOLAZ
	const memory = new WebAssembly.Memory({ // OK
	  initial: 10,
	  maximum: 100,
	});

	let socket = new WebSocket($userInput); // VIOLAZ

}
function createWorker(code){
	

	let url = JSON.parse('{"__proto__": {"constructor": {"prototype": {"AdminUrl": url}}}}');  // url untrusted
    // VIOLAZ code è untrusted se Opzioni-Analisi-Funzione Public disabilitato
    let blob = new Blob([code], {type:'application/javascript'}); 
    // VIOLAZ code è untrusted se Opzioni-Analisi-Funzione Public disabilitato
    let worker = new Worker(URL.createObjectURL(blob)); // VIOLAZ se code è untrusted
    worker.onmessage = function(event) {
        console.log("Worker returned: "+ event.data);
    };
    worker.onerror = function(error){
        console.log("Worker error: " + error.message);
    }
    worker.postMessage('start');
	createWorker(document.location.hash.substring(1)); // VIOLAZ

	let wasmMemory = new WebAssembly.Memory({ initial: 1 }); // VIOLAZ
	const memory = new WebAssembly.Memory({initial : 2, maximum : 2}) // OK
	const memory = new WebAssembly.Memory({ // OK
	  initial: 10,
	  maximum: 100,
	});


}

function displayComment(comment) {
  // VIOLAZ comment è untrusted se Opzioni-Analisi-Funzione Public disabilitato
  document.getElementById("comments").insertAdjacentHTML("beforeend", '<div>${comment}</div>'); 
  
  fetch("simple.wasm")
  .then((response) => response.arrayBuffer())
  .then((bytes) => WebAssembly.instantiate(bytes, importObject)) // VIOLAZ
  .then((result) => result.instance.exports.exported_func());
WebAssembly.instantiate(mod, importObject).then((instance) => { // VIOLAZ
    instance.exports.exported_func();
  });


}
function displayUsername(username) {
  // VIOLAZ username è untrusted se Opzioni-Analisi-Funzione Public disabilitato
  document.getElementById("output").innerHTML = username; 
}

function changeElementAttribute(elementId, attributeName, attributeValue) {
  // VIOLAZ attributeValue è untrusted se Opzioni-Analisi-Funzione Public disabilitato
  document.getElementById(elementId)[attributeName] = attributeValue; 
  document.write(document.location.hash.substring(1)); // VIOLAZ document.location.hash untrusted
}

function Csrf03 ()
{
	let url = JSON.parse('{"__proto__": {"constructor": {"prototype": {"AdminUrl": url}}}}');  // url untrusted
	$(location).attr('href',url); // VIOLAZ
	window.location.href = url; // VIOLAZ 
	document.location.href = url; // VIOLAZ
	window.location.replace(url); // VIOLAZ
	self.location = url; // VIOLAZ
	top.location = url; // VIOLAZ
	window.location = url; // VIOLAZ
	
	$(window).attr('location',url); // VIOLAZ
	$(location).prop('href', url); // VIOLAZ
	window.location.assign(url); // VIOLAZ
	window.navigate(url); // VIOLAZ
	let userInput = document.location.hash.substring(1); // userinput untrusted
	vulnerableInput.value = userInput; // vulnerableInput untrusted
	let newScript = document.createElement('script');
	newScript.textContent = vulnerableInput.value; // newScript untrusted
	document.body.appendChild(newScript);  // VIOLAZ
	const headers = {
	  "C ontent-Type": "text/xml",
	  "Breaking-Bad": "<3",
	};
	//manca
	fetch(url, { headers }); // VIOLAZ

}

function InsufficientPostMessage()
{
	socket.send('os.execute("rm -rf /")'); // VIOLAZ
	JSON.parse(document.location.hash.substring(1)); // VIOLAZ
	
	window.addEventListener('message', function(event) { // VIOLAZ
	  Function(event.data)(); // esecuzione del codice
	});
	window.addEventListener('message', function(event) { // OK
	  Function(escapeURI(event.data));
	});
	window.addEventListener('message', function(event) { // OK
	  // validazione
	  if (isEmail(event.data)) {
		 Function(event.data);
	   }
	});
	let socket = new WebSocket('ws://vulnerable-server.com');
	socket.onmessage = function(event) { // VIOLAZ
		console.log(event.data);
		advancedCreateWorker('self.onmessage = function(event) { postMessage(event.data); };'); // VIOLAZ postMessage
	};
	let request = indexedDB.open('userDB', 1);
		request.onsuccess = function(event) { // VIOLAZ
			let db = event.target.result; // db untrusted da event
			let transaction = db.transaction(['users'], 'readwrite');
			let store = transaction.objectStore('users');
			store.put(userData, 1); 
	 }; 
	self.addEventListener('fetch', event => { // VIOLAZ
		 let token = localStorage.getItem('jwtToken');
		 event.respondWith(new Response(JSON.stringify({ token: token }), { headers: { 'Content-Type': 'application/json' } }));
	});
	self.addEventListener('fetch', event => { // OK event validata
	   if (event.request.url.includes('/api/auth')) {
		 let token = localStorage.getItem('jwtToken');
		 event.respondWith(new Response(JSON.stringify({ token: token }), { headers: { 'Content-Type': 'application/json' } }));
	   }
	 });
	worker.postMessage(document.location.hash.substring(1)); // VIOLAZ

}