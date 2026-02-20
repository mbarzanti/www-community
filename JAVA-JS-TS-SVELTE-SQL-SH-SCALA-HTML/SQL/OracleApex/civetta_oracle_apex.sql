CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED " MyTestClass"
AS
// VIOLAZ
@IsTest
private class MyTestClass {
   // VIOLAZ
   @IsTest static void test1() {
      // Implement test code
   }
  // VIOLAZ
   @TestSetup static void methodName() {
      // Implement test code
   }
  // VIOLAZ
    @TestVisible private static Integer recordNumber = 1;
   // VIOLAZ
    @TestVisible private static void updateRecord(String name) {
        // Do something
    }
	
	static class innerClassTest
	{
		static int test = 1;
		try 
		{
		}
		catch (EXCEPTION ex) 
		{
		}
		finally 
		{
		}
	}
}
END;
select 
  task_name,
  start_date,
  end_date,
  status,
  assigned_to,
  case status
     when 'Open'    then 'apex-cal-green'
     when 'Pending' then 'apex-cal-yellow'
     when 'Closed'  then 'apex-cal-red'
     when 'On-Hold' then 'apex-cal-black'
  end as css_class
from 
  eba_ut_chart_tasks

select APEX_INSTANCE_ADMIN.GET_PARAMETER('APEX_REST_PATH_PREFIX')
GRANT EXECUTE ON apex_030200.wwv_flow_worksheet_standard TO giffy;
CREATE OR REPLACE SYNONYM giffy.wwv_flow_worksheet_standard FOR apex_030200.wwv_flow_worksheet_standard

pwa.install = function() {
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

function changeProp(pCol) {
   var elms = document.getElementsByName(pCol);
   for (i=0; i< elms.length; i++) {
 	if $v_IsEmpty(elms[i].value) {
		alert('The value is' + elms[i].value);
		}
   } 
}

Select d.apex_view_name
      ,d.column_name
  From apex_dictionary d
      ,all_tab_cols tc
      ,(Select Max(u1.username) As current_apex_user
          From all_users u1
         Where u1.username Like 'APEX%'
               And regexp_like(substr(u1.username
                                     ,'6'
                                     ,1)
                              ,'^[0-9]*$')) u
 Where d.apex_view_name = tc.table_name
       And d.column_name = tc.column_name
       And tc.owner = u.current_apex_user
       And d.column_id <> 0
       And (d.column_name Like '%FILE_URLS%')
       And d.column_name Not In ('APEX_WORKSPACE_UI_TYPES')
…
Select iv_app.workspace
         ,iv_app.application_id
         ,Null As page_id
         ,'APEX_APPL_USER_INTERFACES' || ' JS' As file_type
         ,Null As object_name
         ,aui.javascript_file_urls As file_urls
    From iv_app
…
apex.widget.initPageItem( "P100_COMPANY_NAME", {
    getValue:   function() {
        var lValue;
        // code to determine lValue based on the item type.
        return lValue;
    },
    setValue:   function( pValue, pDisplayValue ) {
        // code that sets pValue and pDisplayValue (if required), for the item type
    },
    enable:     function() {
        // code that enables the item type
    },
    disable:    function() {
        // code that disables the item type
    },
    show:       function() {
        // code that shows the item type
    },
    hide:       function() {
        // code that hides the item type
    },
    addValue:   function( pValue ) {
        // code that adds pValue to the values already in the item type
    },
    nullValue:  "<null return value for the item>",
    setFocusTo: $( "<some jQuery selector>" ),
    setStyleTo: $( "<some jQuery selector>" ),    
    afterModify:        function(){
        // code to always fire after the item has been modified (value set, enabled, etc.)
    },
    loadingIndicator:   function( pLoadingIndicator$ ){
        // code to add the loading indicator in the best place for the item
        return pLoadingIndicator$;
    }
});

select APEX_INSTANCE_ADMIN.GET_PARAMETER('APEX_REST_PATH-PREFIX')
apex.page.confirm('Delete Department', 'DELETE');
apex.confirm('Delete Department', 'DELETE');

select reg.source_type, fs.series_seq, fs.series_name, fs.series_query source
from apex_application_page_regions reg,
    apex_application_page_flash5_s fs
where reg.application_id = :APP_ID
    and reg.page_id = :APP_PAGE_ID
    and reg.static_id = d_region_static_id
    and fs.application_id = reg.application_id
    and fs.page_id = reg.page_id
    and fs.region_id = reg.region_id
    and reg.source_type in (
        'Flash Chart',
        'Map'
    )

APEX_DEBUG.LOG_MESSAGE ( 
    p_message IN VARCHAR2 DEFAULT NULL, 
    p_enabled IN BOOLEAN DEFAULT FALSE, 
    p_level IN T_LOG_LEVEL DEFAULT C_LOG_LEVEL_APP_TRACE );

CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED "Operator"
AS
public class Operator
{
    ADDITION("+") {
      // VIOLAZ annotazione non permessa in Oracle
       @Override public double apply(double x1, double x2) {
        return x1 + x2;
       }
    },
    SUBTRACTION("-") {
	// VIOLAZ annotazione non permessa in Oracle
       @Override public double apply(double x1, double x2) {
        return x1 - x2;
       }
    };
}
	END;



CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED " MyTestClass2"
AS
private class MyTestClass2 {
// VIOLAZ
@Deprecated 
  global void myMethod(String a) {
  }
}
END; 

CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED " MyTestClass3"
AS
private static class MyTestClass3 {
@SuppressWarnings  // VIOLAZ
  global void myMethod(String a) {
  }
}
END;

