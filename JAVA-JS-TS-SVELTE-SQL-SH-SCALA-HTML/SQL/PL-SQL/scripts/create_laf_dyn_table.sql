create table laf_dyn_table
(
   c1 integer primary key
  ,c2 varchar2(100)
  ,c3 number
  ,c4 date
  ,c5  blob
  ,c6 varchar2(30) 
  ,c7 clob  
)
/
declare
  v varchar2(1);
begin
  for i in 1 .. 100 loop
    if mod(i,5)=0 then
	  v := 'Y';
	else
	  v := 'N';
	End if ;    
    insert into laf_dyn_table (c1,c2,c3,c4,c6)
	values(i, 'line ' ||i, (i*1.2), (sysdate-i), v);
  end loop;
end;
/
commit
/
