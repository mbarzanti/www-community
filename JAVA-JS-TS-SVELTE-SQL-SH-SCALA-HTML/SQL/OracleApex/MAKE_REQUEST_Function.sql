declare
    l_envelope CLOB;
    l_xml	XMLTYPE;
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
   p_envelope => l_envelope
);
END