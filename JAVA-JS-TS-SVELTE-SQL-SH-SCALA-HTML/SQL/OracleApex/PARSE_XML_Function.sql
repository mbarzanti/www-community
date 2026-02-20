APEX_WEB_SERVICE.PARSE_XML (
    p_xml               IN XMLTYPE,
    p_xpath             IN VARCHAR2,
    p_ns                IN VARCHAR2 DEFAULT NULL ) 
RETURN VARCHAR2;
declare
    l_envelope CLOB;
    l_xml XMLTYPE;
    l_movie VARCHAR2(4000);
BEGIN
    l_envelope := ' <?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
xmlns:tns="http://www.ignyte.com/whatsshowing"
xmlns:xs="http://www.w3.org/2001/XMLSchema">
   <soap:Body>
      <tns:GetTheatersAndMovies>
         <tns:zipCode>43221</tns:zipCode>
         <tns:radius>5</tns:radius>
      </tns:GetTheatersAndMovies>
   </soap:Body>
</soap:Envelope>';
 
   l_xml := apex_web_service.make_request(
     p_url => ' http://www.ignyte.com/webservices/ignyte.whatsshowing.webservice/moviefunctions.asmx',
     p_action => ' http://www.ignyte.com/whatsshowing/GetTheatersAndMovies',
     p_envelope => l_envelope );
 
   l_movie := apex_web_service.parse_xml(
     p_xml => l_xml,
     p_xpath => ' //GetTheatersAndMoviesResponse/GetTheatersAndMoviesResult/Theater/Movies/Movie/Name[1]',
     p_ns => ' xmlns="http://www.ignyte.com/whatsshowing"' );
 
END;
