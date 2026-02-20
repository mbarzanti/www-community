declare
 l_filename varchar2(255);
 l_BLOB BLOB;
 l_CLOB CLOB;
 l_envelope CLOB;
 l_response_msg varchar2(32767);
BEGIN
 IF :P1_FILE IS NOT NULL THEN
    SELECT filename, BLOB_CONTENT
      INTO l_filename, l_BLOB
      FROM APEX_APPLICATION_FILES
      WHERE name = :P1_FILE;
 
    l_CLOB := apex_web_service.blob2clobbase64(l_BLOB);
 
   l_envelope := q'!<?xml version='1.0' encoding='UTF-8'?>!';
   l_envelope := l_envelope|| '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:chec="http://www.stellent.com/CheckIn/">
  <soapenv:Header/>
  <soapenv:Body>
     <chec:CheckInUniversal>
        <chec:dDocName>'||l_filename||'</chec:dDocName>
        <chec:dDocTitle>'||l_filename||'</chec:dDocTitle>
        <chec:dDocType>Document</chec:dDocType>
        <chec:dDocAuthor>GM</chec:dDocAuthor>
        <chec:dSecurityGroup>Public</chec:dSecurityGroup>
        <chec:dDocAccount></chec:dDocAccount>
        <chec:CustomDocMetaData>
           <chec:property>
              <chec:name></chec:name>
              <chec:value></chec:value>
           </chec:property>
        </chec:CustomDocMetaData>
        <chec:primaryFile>
           <chec:fileName>'||l_filename'||</chec:fileName>
           <chec:fileContent>'||l_CLOB||'</chec:fileContent>
        </chec:primaryFile>
        <chec:alternateFile>
           <chec:fileName></chec:fileName>
           <chec:fileContent></chec:fileContent>
        </chec:alternateFile>
        <chec:extraProps>
           <chec:property>
              <chec:name></chec:name>
              <chec:value></chec:value>
           </chec:property>
        </chec:extraProps>
     </chec:CheckInUniversal>
  </soapenv:Body>
</soapenv:Envelope>';
 
apex_web_service.make_request(
   p_url               => 'http://192.0.2.1/idc/idcplg',
   p_action            => 'http://192.0.2.1/CheckIn/',
   p_collection_name   => 'STELLENT_CHECKIN',
   p_envelope          => l_envelope,
   p_username          => 'sysadmin',
   p_password          => 'password' );
 
 l_response_msg := apex_web_service.parse_response(
  p_collection_name=>'STELLENT_CHECKIN',
p_xpath=>'//idc:CheckInUniversalResponse/idc:CheckInUniversalResult/idc:StatusInfo/idc:statusMessage/text()',
  p_ns=>'xmlns:idc="http://www.stellent.com/CheckIn/"');
 
 :P1_RES_MSG := l_response_msg;
 
 END IF;
END;