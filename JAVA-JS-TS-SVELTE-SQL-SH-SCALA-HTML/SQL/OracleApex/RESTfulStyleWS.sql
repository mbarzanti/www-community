declare
  l_clob clob;
  l_buffer         varchar2(32767);
  l_amount         number;
  l_offset         number;
begin
 
  l_clob := apex_web_service.make_rest_request(
              p_url => 'http://us.music.yahooapis.com/ video/v1/list/published/popular',
              p_http_method => 'GET',
              p_parm_name => apex_util.string_to_table('appid:format'),
              p_parm_value => apex_util.string_to_table(apex_application.g_x01||':'||apex_application.g_x02));
 
    l_amount := 32000;
    l_offset := 1;
    begin
        loop
            dbms_lob.read( l_clob, l_amount, l_offset, l_buffer );
            htp.p(l_buffer);
            l_offset := l_offset + l_amount;
            l_amount := 32000;
        end loop;
    exception
        when no_data_found then
            null;
    end;
 
end;