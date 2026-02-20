CREATE OR REPLACE PACKAGE PKG_LAF AS

  -- populate the BLOB column --
  FUNCTION select_blob ( PC$Clause IN VARCHAR2 ) RETURN BOOLEAN ;
  -- Get and return chunks of 4000 bytes --
  FUNCTION Get_B64_Chunk RETURN VARCHAR2 ;
  -- Get the blob length --
  FUNCTION Get_Length RETURN PLS_INTEGER ;
  -- Set the Blob chunk --
  PROCEDURE Set_B64_Chunk ( PC$Chunk IN VARCHAR2 ) ;
  -- Save the Blob column --
    FUNCTION  Save_Blob
    ( 
	   PC$Table   IN VARCHAR2
	  ,PC$Column  IN VARCHAR2
	  ,PC$Where   IN VARCHAR2  
    ) RETURN VARCHAR2 ;
  -- Init the transfert process --
  PROCEDURE Init_Transfer ;
  
  ----------------------------
  --   CLOB/NCLOB methods   --
  ----------------------------
  -- populate the CLOB/NCLOB column --
  FUNCTION select_clob  ( PC$Clause IN VARCHAR2 ) RETURN BOOLEAN ;
  FUNCTION select_nclob ( PC$Clause IN VARCHAR2 ) RETURN BOOLEAN ;
  -- Get and return chunks --
  FUNCTION Get_Chunk RETURN  VARCHAR2 ;
  FUNCTION Get_NChunk RETURN VARCHAR2 ;
  -- Set chunks --
  PROCEDURE Init_Transfer_lob ( PC$Type IN VARCHAR2 ) ;
  FUNCTION  Transfer( PC$Clause IN VARCHAR2, PC$Type IN VARCHAR2 ) RETURN VARCHAR2;
  PROCEDURE Set_Chunk ( PC$Chunk IN VARCHAR2 )  ;
  PROCEDURE Set_NChunk ( PC$Chunk IN VARCHAR2 ) ;
  
  PROCEDURE Set_ChunkSize ( PN$Size IN PLS_INTEGER ) ;  
  
END PKG_LAF;

/


