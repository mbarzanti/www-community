FUNCTION OAUTH_GET_LAST_TOKEN RETURN VARCHAR2;
select apex_web_service.oauth_get_last_token from dual;
