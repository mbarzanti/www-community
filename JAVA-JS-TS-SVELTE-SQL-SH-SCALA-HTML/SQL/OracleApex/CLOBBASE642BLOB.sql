declare
	    l_base64	CLOB;
    l_blob	BLOB;
    l_xml   	XMLTYPE;
begin
    l_base64 := apex_web_service.parse_xml_clob(l_xml, ' //runReportReturn/reportBytes/text()');
    	l_blob := apex_web_service.clobbase642blob(l_base64);
end;