create or replace
PACKAGE BODY PKG_LAF AS

  GL$Blob   BLOB ;             -- global BLOB variable
  GN$Pos    PLS_INTEGER := 1 ; -- global current pos in the BLOB
  GN$Length PLS_INTEGER := 0 ; -- global current length of th blob
  GN$Chunk  PLS_INTEGER := 16384 ;
  
  GL$Clob       CLOB ;                -- global CLOB variable
  GL$NClob      NCLOB ;               -- global NCLOB variable  
  GN$PosL       PLS_INTEGER := 1 ;    -- global current pos in the CLOB
  GN$ChunkSize  PLS_INTEGER := 8192 ; -- cannot be greater that 32767  

  FUNCTION Get_Length RETURN PLS_INTEGER
  IS
  BEGIN
    RETURN GN$Length ;
  END ;

  ----------------------------------------
  -- Get the content of the BLOB column --
  ----------------------------------------
  FUNCTION select_Blob ( PC$Clause IN VARCHAR2 ) RETURN BOOLEAN
  IS
  BEGIN
    EXECUTE IMMEDIATE PC$Clause INTO GL$Blob ;
    GN$Pos := 1 ;
    RETURN TRUE ;
  EXCEPTION
    WHEN OTHERS THEN
	  RETURN FALSE ;
  END select_Blob ;
  

  -------------------------------------------------------------
  -- Return a Base 64 16384 bytes chunk of the selected BLOB --
  -------------------------------------------------------------
  FUNCTION Get_B64_Chunk RETURN VARCHAR2
  IS
    LN$amt  NUMBER := GN$Chunk ;
    LR$raw  RAW(16384);
  BEGIN
    LN$amt := GN$Chunk ;
    -- Read the BLOB
    dbms_lob.READ(GL$Blob, LN$amt, GN$Pos, LR$raw);
    GN$Pos := GN$Pos + LN$amt;
    LN$amt := GN$Chunk;
    RETURN UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_ENCODE(LR$Raw));
  EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL ;
  END Get_B64_Chunk ;


  ----------------------------------
  --  set the chunks to populate  --
  --  the BLOB column             --
  ----------------------------------
  PROCEDURE Set_B64_Chunk ( PC$Chunk IN VARCHAR2 )
  IS
    rawData RAW(16384);
  BEGIN
    IF PC$Chunk IS NOT NULL THEN
      rawData := utl_encode.BASE64_DECODE(utl_raw.CAST_TO_RAW(PC$Chunk));
      dbms_lob.WRITEAPPEND(GL$Blob, utl_raw.LENGTH(rawData), rawData);
    END IF ;
  END Set_B64_Chunk;

  PROCEDURE Init_Transfer
  IS
  BEGIN
    DBMS_LOB.CREATETEMPORARY(GL$Blob,FALSE);
    DBMS_LOB.OPEN(GL$Blob,DBMS_LOB.LOB_READWRITE);
    GN$Pos := 1 ;
  END Init_Transfer;


  FUNCTION  Save_Blob
    ( 
	   PC$Table   IN VARCHAR2
	  ,PC$Column  IN VARCHAR2
	  ,PC$Where   IN VARCHAR2  
    )
  RETURN VARCHAR2
  IS
    LB$Blob    BLOB ;
    LC$Query   VARCHAR2(512) ;
	LC$Query2  VARCHAR2(512) ;
	
  BEGIN
     -- get the locator to the table blob
     LC$Query := 'select '||PC$Column||' from '||PC$Table||' where '||PC$Where||' for update';	 
     EXECUTE IMMEDIATE LC$Query INTO LB$Blob;

     -- Check the blob has been initialised
     -- and if it's not empty clear it out
     IF LB$Blob IS NULL THEN
       LC$Query2 := 'update '||PC$Table||' set '||PC$Column||'=EMPTY_BLOB()  where '||PC$Where;
       EXECUTE IMMEDIATE LC$Query2;
       EXECUTE IMMEDIATE LC$Query INTO LB$Blob;
     ELSIF dbms_lob.getlength(LB$Blob) > 0 THEN
       dbms_lob.TRIM(LB$Blob,0);
     END IF;
     -- now replace the table data with the temp BLOB
     DBMS_LOB.APPEND(LB$Blob,GL$Blob);
     DBMS_LOB.CLOSE(GL$Blob);
    RETURN 'OK' ;
  EXCEPTION
    WHEN OTHERS THEN
      RETURN SQLERRM ;
  END Save_Blob ;


  ----------------------------
  --   CLOB/NCLOB methods   --
  ----------------------------
  
  ----------------------------------------
  -- Get the content of the CLOB column --
  ----------------------------------------
  FUNCTION select_clob ( PC$Clause IN VARCHAR2 ) RETURN BOOLEAN
  IS
  BEGIN
    execute immediate PC$Clause into GL$Clob ;
    GN$Pos := 1 ;
    RETURN TRUE ;
  EXCEPTION
    WHEN OTHERS THEN
	  RETURN FALSE ;
  END select_clob ;
 
  -----------------------------------------
  -- Get the content of the NCLOB column --
  -----------------------------------------
  FUNCTION select_nclob ( PC$Clause IN VARCHAR2 ) RETURN BOOLEAN
  IS
  BEGIN
    execute immediate PC$Clause into GL$NClob ;
    GN$Pos := 1 ;
    RETURN TRUE ;
  EXCEPTION
    WHEN OTHERS THEN
	  RETURN FALSE ;
  END select_nclob ;
 
  ------------------------
  --  get a CLOB chunk  --
  ------------------------
  FUNCTION Get_Chunk RETURN VARCHAR2
  IS
    LC$Chunk  VARCHAR2(32767) ;
  BEGIN
    LC$Chunk := dbms_lob.substr( GL$Clob, GN$ChunkSize, GN$Pos ) ;
    GN$Pos := GN$Pos + GN$ChunkSize ;
    RETURN LC$Chunk ;
  END Get_Chunk ;  
 
  -------------------------
  --  get a NCLOB chunk  --
  -------------------------
  FUNCTION Get_NChunk RETURN VARCHAR2
  IS
    LC$Chunk  VARCHAR2(32767) ;
  BEGIN
	LC$Chunk := dbms_lob.substr( GL$NClob, GN$ChunkSize, GN$Pos  ) ;
	GN$Pos := GN$Pos + GN$ChunkSize ;
    RETURN LC$Chunk ;
  EXCEPTION
    WHEN OTHERS THEN
	  RETURN NULL ;
  END Get_NChunk ;
  
  --------------------------
  --  set the chunk size  --
  --------------------------
  PROCEDURE Set_ChunkSize ( PN$Size IN PLS_INTEGER )
  IS
  Begin
    If Nvl(PN$Size,0) between 128 and 32767 Then
      GN$ChunkSize := PN$Size  ;
    End if ;
  End Set_ChunkSize;
  

  ----------------------------------
  --  set the chunks to populate  --
  --  the CLOB column             --
  ----------------------------------
  PROCEDURE Set_Chunk ( PC$Chunk IN VARCHAR2 )
  Is
  Begin
    If PC$Chunk Is Not Null Then
      dbms_lob.append(GL$Clob, PC$Chunk);
    End if ;
  End Set_Chunk;

  ----------------------------------
  --  set the chunks to populate  --
  --  the NCLOB column             --
  ----------------------------------
  PROCEDURE Set_NChunk ( PC$Chunk IN VARCHAR2 )
  Is
  Begin
    If PC$Chunk Is Not Null Then
      GL$NClob := GL$NClob || PC$Chunk ;  
    End if ;
  End Set_NChunk;
  
  PROCEDURE Init_Transfer_lob ( PC$Type IN VARCHAR2 )
  Is
  Begin
    If Upper(PC$Type) = 'CLOB' Then
      dbms_lob.createtemporary( GL$Clob, TRUE ) ;
    ElsIf Upper(PC$Type) = 'NCLOB' Then
      dbms_lob.createtemporary( GL$NClob, TRUE ) ;
    End if ;
  End Init_Transfer_lob;
  
  
  FUNCTION  Transfer( PC$Clause IN VARCHAR2, PC$Type IN VARCHAR2 )
  RETURN VARCHAR2
  Is
  Begin
    If Upper(PC$Type) = 'CLOB' Then
      execute immediate PC$Clause using GL$Clob ;
    Else
      execute immediate PC$Clause using GL$NClob ;
    End if ;
    return 'OK' ;
  Exception
    When Others Then
      return SQLERRM ;
  End Transfer ;
 

END PKG_LAF;
/
