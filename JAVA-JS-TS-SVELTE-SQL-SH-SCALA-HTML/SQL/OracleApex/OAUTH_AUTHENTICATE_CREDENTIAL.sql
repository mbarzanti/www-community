begin
    apex_web_service.oauth_authenticate_credential(
        p_token_url => '[URL to ORDS OAuth troken service: http(s)://{host}:{port}/ords/.../oauth/token]',
        p_credential_static_id => '[web-credential]');
end;