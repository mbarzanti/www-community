PROCEDURE OAUTH_SET_TOKEN(
    p_token   IN VARCHAR2,
    p_expires IN DATE DEFAULT NULL );
 begin
     apex_web_service.oauth_set_token(
         p_token =>   '{oauth access token}'
     );
 end;