DECLARE
    l_multipart    apex_web_service.t_multipart_parts;
BEGIN
    apex_web_service.append (
        p_multipart    => l_multipart,
        p_name         => 'param1',
        p_content_type => 'application/octet-stream',
        p_body_body    => (select blob from table where id = 1) );
END;