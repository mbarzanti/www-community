DECLARE
    l_multipart    apex_web_service.t_multipart_parts;
    l_request_blob blob;
BEGIN
     l_request_blob := apex_web_service.generate_request_body (
                           p_multipart    => l_multipart );
END;