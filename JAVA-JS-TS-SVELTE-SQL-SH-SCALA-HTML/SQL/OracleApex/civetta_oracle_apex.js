export class JsonValueConverter {
  public toView(value): string | void {
    if (value) {
      return JSON.stringify(value, undefined, '\t');
    }
    return;
  }
  function apex_03_a ()
  {
	    installPrompt.prompt();
	  // Wait for the user to respond to the prompt
	  installPrompt.userChoice
		.then(function(choiceResult) {
		  apex.debug.log('User ' + choiceResult.outcome + ' to install the app' + pwa.getInstallText()
	);
		  // Reset the install prompt
		  installPrompt = null;
		  // Hide the install button
		  pwa.init.ui();
		});
	};
	
	apex.jQuery( window ).on('apexwindowresized', function() {
	  if ( apex.theme42.util.mq( '(min-width: 640px)' ) ) {
		console.log( 'Window resized, and viewport is at least 640px wide' )
	  }
	});

	onkeypress="submitEnter(this,event)"
	apex.alert('Delete Department 1', 'DELETE');
	apex.page.alert('Delete Department 2', 'DELETE');
	apex.page.confirm('Delete Department 3', 'DELETE');
	apex.confirm('Delete Department 3', 'DELETE');
  }

	function changeProp(pCol) {
	   var elms = document.getElementsByName(pCol);
	   for (i=0; i< elms.length; i++) {
		if $v_IsEmpty(elms[i].value) {
			apex.alert('The value is' + elms[i].value);
			}
	   } 
	}

}


