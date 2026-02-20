--------------------------------------------------------
--  DDL for Procedure CLOSE_RECUP_OFF_MASTER_SLAVE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "CLOSE_RECUP_OFF_MASTER_SLAVE" 
                                                    (STATO out varchar2 
                                                    , DESC_STATO out varchar2 
                                                    , ID_LAVORAZ in number)  as 

begin

    for recup_master in (select * 
                        from GEAC_RECUP_OFF_MASTER mast 
                        where ID_LAVORAZIONE = ID_LAVORAZ
                        and STATO <> 'CHIUSA'
                        and  0 = (select count (*)
                                    from GEAC_RECUP_OFF_SLAVE slave
                                    where ID_LAVORAZIONE = mast.ID_LAVORAZIONE
                                    and slave.ID_RECUP_OFF_MASTER = mast.ID_RECUP_OFF_MASTER
                                    and slave.STATO not in ('NON_TROVATA','EVASA','EVASA_PARZIALE','RITORNATA')
                                )
                        )
    loop
        update GEAC_RECUP_OFF_SLAVE set STATO = 'CHIUSA', DATA_MODIFICA = SYSDATE where ID_RECUP_OFF_MASTER =  recup_master.ID_RECUP_OFF_MASTER;
        update GEAC_RECUP_OFF_MASTER set STATO = 'CHIUSA', DESC_STATO = 'Richiesta Chiusa', DATA_MODIFICA = SYSDATE where ID_RECUP_OFF_MASTER =  recup_master.ID_RECUP_OFF_MASTER;
    end loop;
    commit;
    STATO := 'SUCCESS';
    DESC_STATO := 'Procedura CLOSE_RECUP_OFF_MASTER_SLAVE eseguita con successo';
exception when others then
    STATO := 'ERROR';
    DESC_STATO := 'Errore Procedura CLOSE_RECUP_OFF_MASTER_SLAVE : '||SQLCODE ||' -- '||SQLERRM ;  
    rollback;
end;

/
