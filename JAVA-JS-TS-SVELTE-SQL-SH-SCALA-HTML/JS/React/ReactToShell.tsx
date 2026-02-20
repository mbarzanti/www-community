/*
React4Shell
React servers that use React Server Function endpoints are known to be vulnerable. It is possible to check React Server applications for this vulnerable functionality by looking for the use server; directive in any of the application’s source code files, which signifies a Server Function is defined.
*/
import React from 'https://cdn.skypack.dev/react';
import ReactDOM from 'https://cdn.skypack.dev/react-dom';
import Button from './Button';

function EmptyNote () {
  async function createNoteAction() {
    // Server Function
	// VIOLAZ React4Shell exposed. 
	// CWE 502 
	// CVE-2025-66478 
	// CVSS 10.0 CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:C/C:H/I:H/A:H 
	// Usage of the vulnerable App Router funtionality
	/* REFERENCES:
	https://react.dev/reference/rsc/server-functions ↩
	https://tonyalicea.dev/blog/understanding-react-server-components/ ↩
	https://github.com/facebook/react/pull/35277/commits/e2fd5dc6ad973dd3f220056404d0ae0a8707998d ↩
	https://developer.mozilla.org/en-US/docs/Learn_web_development/Extensions/Advanced_JavaScript_objects/Object_prototypes ↩
	https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/Function ↩
	https://x.com/maple3142
	*/
	// Remediation: /https://gist.github.com/HerringtonDarkholme/87f14efca45f7d38740be9f53849a89f#mitigation-recommendations
    'use server'; 
    
    await db.notes.create();
  }

  return <Button onClick={createNoteAction}/>;
}

"use client"; // VIOLAZ React4Shell exposed

export default function Button({onClick}) { 
  console.log(onClick); 
  // {$$typeof: Symbol.for("react.server.reference"), $$id: 'createNoteAction'}
  return <button onClick={() => onClick()}>Create Empty Note</button>
}