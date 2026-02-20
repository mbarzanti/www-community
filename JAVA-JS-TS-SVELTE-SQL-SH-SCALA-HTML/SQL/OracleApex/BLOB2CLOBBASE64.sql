declare
    	l_clob	CLOB;
    l_blob	BLOB;
begin
    SELECT BLOB_CONTENT
      INTO l_BLOB
      FROM APEX_APPLICATION_FILES
      WHERE name = :P1_FILE;
 
    l_CLOB := apex_web_service.blob2clobbase64(l_BLOB);
end;