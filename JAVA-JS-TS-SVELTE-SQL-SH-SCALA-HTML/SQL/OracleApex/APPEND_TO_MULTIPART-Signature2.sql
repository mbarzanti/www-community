DECLARE
    l_multipart    apex_web_service.t_multipart_parts;
BEGIN
    apex_web_service.append (
        p_multipart    => l_multipart,
        p_name         => 'param1',
        p_content_type => 'application/json',
        p_body         => '{"hello":"world"}' );
END;