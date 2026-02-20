APEX_ZIP.FINISH (
    p_zipped_blob IN OUT NOCOPY BLOB );
declare
    l_zip_file blob;
begin
    for l_file in ( select file_name,
                            file_content
                       from my_files )
    loop
        apex_zip.add_file (
            p_zipped_blob => l_zip_file,
            p_file_name   => l_file.file_name,
            p_content     => l_file.file_content );
    end loop;

    apex_zip.finish (
        p_zipped_blob => l_zip_file );

end;