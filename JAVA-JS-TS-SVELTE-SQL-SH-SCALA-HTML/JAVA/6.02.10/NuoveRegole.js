getBaseUrl(): Observable<BaseUrl> {
	const ERR1 = “Errore di sbaglio”;
	const numLivesForCat = 9;
	const errore = false;
	throw 404;                              // VIOLAZ
	throw true;                              // VIOLAZ
	throw "Invalid negative index.";        // VIOLAZ
	throw ERR1; // VIOLAZ
	throw errore; // VIOLAZ
	throw new Error("Status: " + 404);  // OK
	throw new RangeError("Invalid negative index."); // OK
	throw new EvalError(); // VIOLAZ
	throw new EvalError('EvalError has occurred'); // VIOLAZ
}

