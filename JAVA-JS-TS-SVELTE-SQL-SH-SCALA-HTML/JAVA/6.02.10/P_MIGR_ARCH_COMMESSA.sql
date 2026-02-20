create or replace PROCEDURE P_MIGR_ARCH_COMMESSA (
    V_DOCMAN_ID_COMMESSA IN VARCHAR2
) AS 
BEGIN
    /*
    insert into TBL_ARC_UBICAZIONI select * from TBL_UBICAZIONI where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL_UBICAZIONI where id_commessa = V_DOCMAN_ID_COMMESSA;
    */
    -- Come abbiamo visto le ubicazioni su Docman sono spesso associate ad altre commesse, quindi archivio per tuple importate
    insert into TBL_ARC_UBICAZIONI select * from TBL_UBICAZIONI where import_id_track is not null;
    delete from TBL_UBICAZIONI where import_id_track is not null;
    

    insert into TBL_ARC_TITOLARI select * from TBL_TITOLARI where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL_TITOLARI where id_commessa = V_DOCMAN_ID_COMMESSA;

    insert into TBL_ARC_DETTAGLIO_TITOLARI select * from TBL_DETTAGLIO_TITOLARI where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL_DETTAGLIO_TITOLARI where id_commessa = V_DOCMAN_ID_COMMESSA;


    insert into TBL__ARC_PEDANE_ select * from TBL__PEDANE_ where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL__PEDANE_ where id_commessa = V_DOCMAN_ID_COMMESSA;

    insert into TBL__ARC_MAGAZZINO_PEDANE_ select * from TBL__MAGAZZINO_PEDANE_ where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL__MAGAZZINO_PEDANE_ where id_commessa = V_DOCMAN_ID_COMMESSA;


    insert into TBL_ARC_FALDONI select * from TBL_FALDONI where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL_FALDONI where id_commessa = V_DOCMAN_ID_COMMESSA;
    
    
    insert into TBL_ARC_UDC select * from TBL_UDC where id_commessa = V_DOCMAN_ID_COMMESSA and (data_associazione is null or import_id_track is not null);
    delete from TBL_UDC where id_commessa = V_DOCMAN_ID_COMMESSA and (data_associazione is null or import_id_track is not null);

    insert into TBL_ARC_MAGAZZINO_UDC select * from TBL_MAGAZZINO_UDC where id_commessa = V_DOCMAN_ID_COMMESSA and import_id_track is not null;
    delete from TBL_MAGAZZINO_UDC where id_commessa = V_DOCMAN_ID_COMMESSA and import_id_track is not null;


    insert into TBL_ARC_UDA select * from TBL_UDA where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL_UDA where id_commessa = V_DOCMAN_ID_COMMESSA and import_id_track is not null;
    
    insert into TBL_ARC_MAGAZZINO_UDA select * from TBL_MAGAZZINO_UDA where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL_MAGAZZINO_UDA where id_commessa = V_DOCMAN_ID_COMMESSA and import_id_track is not null;


    insert into TBL_ARC_CLASSIFICAZIONE select * from TBL_CLASSIFICAZIONE where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL_CLASSIFICAZIONE where id_commessa = V_DOCMAN_ID_COMMESSA and import_id_track is not null;

    insert into TBL_ARC_PRE_CLASSIFICAZIONE select * from TBL_PRE_CLASSIFICAZIONE where id_commessa = V_DOCMAN_ID_COMMESSA;
    delete from TBL_PRE_CLASSIFICAZIONE where id_commessa = V_DOCMAN_ID_COMMESSA;
    
END P_MIGR_ARCH_COMMESSA;