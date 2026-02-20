PROMPT CREATE OR REPLACE PACKAGE pkg_db_laf_lov
CREATE OR REPLACE PACKAGE pkg_db_laf_lov AS
 ----------------------------------------
 --                                    --
 --  Database package to handle        --
 --  dynamic queries in order          --
 --  to populate Swing LAF LOVs        --
 --                                    --
 --  Francois Degrelle                 --
 --  creation     : June 2009          --
 --  version      : 1.2                --
 --                                    --
 ----------------------------------------


  TYPE       TYP_TAB_CHAR IS TABLE OF VARCHAR2(100) INDEX BY BINARY_INTEGER ;
  TabCols    TYP_TAB_CHAR ;

  --  record to handle LOV's column mapping   --
  TYPE       LOV_MAPPING IS RECORD
             (
                LOV_COLUMN         VARCHAR2(100)
               ,LOV_COL_MIN_WIDTH  PLS_INTEGER
               ,LOV_COL_MAX_WIDTH  PLS_INTEGER
               ,LOV_ITEM1          VARCHAR2(100)
               ,LOV_ITEM2          VARCHAR2(100)
               ,LOV_ITEM3          VARCHAR2(100)
             );
  TYPE       TYP_TAB_LOV_MAPPING IS TABLE OF LOV_MAPPING INDEX BY BINARY_INTEGER ;

  --  store the COLUMN's type   --
  TYPE       LOV_COL_TYPE IS RECORD
             (
                LOV_COLUMN         VARCHAR2(100)
               ,LOV_COL_TYPE       VARCHAR2(30) DEFAULT 'CHAR'
             );
  TYPE       TYP_TAB_LOV_COL_TYPE IS TABLE OF LOV_COL_TYPE INDEX BY BINARY_INTEGER ;

  --  store the COLUMN's width   --
  TYPE       LOV_COL_WIDTH IS RECORD
             (
                LOV_COLUMN         VARCHAR2(100)
               ,WIDTH              PLS_INTEGER DEFAULT -1
               ,MIN_WIDTH          PLS_INTEGER DEFAULT -1
               ,MAX_WIDTH          PLS_INTEGER DEFAULT -1
             );
  TYPE       TYP_TAB_LOV_COL_WIDTH IS TABLE OF LOV_COL_WIDTH INDEX BY BINARY_INTEGER ;

  --  store the Item Validation correspondances  --
  TYPE       LOV_COL_ITEM_VAL IS RECORD
             (
                LOV_ITEM         VARCHAR2(100)
               ,LOV_COLUMN       VARCHAR2(100)
             );
  TYPE       TYP_TAB_LOV_COL_ITEM_VAL IS TABLE OF LOV_COL_ITEM_VAL INDEX BY BINARY_INTEGER ;

  --  record to handle LOV's properties  --
  TYPE       LOV_RECORD IS RECORD
             (
                LOV_NAME              VARCHAR2(30)
               ,LOV_FORM              VARCHAR2(100)
               ,LOV_BEAN_NAME         VARCHAR2(128)
               ,LOV_TITLE             VARCHAR2(256)
               ,LOV_SELECT            VARCHAR2(32000)
               ,LOV_PROMPT            VARCHAR2(256)
               ,LOV_CHECK             BOOLEAN DEFAULT FALSE
               ,LOV_WIDTH             PLS_INTEGER DEFAULT -1
               ,LOV_HEIGHT            PLS_INTEGER DEFAULT 400
               ,LOV_X_POSITION        PLS_INTEGER DEFAULT 0
               ,LOV_Y_POSITION        PLS_INTEGER DEFAULT 0
               ,LOV_MAX_COL_WIDTH     PLS_INTEGER DEFAULT -1
               ,LOV_COL_SEARCH        VARCHAR2(100)
               ,LOV_COL_SEARCH_TYPE   VARCHAR2(10)
               ,LOV_VALIDATION        VARCHAR2(1) DEFAULT 'N'
               ,LOV_SCHEME            VARCHAR2(100) DEFAULT NULL
               ,LOV_PAGING            PLS_INTEGER   DEFAULT -1
               ,LOV_BUTTON1           VARCHAR2(100)
               ,LOV_BUTTON2           VARCHAR2(100)
               ,LOV_NB_PAGES          PLS_INTEGER   DEFAULT 1
               ,LOV_MAPPING           TYP_TAB_LOV_MAPPING
               ,LOV_TYPES             TYP_TAB_LOV_COL_TYPE
               ,LOV_ITEMS             TYP_TAB_CHAR
               ,LOV_ITEM_VALID        TYP_TAB_LOV_COL_ITEM_VAL
               ,LOV_WHERE_CLAUSE      VARCHAR2(1000) DEFAULT NULL
               ,LOV_ORDER_BY          VARCHAR2(1000) DEFAULT NULL
               ,COL_WIDTH             TYP_TAB_LOV_COL_WIDTH
               ,LOV_FILTER            VARCHAR2(1000)
             );
  TYPE       TYP_TAB_LOV_RECORD IS TABLE OF LOV_RECORD INDEX BY BINARY_INTEGER ;
  TabLOVs    TYP_TAB_LOV_RECORD;


 -- get a copy of tab LOVs --
  FUNCTION Get_Table_LOVS
  RETURN TYP_TAB_LOV_RECORD ;

  -- add a LOV description --
 FUNCTION   Add_Lov
            (
               PC$BeanName   IN VARCHAR2
              ,PC$LOV_Name   IN VARCHAR2
              ,PC$LOV_Form   IN VARCHAR2
              ,PR$Attributes IN LOV_RECORD
              ,PC$Replace    IN VARCHAR2 DEFAULT 'R'
            )
 RETURN     VARCHAR2 ;

  -- add a LOV description --
 FUNCTION   Add_Lov
            (
               LOV_BEAN_NAME      IN VARCHAR2
              ,LOV_NAME           IN VARCHAR2
              ,LOV_FORM           IN VARCHAR2
              ,LOV_TITLE          IN VARCHAR2
              ,LOV_SELECT         IN VARCHAR2
              ,LOV_PROMPT         IN VARCHAR2    DEFAULT NULL
              ,LOV_CHECK          IN BOOLEAN     DEFAULT FALSE
              ,LOV_WIDTH          IN PLS_INTEGER DEFAULT 400
              ,LOV_HEIGHT         IN PLS_INTEGER DEFAULT 400
              ,LOV_X_POSITION     IN PLS_INTEGER DEFAULT 0
              ,LOV_Y_POSITION     IN PLS_INTEGER DEFAULT 0
              ,LOV_MAX_COL_WIDTH  IN PLS_INTEGER DEFAULT -1
              ,LOV_COL_SEARCH     IN VARCHAR2    DEFAULT NULL
              ,LOV_VALIDATION     IN VARCHAR2    DEFAULT 'N'
              ,LOV_SCHEME         IN VARCHAR2    DEFAULT NULL
              ,LOV_PAGING         PLS_INTEGER    DEFAULT -1
              ,LOV_BUTTON1        IN VARCHAR2    DEFAULT NULL
              ,LOV_BUTTON2        IN VARCHAR2    DEFAULT NULL
              ,REPLACE            IN VARCHAR2    DEFAULT 'R'
            )
 RETURN     VARCHAR2 ;

 -- add a LOV mapping description --
 FUNCTION   Set_Lov_Mapping
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
              ,PR$Mappings    IN TYP_TAB_LOV_MAPPING
            )
 RETURN     VARCHAR2 ;

 -- add a LOV validation description --
 FUNCTION   Set_Lov_Validations
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
              ,PR$Valids      IN TYP_TAB_LOV_COL_ITEM_VAL
            )
 RETURN     VARCHAR2 ;

 -- List of items that support the LOV --
 FUNCTION   Set_Lov_Items
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
              ,PR$Items       IN TYP_TAB_CHAR
            )
 RETURN     VARCHAR2 ;

  -- add Item that supports the LOV --
 FUNCTION   Add_Lov_Item
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
              ,PC$LOV_Item    IN VARCHAR2
            )
 RETURN     VARCHAR2 ;

  -- get a LOV record --
 FUNCTION   Get_Lov_Record
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
            )
 RETURN     LOV_RECORD ;

 -- add a LOV mapping description --
 FUNCTION   Add_Lov_Mapping_Item
            (
                LOV_Name           IN VARCHAR2
               ,LOV_Form           IN VARCHAR2
               ,LOV_COLUMN         IN VARCHAR2
               ,LOV_COL_MIN_WIDTH  IN PLS_INTEGER  DEFAULT NULL
               ,LOV_COL_MAX_WIDTH  IN PLS_INTEGER  DEFAULT NULL
               ,LOV_ITEM1          IN VARCHAR2
               ,LOV_ITEM2          IN VARCHAR2     DEFAULT NULL
               ,LOV_ITEM3          IN VARCHAR2     DEFAULT NULL
            )
 RETURN     VARCHAR2 ;

 -- remove a LOV description --
 PROCEDURE  Remove_Lov
           (
                PC$LOV_Name  IN VARCHAR2
               ,PC$LOV_Form  IN VARCHAR2
     ) ;

 -- clear LOV mappings --
 PROCEDURE  Clear_Mappings
           (
                PC$LOV_Name  IN VARCHAR2
               ,PC$LOV_Form  IN VARCHAR2
     ) ;

 -- build the LOV Where clause --
 PROCEDURE  Build_LOV_Where_Clause
           (
                PC$LOV_Name  IN VARCHAR2
               ,PC$LOV_Form  IN VARCHAR2
               ,PC$Value     IN VARCHAR2
     ) ;

 -- set the LOV Order By --
 PROCEDURE  Set_LOV_OrderBy
           (
                PC$LOV_Name  IN VARCHAR2
               ,PC$LOV_Form  IN VARCHAR2
               ,PC$Value     IN VARCHAR2
     ) ;

 -- get a LOV property --
 FUNCTION  Get_Lov_Property
            (
               PC$LOV_Name     IN VARCHAR2
              ,PC$LOV_Form     IN VARCHAR2
              ,PC$Property     IN VARCHAR2
            )
 RETURN VARCHAR2 ;

 --  Find LOV for item  --
 FUNCTION Get_Lov_Item
          (
               PC$Item_Name    IN VARCHAR2
              ,PC$LOV_Form     IN VARCHAR2
              ,PC$LOV_Item     IN VARCHAR2
              ,PC$LOV_Name    OUT VARCHAR2
              ,PC$BeanName    OUT VARCHAR2
              ,PC$Column      OUT VARCHAR2
              ,PC$Validation  OUT VARCHAR2
    )
 RETURN VARCHAR2 ;

 -----------------------
 --   Open a cursor   --
 -----------------------
 FUNCTION Open_Cursor
 (
     PC$Query      IN  VARCHAR2
    ,PN$Paging     IN PLS_INTEGER DEFAULT -1
    ,PN$Page       IN PLS_INTEGER DEFAULT 1
    ,PC$Search     IN VARCHAR2  DEFAULT NULL
 )
  RETURN PLS_INTEGER ;

  -----------------------------
  --  Fetch from the cursor  --
  -----------------------------
  FUNCTION Fetch_Cursor
  RETURN   VARCHAR2 ;

  PROCEDURE Close_Cursor ;

 FUNCTION Prepare_LOV
 (
     PC$LOV_Name    IN  VARCHAR2
    ,PC$LOV_Form    IN  VARCHAR2
    ,PN$NbCols      OUT PLS_INTEGER
    ,PN$NbRows      OUT PLS_INTEGER
    ,PC$Head        OUT VARCHAR2
    ,PC$Type        OUT VARCHAR2
    ,PN$MaxWidth    OUT PLS_INTEGER
    ,PC$Prompt      OUT VARCHAR2
    ,PC$Title       OUT VARCHAR2
    ,PC$Bounds      OUT VARCHAR2
    ,PC$Query       OUT VARCHAR2
    ,PC$ColSearch   OUT VARCHAR2
    ,PC$Validation  OUT VARCHAR2
    ,PC$Scheme      OUT VARCHAR2
    ,PN$Paging      OUT PLS_INTEGER
    ,PN$NbPages     OUT PLS_INTEGER
    ,PC$Button1     OUT VARCHAR2
    ,PC$Button2     OUT VARCHAR2
 )
  RETURN VARCHAR2 ;

  PROCEDURE Set_LOV_Property
          (
               PC$LOV_Name       IN VARCHAR2
              ,PC$LOV_Form       IN VARCHAR2
              ,PC$PropertyName   IN VARCHAR2
              ,PC$PropertyValue  IN VARCHAR2
          );

  FUNCTION Get_LOV_Mapping ( LN$RecNum   IN PLS_INTEGER )
  RETURN TYP_TAB_LOV_MAPPING ;

  FUNCTION Get_LOV_Indice
           (
              PC$LOV_Name  IN VARCHAR2
             ,PC$LOV_form  IN VARCHAR2
     )
  RETURN   PLS_INTEGER ;

  -- return the column's type --
  FUNCTION Get_LOV_Col_Type
           (
              PC$LOV_Name  IN VARCHAR2
             ,PC$LOV_Form  IN VARCHAR2
             ,PC$Col_Name  IN VARCHAR2
   )
  RETURN   VARCHAR2 ;

  FUNCTION SPLIT
 (
    PC$Chaine IN VARCHAR2,         -- input string
    PN$Pos IN PLS_INTEGER,         -- token number
    PC$Sep IN VARCHAR2 DEFAULT ',' -- separator character
 ) RETURN VARCHAR2 ;

  --  Check if value exists for LOV validation --
  FUNCTION Check_Value
  (
    PC$LOV_Name  IN  VARCHAR2
   ,PC$LOV_Form  IN  VARCHAR2
   ,PC$LOV_Col   IN  VARCHAR2
   ,PC$Value     IN  VARCHAR2
   ,PC$ErrorMsg  OUT VARCHAR2
  ) RETURN PLS_INTEGER ;

  FUNCTION getSearchLabel RETURN VARCHAR2 ;

  FUNCTION getSeparator RETURN VARCHAR2 ;

  PROCEDURE setSearchLabel ( PC$Label IN VARCHAR2 ) ;

  PROCEDURE setSeparator ( PC$Separator IN VARCHAR2 ) ;

 -- add a LOV mapping description --
 FUNCTION   Set_Lov_Col_Width
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
              ,PR$Widths      IN TYP_TAB_LOV_COL_WIDTH
            )
 RETURN     VARCHAR2 ;

 -- get the LOV column width --
 FUNCTION  Get_Lov_Col_Width
 (
    PC$LOV_Name  IN  VARCHAR2
   ,PC$LOV_Form  IN  VARCHAR2
 ) RETURN TYP_TAB_LOV_COL_WIDTH;


END Pkg_Db_Laf_Lov;
/

PROMPT CREATE OR REPLACE PACKAGE BODY pkg_db_laf_lov
CREATE OR REPLACE PACKAGE BODY pkg_db_laf_lov AS

  GC$SearchLabel   VARCHAR2(100) := 'Define search column' ;
  GC$Sep           VARCHAR2(1) := '^' ;

  GC$source_cursor INTEGER ;
  GN$Current_Page  PLS_INTEGER := 1 ;
  GC$rec_tab       DBMS_SQL.DESC_TAB;


 -----------------------
 --  Prepare the LOV  --
 -----------------------
 FUNCTION Prepare_LOV
 (
    PC$LOV_Name    IN  VARCHAR2
   ,PC$LOV_Form    IN  VARCHAR2
   ,PN$NbCols      OUT PLS_INTEGER
   ,PN$NbRows      OUT PLS_INTEGER
   ,PC$Head        OUT VARCHAR2
   ,PC$Type        OUT VARCHAR2
   ,PN$MaxWidth    OUT PLS_INTEGER
   ,PC$Prompt      OUT VARCHAR2
   ,PC$Title       OUT VARCHAR2
   ,PC$Bounds      OUT VARCHAR2
   ,PC$Query       OUT VARCHAR2
   ,PC$ColSearch   OUT VARCHAR2
   ,PC$Validation  OUT VARCHAR2
   ,PC$Scheme      OUT VARCHAR2
   ,PN$Paging      OUT PLS_INTEGER
   ,PN$NbPages     OUT PLS_INTEGER
   ,PC$Button1     OUT VARCHAR2
   ,PC$Button2     OUT VARCHAR2
 )
  RETURN VARCHAR2
  IS
   iNbcols       PLS_INTEGER := 0 ;
   iNbrows       PLS_INTEGER := 0 ;
   LC$Line       VARCHAR2(32767) ;
   LC$Head       VARCHAR2(4000) ;
   LC$Type       VARCHAR2(4000) ;
   c             NUMBER;
   d             NUMBER;
   col_cnt       PLS_INTEGER;
   v             VARCHAR2(4000) ;
   col_num       NUMBER;
   source_cursor INTEGER;
   result        INTEGER;
   LC$Qry        VARCHAR2(32000) ;
   LC$Query      VARCHAR2(32000) ;
   LC$Count      VARCHAR2(1000) ;
   LC$Rowid      VARCHAR2(100) ;
   LC$Form       VARCHAR2(100);
   LC$Button1    VARCHAR2(100) ;
   LC$Button2    VARCHAR2(100) ;
   LC$BeanName   VARCHAR2(128);
   LC$Image      VARCHAR2(32767) ;
   LB$Found      BOOLEAN := FALSE ;
   LN$Width      PLS_INTEGER ;
   LN$Height     PLS_INTEGER ;
   LN$X_Pos      PLS_INTEGER := 0 ;
   LN$Y_Pos      PLS_INTEGER := 0 ;
   LN$Paging     PLS_INTEGER ;
   LC$Title      VARCHAR2(256) ;
   LC$Prompt     VARCHAR2(512) ;
   LC$ColSearch  VARCHAR2(100) ;
   LC$Validation VARCHAR2(1) ;
   LC$Scheme     VARCHAR2(100) ;
   LN$MaxWidth   PLS_INTEGER ;
   LN$Indice     PLS_INTEGER ;
   LN$MaxPages   PLS_INTEGER := 1 ;
   LR$Types      TYP_TAB_LOV_COL_TYPE ;
   LI$Start      PLS_INTEGER := 1;
   LI$End        PLS_INTEGER := -1;

 BEGIN

   --  find the LOV --
   IF TabLOVs.COUNT = 0 THEN
       RETURN 'No LOV defined';
   END IF ;
   FOR i IN 1 .. TabLOVs.COUNT LOOP
       IF TabLOVs.EXISTS(i) THEN
           IF  LOWER(PC$LOV_Name) = LOWER(TabLOVs(i).LOV_NAME)
           AND LOWER(PC$LOV_Form) = LOWER(TabLOVs(i).LOV_FORM)THEN
             LC$Qry         := TabLOVs(i).LOV_SELECT ;
             LC$BeanName    := TabLOVs(i).LOV_BEAN_NAME ;
             LN$Width       := TabLOVs(i).LOV_WIDTH ;
             LN$Height      := TabLOVs(i).LOV_HEIGHT ;
             LN$X_Pos       := TabLOVs(i).LOV_X_POSITION ;
             LN$Y_Pos       := TabLOVs(i).LOV_Y_POSITION ;
             LC$Title       := TabLOVs(i).LOV_TITLE ;
             LC$Prompt      := TabLOVs(i).LOV_PROMPT ;
             LC$ColSearch   := TabLOVs(i).LOV_COL_SEARCH ;
             LC$Validation  := TabLOVs(i).LOV_VALIDATION ;
             LC$Scheme      := TabLOVs(i).LOV_SCHEME ;
             LN$Paging      := TabLOVs(i).LOV_PAGING ;
             LC$Button1     := TabLOVs(i).LOV_BUTTON1 ;
             LC$Button2     := TabLOVs(i).LOV_BUTTON2 ;
             LN$MaxWidth    := TabLOVs(i).LOV_MAX_COL_WIDTH ;
             LN$MaxPages    := TabLOVs(i).LOV_NB_PAGES ;
             LB$Found       := TRUE ;
             LN$Indice := i ;
             EXIT ;
           END IF ;
       END IF ;
      END LOOP ;
      IF NOT LB$Found THEN
         RETURN 'LAF LOV : '|| PC$LOV_Form || '.' || PC$LOV_Name || ' not found';
      END IF ;

      GC$rec_tab.DELETE;

     LC$Query := LC$Qry ;

     IF LN$Paging > -1 THEN
       LI$Start :=  1 ;
       LI$End   := LN$Paging ;
     Else
       LI$End   := 9999999 ;
     END IF ;
     LC$Query := 'SeLeCt * FrOm ( select subreq.*, rownum r from ( '
              || LC$Qry
              || ' ) subreq where rownum <= ' || LI$End || ') where r >= ' || LI$Start ;

     --LC$Count := LOWER(Replace( LC$Query, 'SeLeCt * FrOm', 'select count(*) from'));
     LC$Count := Replace( LC$Query, 'SeLeCt * FrOm', 'select count(*) from');
     -- Count the rows --
     source_cursor := DBMS_SQL.OPEN_CURSOR;
     DBMS_SQL.PARSE(source_cursor,  LC$Count, 1);
     DBMS_SQL.DEFINE_COLUMN(source_cursor, 1, v,4000);
     result := DBMS_SQL.EXECUTE(source_cursor);
     IF DBMS_SQL.FETCH_ROWS(source_cursor)>0 THEN
          DBMS_SQL.COLUMN_VALUE(source_cursor, 1, v);
          iNbrows := v ;
     END IF;
     DBMS_SQL.CLOSE_CURSOR(source_cursor);

     -- count the number of pages --
     IF TabLOVs(LN$Indice).LOV_PAGING > -1 THEN
       LN$MaxPages := TRUNC(iNbrows / TabLOVs(LN$Indice).LOV_PAGING, 0) ;
       IF LN$MaxPages * TabLOVs(LN$Indice).LOV_PAGING < iNbrows THEN
          LN$MaxPages := LN$MaxPages + 1 ;
       END IF ;
       TabLOVs(LN$Indice).LOV_NB_PAGES := LN$MaxPages ;
  ELSE
       TabLOVs(LN$Indice).LOV_NB_PAGES := 1 ;
     END IF ;

     -- retrieve the columns of the query --
     c := DBMS_SQL.OPEN_CURSOR;

     DBMS_SQL.PARSE(c, LC$Query , 1);

     d := DBMS_SQL.EXECUTE(c);

     DBMS_SQL.DESCRIBE_COLUMNS(c, col_cnt, GC$rec_tab);

     iNbcols := 0 ;

     Pkg_Db_Laf_Lov.TabCols.DELETE ;

     d := 1 ;

     FOR i IN GC$rec_tab.FIRST .. GC$rec_tab.LAST-1 LOOP
       -- BLOB -> 113
       IF GC$rec_tab(i).col_type IN (1,2,11,12,96,113) THEN
         IF d > 1 THEN
            LC$Type := LC$Type || GC$Sep ;
         ELSE
            If LC$ColSearch Is Null Then
               LC$ColSearch := GC$rec_tab(i).col_name ;
               Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH', LC$ColSearch);
               If GC$rec_tab(i).col_type in (2) Then
                  If GC$rec_tab(i).col_scale =0 Then
                    Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH_TYPE', 'INTEGER');
                  ELSE
                    Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH_TYPE', 'NUMBER');
                  END IF ;
               ElsIf GC$rec_tab(i).col_type in (12) Then
                  Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH_TYPE', 'DATE');
               Else
                  Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH_TYPE', 'CHAR');
               End if ;
            End if ;
         END IF ;

         -- column type --
         LR$Types(i).LOV_COLUMN := GC$rec_tab(i).col_name ;
         IF GC$rec_tab(i).col_type = 2 THEN
           If GC$rec_tab(i).col_scale =0 Then
             LR$Types(i).LOV_COL_TYPE := 'INTEGER' ;
           ELSE
             LR$Types(i).LOV_COL_TYPE := 'NUMBER' ;
           END IF ;
         ELSIF GC$rec_tab(i).col_type = 12 THEN
           LR$Types(i).LOV_COL_TYPE := 'DATE' ;
         ELSIF GC$rec_tab(i).col_type = 113 THEN
           LR$Types(i).LOV_COL_TYPE := 'IMAGE' ;
         ELSE
           LR$Types(i).LOV_COL_TYPE := 'CHAR' ;
         END IF ;

         LC$Type := LC$Type || LR$Types(i).LOV_COL_TYPE; --'CHAR' ;
         IF d > 1 THEN LC$Head := LC$Head || GC$Sep ; END IF ;
         LC$Head := LC$Head || INITCAP(GC$rec_tab(i).col_name) ;
         Pkg_Db_Laf_Lov.TabCols(d) := GC$rec_tab(i).col_name ;
         d := d + 1 ;
         iNbcols := iNbcols + 1 ;
       END IF ;
     END LOOP ;


     DBMS_SQL.CLOSE_CURSOR(c);

     TabLOVs(LN$Indice).LOV_TYPES := LR$Types ;

     -- Init all variables --
     PC$Query       := LC$Qry ;
     PN$NbCols      := iNbcols ;
     PN$NbRows      := iNbrows ;
     PC$Head        := LC$Head ;
     PC$Type        := LC$Type ;
     PN$MaxWidth    := LN$MaxWidth ;
     PC$Prompt      := LC$Prompt ;
     PC$Title       := LC$Title ;
     PC$ColSearch   := LC$ColSearch ;
     PC$Validation  := LC$Validation ;
     PC$Scheme      := LC$Scheme ;
     PN$Paging      := LN$Paging ;
     PC$Button1     := LC$Button1 ;
     PC$Button2     := LC$Button2 ;
     PN$NBPages     := LN$MaxPages ;
     PC$Bounds      := Ltrim(To_Char(LN$X_Pos)) || ',' || Ltrim(To_Char(LN$Y_Pos)) || ',' || LTRIM(TO_CHAR(LN$Width)) || ',' || LTRIM(TO_CHAR(LN$Height)) ;

     RETURN 'OK' ;

  EXCEPTION
    WHEN OTHERS THEN
      RETURN 'LAF LOV error: ' || SQLERRM ;

  END Prepare_LOV;

 -----------------------
 --   Open a cursor   --
 -----------------------
 FUNCTION Open_Cursor
 (
     PC$Query      IN  VARCHAR2
    ,PN$Paging     IN PLS_INTEGER DEFAULT -1
    ,PN$Page       IN PLS_INTEGER DEFAULT 1
    ,PC$Search     IN VARCHAR2  DEFAULT NULL
 )
  RETURN PLS_INTEGER
  IS
   v             VARCHAR2(4000) ;
   col_num       NUMBER;
   LC$Query      VARCHAR2(32000) ;
   LI$Start      PLS_INTEGER := 1 ;
   LI$End        PLS_INTEGER := 999999999;
   LI$Count1     PLS_INTEGER := 0 ;
   LI$Count2     PLS_INTEGER := 0 ;
   LC$Op         VARCHAR2(10) := ' WHERE ' ;
   result        INTEGER;
 BEGIN
     -----------------------
     --  Open the cursor  --
     -----------------------
     --f_trace('Open_Cursor() search:' || PC$Search,'T');
     LC$Query := PC$Query ;

     IF PN$Paging > -1 THEN
       LI$Start := (PN$Paging * (PN$Page - 1)) + 1 ;
       LI$End   := PN$Paging * PN$Page ;
     END IF ;
     -- Where clause filter to add ? --
     If PC$Search Is Not Null Then
        If Instr( Lower( LC$Query ), 'where' ) > 0 Then
           LC$Op := ' AND ' ;
        End if ;
        LI$Count1 := Instr( Lower( LC$Query ), 'group by' ) ;
        If LI$Count1 > 0 Then
           LC$Query := Substr( LC$Query, 1, LI$Count1 - 1)
                       || LC$Op || PC$Search
                       || Substr( LC$Query, LI$Count1 ) ;
        End if ;
        LI$Count2 := Instr( Lower( LC$Query ), 'order by' ) ;
        If LI$Count2 > 0 Then
           LC$Query := Substr( LC$Query, 1, LI$Count2 - 1)
                       || LC$Op || PC$Search
                       || Substr( LC$Query, LI$Count2 ) ;
        End if ;
        IF LI$Count1 + LI$Count2 = 0 THEN
           LC$Query := LC$Query || LC$Op || PC$Search ;
        END IF ;
     End if ;

     LC$Query := 'SELECT * from ( select subreq.*, rownum r from ( '
              || LC$Query
              || ' ) subreq where rownum <= ' || LI$End || ') where r >= ' || LI$Start ;
     --f_trace('PKG_DB_LAF_LOV.Open_Cursor() :' || LC$Query,'T');
     GC$source_cursor := DBMS_SQL.OPEN_CURSOR;
     DBMS_SQL.PARSE(GC$source_cursor,  LC$Query, 1);
     -- Define the columns --
     FOR i IN 1 .. GC$rec_tab.LAST LOOP
       IF GC$rec_tab(i).col_type IN (1,2,11,12,96) THEN
         DBMS_SQL.DEFINE_COLUMN(GC$source_cursor, i, v,4000);
       END IF ;
     END LOOP ;

     result := DBMS_SQL.EXECUTE(GC$source_cursor);

     LI$Count1 := 0 ;
     Loop
       If DBMS_SQL.FETCH_ROWS(GC$source_cursor)>0 THEN
         LI$Count1 := LI$Count1 + 1 ;
       else
         exit ;
       End if ;
     End loop ;

     result := DBMS_SQL.EXECUTE(GC$source_cursor);


      RETURN LI$Count1 ;

  EXCEPTION
    WHEN OTHERS THEN
      RETURN -1 ;
  END Open_Cursor ;


  ------------------------------------
  --  Fetch a line from the cursor  --
  ------------------------------------
  FUNCTION Fetch_Cursor
  RETURN VARCHAR2
  IS
   d             NUMBER;
   v             VARCHAR2(4000) ;
   result        INTEGER;
   LC$Rowid      VARCHAR2(100) ;
   LC$Line       VARCHAR2(32767) ;
  BEGIN
       IF DBMS_SQL.FETCH_ROWS(GC$source_cursor)>0 THEN
         ----------------------------------
         -- get column values of the row --
         ----------------------------------
         LC$Line := '' ;
         d := 1 ;
         FOR i IN 1.. GC$rec_tab.LAST-1 LOOP
           IF GC$rec_tab(i).col_type IN (1,2,11,12,96,113) THEN
             IF GC$rec_tab(i).col_type IN (1,2,11,12,96) THEN
               DBMS_SQL.COLUMN_VALUE(GC$source_cursor, i, v);
             ELSE
              v := NULL ;
             END IF ;
             IF d > 1 THEN LC$Line := LC$Line || GC$Sep ; END IF ;
             IF GC$rec_tab(i).col_type = 11 THEN
               LC$Rowid := v ;
             END IF ;
              LC$Line := LC$Line || NVL(v,' ') ;
           END IF ;
           /*
           If GC$rec_tab(i).col_type = 113 Then
              --------------------
              -- send the image --
              --------------------
              LC$Query := 'Select ' || GC$rec_tab(i).col_name || ' From ' || PC$TableName
                || ' Where ROWID=''' || LC$Rowid || '''';
              If Pkg_Read_Blob_Image.Select_Blob(LC$Query) Then
              Set_Custom_Property( 'BL1.JTABLE', 1, 'SET_IMAGE', '[INDEX_IMAGE],'|| iNbRows || ',' || d) ;
              Loop
                 LC$Image := Pkg_Read_Blob_Image.Get_B64_Chunk ;
                 If LC$Image Is Not Null Then
                   Set_Custom_Property( 'BL1.JTABLE', 1, 'SET_IMAGE', LC$Image ) ;
                 Else
                   Set_Custom_Property( 'BL1.JTABLE', 1, 'SET_IMAGE', '[END_IMAGE]' ) ;
                   Exit ;
                 End if ;
              End loop ;
              End if ;
           End if ;
           */
           d := d + 1 ;
         END LOOP ;

         -- return data --
          RETURN LC$Line ;

       ELSE
         -- No more rows --
         RETURN NULL ;
       END IF;

  EXCEPTION
    WHEN OTHERS THEN
      RETURN NULL ;
  END Fetch_Cursor ;


  -------------------------------
  --  close the opened cursor  --
  -------------------------------
  PROCEDURE Close_Cursor
  IS
  BEGIN
    DBMS_SQL.CLOSE_CURSOR(GC$source_cursor);
  END Close_Cursor ;

 ---------------------------------
 --  add a new LOV description  --
 ---------------------------------
 FUNCTION   Add_Lov
 (
    PC$BeanName   IN VARCHAR2
   ,PC$LOV_Name   IN VARCHAR2
   ,PC$LOV_Form   IN VARCHAR2
   ,PR$Attributes IN LOV_RECORD
   ,PC$Replace    IN VARCHAR2 DEFAULT 'R'
 )
 RETURN     VARCHAR2
 IS
   LN$TotRow   PLS_INTEGER := TabLOVs.COUNT() ;
   LB$Return   BOOLEAN := TRUE ;
   LB$Found    BOOLEAN := FALSE ;
 BEGIN
   IF PR$Attributes.LOV_NAME IS NULL THEN
      RETURN 'LAF LOV description: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV Name must be defined' ;
   END IF ;
   IF PR$Attributes.LOV_BEAN_NAME IS NULL THEN
      RETURN 'LAF LOV description: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV Bean Area Name must be defined' ;
   END IF ;
   IF LN$TotRow > 0 THEN
      FOR i IN 1 .. TabLOVs.COUNT LOOP
        -- LOV already exists ? --
        IF TabLOVs.EXISTS(i) THEN
           IF  LOWER(PC$LOV_Name) = LOWER(TabLOVs(i).LOV_NAME)
           AND LOWER(PC$LOV_Form) = LOWER(TabLOVs(i).LOV_Form)THEN
              IF UPPER(PC$Replace) = 'C' THEN -- cancel
                   RETURN 'LAF LOV description: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV already defined' ;
              ELSIF UPPER(PC$Replace) = 'R' THEN -- replace
                --  LOV inscription --
                 TabLOVs(LN$TotRow + 1) := PR$Attributes ;
                 LB$Found := TRUE ;
                 EXIT ;
              ELSE -- Ignore creation (keep former definition)
                     RETURN 'OK' ;
                  END IF ;
           END IF ;
        END IF ;
      END LOOP ;
      IF NOT LB$Found THEN
        --  LOV inscription --
        TabLOVs(LN$TotRow + 1) := PR$Attributes ;
      END IF ;
   ELSE
      --  LOV inscription --
      TabLOVs(LN$TotRow + 1) := PR$Attributes ;
   END IF ;
   RETURN 'OK' ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
  END Add_Lov ;


  FUNCTION   Add_Lov
 (
    LOV_BEAN_NAME      IN VARCHAR2
   ,LOV_NAME           IN VARCHAR2
   ,LOV_FORM           IN VARCHAR2
   ,LOV_TITLE          IN VARCHAR2
   ,LOV_SELECT         IN VARCHAR2
   ,LOV_PROMPT         IN VARCHAR2    DEFAULT NULL
   ,LOV_CHECK          IN BOOLEAN     DEFAULT FALSE
   ,LOV_WIDTH          IN PLS_INTEGER DEFAULT 400
   ,LOV_HEIGHT         IN PLS_INTEGER DEFAULT 400
   ,LOV_X_POSITION     IN PLS_INTEGER DEFAULT 0
   ,LOV_Y_POSITION     IN PLS_INTEGER DEFAULT 0
   ,LOV_MAX_COL_WIDTH  IN PLS_INTEGER DEFAULT -1
   ,LOV_COL_SEARCH     IN VARCHAR2    DEFAULT NULL
   ,LOV_VALIDATION     IN VARCHAR2    DEFAULT 'N'
   ,LOV_SCHEME         IN VARCHAR2    DEFAULT NULL
   ,LOV_PAGING         IN PLS_INTEGER DEFAULT -1
   ,LOV_BUTTON1        IN VARCHAR2    DEFAULT NULL
   ,LOV_BUTTON2        IN VARCHAR2    DEFAULT NULL
   ,REPLACE            IN VARCHAR2    DEFAULT 'R'
 )
  RETURN     VARCHAR2
  IS
     LR$RecLOV   LOV_RECORD ;
  BEGIN
     LR$RecLOV.LOV_NAME           := LOV_NAME ;
     LR$RecLOV.LOV_FORM           := LOV_FORM ;
     LR$RecLOV.LOV_BEAN_NAME      := LOV_BEAN_NAME ;
     LR$RecLOV.LOV_TITLE          := LOV_TITLE ;
     LR$RecLOV.LOV_SELECT         := LOV_SELECT ;
     LR$RecLOV.LOV_PROMPT         := LOV_PROMPT ;
     LR$RecLOV.LOV_CHECK          := LOV_CHECK ;
     LR$RecLOV.LOV_WIDTH          := LOV_WIDTH ;
     LR$RecLOV.LOV_X_POSITION     := LOV_X_POSITION ;
     LR$RecLOV.LOV_Y_POSITION     := LOV_Y_POSITION ;
     LR$RecLOV.LOV_HEIGHT         := LOV_HEIGHT ;
     LR$RecLOV.LOV_MAX_COL_WIDTH  := LOV_MAX_COL_WIDTH ;
     LR$RecLOV.LOV_COL_SEARCH     := LOV_COL_SEARCH ;
     LR$RecLOV.LOV_VALIDATION     := LOV_VALIDATION ;
     LR$RecLOV.LOV_SCHEME         := LOV_SCHEME ;
     LR$RecLOV.LOV_PAGING         := LOV_PAGING ;
     LR$RecLOV.LOV_BUTTON1        := LOV_BUTTON1 ;
     LR$RecLOV.LOV_BUTTON2        := LOV_BUTTON2 ;
     RETURN
     Add_Lov
     (
       PC$BeanName    => LOV_BEAN_NAME
      ,PC$LOV_Name    => LOV_NAME
      ,PC$LOV_Form    => LOV_FORM
      ,PR$Attributes  => LR$RecLOV
      ,PC$Replace     => REPLACE
     ) ;
  EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
  END Add_Lov ;


 ----------------------------------
 --  build the LOV Where clause  --
 ----------------------------------
 PROCEDURE  Build_LOV_Where_Clause
           (
                PC$LOV_Name  IN VARCHAR2
               ,PC$LOV_Form  IN VARCHAR2
               ,PC$Value     IN VARCHAR2
     )
 IS
   LN$Indice  PLS_INTEGER ;
   LC$ColSearch   Varchar2(100 ) ;
   LC$ColType     Varchar2(10) ;
   LC$Clause      Varchar2(1000);
 Begin

   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
     If TabLOVs(LN$Indice).LOV_COL_SEARCH Is Null Then Return ; End if ;
     If PC$Value Is Null Then
        TabLOVs(LN$Indice).LOV_WHERE_CLAUSE := null ;
        Return ;
     End if ;
     LC$ColSearch := TabLOVs(LN$Indice).LOV_COL_SEARCH ;
     LC$ColType   := TabLOVs(LN$Indice).LOV_COL_SEARCH_TYPE ;
     If LC$ColType = 'NUMBER' Then
        LC$Clause := LC$ColSearch || ' LIKE ' || PC$Value || ' ' ;
     Else
        LC$Clause := LC$ColSearch || ' LIKE ''' || PC$Value || ''' ' ;
     End if ;
     TabLOVs(LN$Indice).LOV_WHERE_CLAUSE := LC$Clause ;

   End if ;
 End  Build_LOV_Where_Clause ;


  ----------------------------
  --  set the LOV Order By  --
  ----------------------------
 PROCEDURE  Set_LOV_OrderBy
     (
        PC$LOV_Name  IN VARCHAR2
       ,PC$LOV_Form  IN VARCHAR2
       ,PC$Value     IN VARCHAR2
     )
 Is
   LN$Indice  PLS_INTEGER ;
   LN$I       PLS_INTEGER ;
   LC$Query   Varchar(32000);
 Begin
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      TabLOVs(LN$Indice).LOV_ORDER_BY := PC$Value ;
      -- previous order by in query to replace ?
      LC$Query := Lower(TabLOVs(LN$Indice).LOV_SELECT);
      LN$I := Instr( LC$Query, 'order by' ) ;
      If LN$I > 0 Then
         LC$Query := Substr( TabLOVs(LN$Indice).LOV_SELECT, 1, LN$I-1 )
                     || ' ORDER BY ' || PC$Value ;
      Else
         LC$Query := TabLOVs(LN$Indice).LOV_SELECT || ' ORDER BY ' || PC$Value ;
      End if ;
      TabLOVs(LN$Indice).LOV_SELECT := LC$Query ;
      --f_trace('Set_LOV_OrderBy():' || TabLOVs(LN$Indice).LOV_SELECT ,'T');
   End if ;
 End  Set_LOV_OrderBy;


 -------------------------------------
 --  add a LOV mapping description  --
 -------------------------------------
 FUNCTION   Set_Lov_Mapping
 (
    PC$LOV_Name    IN VARCHAR2
   ,PC$LOV_Form    IN VARCHAR2
   ,PR$Mappings    IN TYP_TAB_LOV_MAPPING
 )
 RETURN     VARCHAR2
 IS
   LB$Return  BOOLEAN := TRUE ;
   LB$Found   BOOLEAN := FALSE ;
   LR$Mapp    TYP_TAB_LOV_MAPPING := PR$Mappings ;
   LN$Indice  PLS_INTEGER ;
 BEGIN
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      FOR j IN 1 .. PR$Mappings.COUNT LOOP
          LR$Mapp(j).LOV_COLUMN := '<' || LR$Mapp(j).LOV_COLUMN || '>' ;
      END LOOP;
      --  LOV mapping inscription --
      TabLOVs(LN$Indice).LOV_MAPPING := LR$Mapp ;
   ELSE
      RETURN 'LAF LOV Mapping: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV not found';
   END IF ;
   RETURN 'OK' ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
 END Set_Lov_Mapping ;



 ----------------------------------------
 --  add a LOV validation description  --
 ----------------------------------------
 FUNCTION   Set_Lov_Validations
 (
    PC$LOV_Name    IN VARCHAR2
   ,PC$LOV_Form    IN VARCHAR2
   ,PR$Valids      IN TYP_TAB_LOV_COL_ITEM_VAL
 )
 RETURN     VARCHAR2
 IS
   LB$Return  BOOLEAN := TRUE ;
   LB$Found   BOOLEAN := FALSE ;
   LN$Indice  PLS_INTEGER ;
 BEGIN
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      --  LOV validations inscription --
      TabLOVs(LN$Indice).LOV_ITEM_VALID := PR$Valids;
   ELSE
      RETURN 'LAF LOV Validations: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV not found';
   END IF ;
   RETURN 'OK' ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
 END Set_Lov_Validations ;



 --------------------------------
 --  remove a LOV description  --
 --------------------------------
 PROCEDURE  Remove_Lov ( PC$LOV_Name  IN VARCHAR2, PC$LOV_Form IN  VARCHAR2)
 IS
   LN$Indice   PLS_INTEGER ;
   tb_sav      TYP_TAB_LOV_RECORD ;
   j           PLS_INTEGER := 1 ;
 BEGIN
   IF UPPER(PC$LOV_Name) = 'ALL_LOVS' THEN
      IF tabLOVs.COUNT > 0 THEN
        FOR i IN tabLOVs.FIRST .. tabLOVs.LAST LOOP
           IF tabLOVs.EXISTS(i) THEN
             IF UPPER(tabLOVs(i).LOV_FORM) = UPPER(PC$LOV_Form) THEN
                tabLOVs.DELETE(i);
             END IF ;
           END IF ;
        END LOOP;
      END IF ;
      IF tabLOVs.COUNT > 0 THEN
        FOR i IN tabLOVs.FIRST .. tabLOVs.LAST LOOP
           IF tabLOVs.EXISTS(i) THEN
             tb_sav(j) := tabLOVs(i) ;
             j := j + 1 ;
           END IF ;
        END LOOP;
        tabLOVs := tb_sav ;
      END IF ;
   ELSE
     LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
     IF LN$Indice > 0 THEN
        tabLOVs.DELETE(LN$Indice) ;
     END IF ;
   END IF ;
 END Remove_Lov ;


 --------------------------
 --  clear LOV mappings  --
 --------------------------
 PROCEDURE  Clear_Mappings( PC$LOV_Name  IN VARCHAR2, PC$LOV_Form IN VARCHAR2)
 IS
   LN$Indice   PLS_INTEGER ;
 BEGIN
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      tabLOVs(LN$Indice).LOV_MAPPING.DELETE ;
   END IF ;
 END Clear_Mappings ;


 -- add a LOV mapping description --
 FUNCTION   Add_Lov_Mapping_Item
 (
    LOV_Name           IN VARCHAR2
   ,LOV_Form           IN VARCHAR2
   ,LOV_COLUMN         IN VARCHAR2
   ,LOV_COL_MIN_WIDTH  IN PLS_INTEGER  DEFAULT NULL
   ,LOV_COL_MAX_WIDTH  IN PLS_INTEGER  DEFAULT NULL
   ,LOV_ITEM1          IN VARCHAR2
   ,LOV_ITEM2          IN VARCHAR2     DEFAULT NULL
   ,LOV_ITEM3          IN VARCHAR2     DEFAULT NULL
 )
 RETURN     VARCHAR2
 IS
   LN$Indice   PLS_INTEGER ;
   LR$Mapping  LOV_MAPPING ;
   LN$Pos      PLS_INTEGER ;
 BEGIN
     LN$Indice := Get_LOV_Indice( LOV_Name, LOV_Form ) ;
     IF LN$Indice > 0 THEN

 LN$Pos   := TabLOVS(LN$Indice).LOV_MAPPING.COUNT ;

 LR$Mapping.LOV_COLUMN         := '<' || LOV_COLUMN || '>';
        LR$Mapping.LOV_COL_MIN_WIDTH  := LOV_COL_MIN_WIDTH ;
        LR$Mapping.LOV_COL_MAX_WIDTH  := LOV_COL_MAX_WIDTH ;
        LR$Mapping.LOV_ITEM1          := LOV_ITEM1 ;
        LR$Mapping.LOV_ITEM2          := LOV_ITEM2 ;
        LR$Mapping.LOV_ITEM3          := LOV_ITEM3 ;

 TabLOVS(LN$Indice).LOV_MAPPING(LN$Pos + 1) := LR$Mapping ;

 RETURN ('OK') ;
     END IF ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV Add  Mapping error: ' || SQLERRM ;
 END  Add_Lov_Mapping_Item ;


 -- get a copy of tab LOVs --
  FUNCTION Get_Table_LOVS
  RETURN TYP_TAB_LOV_RECORD
  IS
  BEGIN
    RETURN TabLOVs ;
  END   Get_Table_LOVS ;



 --------------------------
  --  get a string token  --
  --------------------------
 FUNCTION SPLIT
 (
    PC$Chaine IN VARCHAR2,         -- input string
    PN$Pos IN PLS_INTEGER,         -- token number
    PC$Sep IN VARCHAR2 DEFAULT ',' -- separator character
 )
 RETURN VARCHAR2
 IS
   LC$Chaine VARCHAR2(32767) := PC$Sep || PC$Chaine ;
   LI$I      PLS_INTEGER ;
   LI$I2     PLS_INTEGER ;
 BEGIN
   LI$I := INSTR( LC$Chaine, PC$Sep, 1, PN$Pos ) ;
   IF LI$I > 0 THEN
     LI$I2 := INSTR( LC$Chaine, PC$Sep, 1, PN$Pos + 1) ;
     IF LI$I2 = 0 THEN LI$I2 := LENGTH( LC$Chaine ) + 1 ; END IF ;
     RETURN( SUBSTR( LC$Chaine, LI$I+1, LI$I2 - LI$I-1 ) ) ;
   ELSE
     RETURN NULL ;
   END IF ;
 END SPLIT;


  -------------------------------------------------
  --  get a LOV_MAPPING record from a given LOV  --
  -------------------------------------------------
  FUNCTION Get_LOV_Mapping ( LN$RecNum   IN PLS_INTEGER )
  RETURN TYP_TAB_LOV_MAPPING
  IS
  BEGIN
    RETURN tabLOVs(LN$RecNum).LOV_MAPPING ;
  END ;

  -------------------------------------------
  --  Find LOV indice in the PL/SQL table  --
  -------------------------------------------
  FUNCTION Get_LOV_Indice
 (
    PC$LOV_Name  IN VARCHAR2,
    PC$LOV_Form  IN VARCHAR2
 )
  RETURN   PLS_INTEGER
  IS
  BEGIN
     IF TabLOVs.COUNT > 0 THEN
        FOR i IN 1 .. TabLOVs.COUNT LOOP
            -- Find the LOV --
            IF TabLOVs.EXISTS(i) THEN
               IF  LOWER(PC$LOV_Name) = LOWER(TabLOVs(i).LOV_NAME)
               AND LOWER(PC$LOV_Form) = LOWER(TabLOVs(i).LOV_FORM) THEN
                 RETURN  i ;
                 EXIT ;
               END IF ;
            END IF ;
        END LOOP ;
        RETURN 0 ;
     ELSE
        RETURN 0 ;
     END IF ;
  EXCEPTION
     WHEN OTHERS THEN
     RETURN 0 ;
  END Get_LOV_Indice ;


  -------------------------------
  --  set a specific property  --
  -------------------------------
  PROCEDURE Set_LOV_Property
 (
    PC$LOV_Name       IN VARCHAR2
   ,PC$LOV_Form       IN VARCHAR2
   ,PC$PropertyName   IN VARCHAR2
   ,PC$PropertyValue  IN VARCHAR2
 )
  IS
    LN$Indice   PLS_INTEGER   ;
    LC$Property VARCHAR2(100) := UPPER(PC$PropertyName);
  BEGIN
     LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
     IF LN$Indice > 0 THEN
        IF LC$Property = 'LOV_NAME' THEN TabLOVs(LN$Indice).LOV_NAME := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_BEAN_NAME' THEN TabLOVs(LN$Indice).LOV_BEAN_NAME := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_TITLE' THEN TabLOVs(LN$Indice).LOV_TITLE := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_SELECT' THEN TabLOVs(LN$Indice).LOV_SELECT := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_PROMPT' THEN TabLOVs(LN$Indice).LOV_PROMPT := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_CHECK' THEN
           IF LOWER(PC$PropertyValue) = 'true' THEN
              TabLOVs(LN$Indice).LOV_CHECK := TRUE ;
           ELSE
              TabLOVs(LN$Indice).LOV_CHECK := FALSE ;
        END IF ;
        ELSIF LC$Property = 'LOV_WIDTH' THEN TabLOVs(LN$Indice).LOV_WIDTH := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_HEIGHT' THEN TabLOVs(LN$Indice).LOV_HEIGHT := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_MAX_COL_WIDTH' THEN TabLOVs(LN$Indice).LOV_MAX_COL_WIDTH := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_COL_SEARCH' THEN TabLOVs(LN$Indice).LOV_COL_SEARCH := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_VALIDATION' THEN TabLOVs(LN$Indice).LOV_VALIDATION := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_SCHEME' THEN TabLOVs(LN$Indice).LOV_SCHEME := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_PAGING' THEN TabLOVs(LN$Indice).LOV_PAGING := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_WHERE_CLAUSE' THEN TabLOVs(LN$Indice).LOV_WHERE_CLAUSE := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_ORDER_BY' THEN TabLOVs(LN$Indice).LOV_ORDER_BY := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_BUTTON1' THEN TabLOVs(LN$Indice).LOV_BUTTON1 := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_BUTTON2' THEN TabLOVs(LN$Indice).LOV_BUTTON2 := PC$PropertyValue ;
 END IF ;
     END IF ;
  END Set_LOV_Property;


  --------------------------
  --  get a LOV property  --
  --------------------------
  FUNCTION  Get_Lov_Property
 (
    PC$LOV_Name     IN VARCHAR2
   ,PC$LOV_Form     IN VARCHAR2
   ,PC$Property     IN VARCHAR2
 )
  RETURN VARCHAR2
  IS
    LN$Indice   PLS_INTEGER   ;
    LC$Property VARCHAR2(100) := UPPER(PC$Property);
  BEGIN
     LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
     IF LN$Indice > 0 THEN
        IF LC$Property = 'LOV_NAME' THEN RETURN TabLOVs(LN$Indice).LOV_NAME  ;
        ELSIF LC$Property = 'LOV_BEAN_NAME' THEN RETURN TabLOVs(LN$Indice).LOV_BEAN_NAME  ;
        ELSIF LC$Property = 'LOV_TITLE' THEN RETURN TabLOVs(LN$Indice).LOV_TITLE  ;
        ELSIF LC$Property = 'LOV_SELECT' THEN RETURN TabLOVs(LN$Indice).LOV_SELECT  ;
        ELSIF LC$Property = 'LOV_PROMPT' THEN RETURN TabLOVs(LN$Indice).LOV_PROMPT  ;
        ELSIF LC$Property = 'LOV_CHECK' THEN
           IF TabLOVs(LN$Indice).LOV_CHECK = TRUE THEN RETURN 'TRUE' ;
           ELSE RETURN 'FALSE' ;
           END IF ;
        ELSIF LC$Property = 'LOV_WIDTH' THEN RETURN TabLOVs(LN$Indice).LOV_WIDTH  ;
        ELSIF LC$Property = 'LOV_HEIGHT' THEN RETURN TabLOVs(LN$Indice).LOV_HEIGHT  ;
        ELSIF LC$Property = 'LOV_MAX_COL_WIDTH' THEN RETURN TabLOVs(LN$Indice).LOV_MAX_COL_WIDTH  ;
        ELSIF LC$Property = 'LOV_COL_SEARCH' THEN RETURN TabLOVs(LN$Indice).LOV_COL_SEARCH  ;
        ELSIF LC$Property = 'LOV_VALIDATION' THEN RETURN TabLOVs(LN$Indice).LOV_VALIDATION  ;
        ELSIF LC$Property = 'LOV_SCHEME' THEN RETURN TabLOVs(LN$Indice).LOV_SCHEME  ;
        ELSIF LC$Property = 'LOV_PAGING' THEN RETURN TabLOVs(LN$Indice).LOV_PAGING  ;
        ELSIF LC$Property = 'LOV_WHERE_CLAUSE' THEN RETURN TabLOVs(LN$Indice).LOV_WHERE_CLAUSE  ;
        ELSIF LC$Property = 'LOV_ORDER_BY' THEN RETURN TabLOVs(LN$Indice).LOV_ORDER_BY  ;
        ELSIF LC$Property = 'LOV_NB_PAGES' THEN RETURN TabLOVs(LN$Indice).LOV_NB_PAGES  ;
        ELSIF LC$Property = 'LOV_BUTTON1' THEN RETURN TabLOVs(LN$Indice).LOV_BUTTON1  ;
        ELSIF LC$Property = 'LOV_BUTTON2' THEN RETURN TabLOVs(LN$Indice).LOV_BUTTON2  ;
 END IF ;
     END IF ;
     RETURN NULL ;
  END Get_Lov_Property ;


   -----------------------------------------------
   --  Test if record exists for LOV validation --
   -----------------------------------------------
   FUNCTION Check_Value
   (
  PC$LOV_Name  IN  VARCHAR2
 ,PC$LOV_Form  IN  VARCHAR2
 ,PC$LOV_Col   IN  VARCHAR2
 ,PC$Value     IN  VARCHAR2
 ,PC$ErrorMsg  OUT VARCHAR2
   ) RETURN PLS_INTEGER
   IS
      TYPE T_CUR IS REF CURSOR ;
   Cur         T_CUR ;
   LN$Nbre     PLS_INTEGER ;
   LC$Select   VARCHAR2(10000) ;
   LC$Order    VARCHAR2(10000) ;
   LN$Indice   PLS_INTEGER   ;
   LC$ColType  VARCHAR2(30) ;
   BEGIN

 -- find the LOV --
 LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
 IF LN$Indice > 0 THEN
  LC$Select := LOWER(tabLOVs(LN$Indice).LOV_SELECT) ;
  IF INSTR(LC$Select, 'order by' ) > 0 THEN
   LC$Select := SUBSTR( LC$Select, 1, INSTR(LC$Select, 'order by' ) - 1 ) ;
  END IF ;
  LC$Order := 'Select 1 From DUAL Where exists( Select 1 ' || SUBSTR( LC$Select, INSTR( LC$Select, 'from' ) ) ;
  IF INSTR( LC$Select, 'where' ) > 0 THEN
   LC$Order := LC$Order || ' AND ' ;
  ELSE
   LC$Order := LC$Order || ' WHERE ' ;
  END IF ;

  LC$ColType := Get_LOV_Col_Type( PC$LOV_Name, PC$LOV_Form, PC$LOV_Col) ;

  IF LC$ColType = 'NUMBER' THEN
   LC$Order := LC$Order || PC$LOV_Col || ' = ' || PC$Value  ;
  ELSE
   LC$Order := LC$Order || PC$LOV_Col || ' = ''' || PC$Value || '''' ;
  END IF ;

  LC$Order := LC$Order || ' )' ;

  OPEN  Cur FOR LC$Order ;
  FETCH Cur INTO LN$Nbre ;
  LN$Nbre := Cur%ROWCOUNT ;
  CLOSE Cur ;
  RETURN LN$Nbre ;
 ELSE
  PC$ErrorMsg := 'LAF LOV Validation LOV not found: ' || PC$LOV_Form || '.' || PC$LOV_Name ;
  RETURN -1 ;
 END IF ;
   EXCEPTION
      WHEN OTHERS THEN
         PC$ErrorMsg := 'LAF LOV Validation error: ' || SQLERRM ;
   RETURN -1 ;
   END Check_Value ;


  --------------------------------
  --  return the column's type  --
  --------------------------------
  FUNCTION Get_LOV_Col_Type
 (
    PC$LOV_Name  IN VARCHAR2
   ,PC$LOV_Form  IN VARCHAR2
   ,PC$Col_Name  IN VARCHAR2
 )
  RETURN   VARCHAR2
  IS
    LN$Indice  PLS_INTEGER ;
  BEGIN
      -- find the LOV --
      LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
      IF LN$Indice > 0 THEN
        FOR i IN 1 .. tabLOVs(LN$Indice).LOV_TYPES.COUNT() LOOP
           IF LOWER(tabLOVs(LN$Indice).LOV_TYPES(i).LOV_COLUMN) = LOWER(PC$Col_Name) THEN
              RETURN tabLOVs(LN$Indice).LOV_TYPES(i).LOV_COL_TYPE ;
           END IF ;
        END LOOP ;
      END IF ;
      RETURN '' ;
  END Get_LOV_Col_Type ;


  ------------------------------------------
  --  List of items that support the LOV  --
  ------------------------------------------
  FUNCTION   Set_Lov_Items
 (
    PC$LOV_Name    IN VARCHAR2
   ,PC$LOV_Form    IN VARCHAR2
   ,PR$Items       IN TYP_TAB_CHAR
 )
  RETURN     VARCHAR2
  IS
     LN$Indice  PLS_INTEGER ;
  BEGIN
   -- find the LOV --
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
    tabLOVs(LN$Indice).LOV_ITEMS := PR$Items ;
   END IF ;
   RETURN 'OK' ;
  EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV Adding Item List error: ' || SQLERRM ;
  END Set_Lov_Items ;



  ------------------------------------------
  --  List of items that support the LOV  --
  ------------------------------------------
  FUNCTION   Add_Lov_Item
 (
    PC$LOV_Name    IN VARCHAR2
   ,PC$LOV_Form    IN VARCHAR2
   ,PC$LOV_Item    IN VARCHAR2
 )
  RETURN     VARCHAR2
  IS
 LN$Indice  PLS_INTEGER ;
 LR$Items   TYP_TAB_CHAR ;
  BEGIN
   -- find the LOV --
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
     LR$Items := tabLOVs(LN$Indice).LOV_ITEMS ;
     LR$Items(LR$Items.COUNT + 1) := PC$LOV_Item ;
     tabLOVs(LN$Indice).LOV_ITEMS := LR$Items ;
   END IF ;
   RETURN 'OK' ;
  EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV Adding Item error: ' || SQLERRM ;
  END Add_Lov_Item ;



  -------------------------------
  --  Find LOV for given item  --
  -------------------------------
  FUNCTION Get_Lov_Item
 (
    PC$Item_Name    IN VARCHAR2
   ,PC$LOV_Form     IN VARCHAR2
   ,PC$LOV_Item     IN VARCHAR2
   ,PC$LOV_Name    OUT VARCHAR2
   ,PC$BeanName    OUT VARCHAR2
   ,PC$Column      OUT VARCHAR2
   ,PC$Validation  OUT VARCHAR2
 )
  RETURN VARCHAR2
  IS
  BEGIN
     IF tabLOVs.COUNT > 0 THEN
     FOR i IN 1 .. tabLOVs.COUNT LOOP
     IF TabLOVs.EXISTS(i) THEN
        IF TabLOVS(i).LOV_ITEMS.COUNT > 0 THEN
           FOR j IN 1 .. TabLOVS(i).LOV_ITEMS.COUNT LOOP
           IF LOWER(PC$Item_Name) = LOWER(TabLOVS(i).LOV_ITEMS(j)) THEN
           --  item found --
           PC$LOV_Name   :=  TabLOVS(i).LOV_NAME ;
           PC$BeanName   :=  TabLOVS(i).LOV_BEAN_NAME ;
           PC$Validation :=  TabLOVS(i).LOV_VALIDATION ;
           IF PC$Validation = 'Y' THEN
              FOR k IN 1 .. TabLOVS(i).LOV_ITEM_VALID.COUNT LOOP
                IF LOWER(TabLOVS(i).LOV_ITEM_VALID(k).LOV_ITEM) = LOWER(PC$Item_Name) THEN
                   PC$Column := TabLOVS(i).LOV_ITEM_VALID(k).LOV_COLUMN ;
                END IF ;
              END LOOP;
           END IF ;
           RETURN 'OK' ;
        END IF ;
        END LOOP ;
        END IF ;
     END IF ;
  END LOOP;
  END IF ;
  RETURN 'KO' ;
  EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV getting LOV item error: ' || SQLERRM ;
  END;

  ------------------------
  --  get a LOV record  --
  ------------------------
  FUNCTION   Get_Lov_Record
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
            )
  RETURN     LOV_RECORD
  Is
     LN$Indice  PLS_INTEGER ;
  BEGIN
    -- find the LOV --
    LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
    IF LN$Indice > 0 THEN
       return tabLOVs(LN$Indice) ;
    Else
       return Null ;
    END IF ;
  End Get_Lov_Record;


  PROCEDURE setSearchLabel ( PC$Label IN VARCHAR2 )
  IS
  BEGIN
    GC$SearchLabel := PC$Label ;
  END setSearchLabel ;

  PROCEDURE setSeparator ( PC$Separator IN VARCHAR2 )
  IS
  BEGIN
    GC$Sep := PC$Separator ;
  END setSeparator ;

  FUNCTION getSearchLabel
  RETURN VARCHAR2
  IS
  BEGIN
    RETURN GC$SearchLabel ;
  END  getSearchLabel ;


  FUNCTION getSeparator
  RETURN VARCHAR2
  IS
  BEGIN
    RETURN GC$Sep ;
  END  getSeparator ;


 ---------------------------------
 --   add a LOV Columns width   --
 ---------------------------------
 FUNCTION   Set_Lov_Col_Width
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
              ,PR$Widths      IN TYP_TAB_LOV_COL_WIDTH
            )
 RETURN     VARCHAR2
 IS
   LB$Return  BOOLEAN := TRUE ;
   LB$Found   BOOLEAN := FALSE ;
   LR$Width   TYP_TAB_LOV_COL_WIDTH := PR$Widths ;
   LN$Indice  PLS_INTEGER ;
 BEGIN
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      --  LOV mapping inscription --
      TabLOVs(LN$Indice).COL_WIDTH := LR$Width ;
   ELSE
      RETURN 'LAF LOV Column width: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV not found';
   END IF ;
   RETURN 'OK' ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
 END Set_Lov_Col_Width ;

  -------------------------------
  --  get a LOV columns width  --
  -------------------------------
 FUNCTION  Get_Lov_Col_Width
 (
    PC$LOV_Name  IN  VARCHAR2
   ,PC$LOV_Form  IN  VARCHAR2
 ) RETURN TYP_TAB_LOV_COL_WIDTH
  Is
     LN$Indice  PLS_INTEGER ;
     LN$T       TYP_TAB_LOV_COL_WIDTH ;
  BEGIN
    -- find the LOV --
    LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
    IF LN$Indice > 0 THEN
       LN$T := TabLOVS(LN$Indice).COL_WIDTH ;
    END IF ;
    Return LN$T ;
  End Get_Lov_Col_Width;


END Pkg_Db_Laf_Lov;
/

PROMPT CREATE OR REPLACE PACKAGE BODY pkg_db_laf_lov
CREATE OR REPLACE PACKAGE BODY pkg_db_laf_lov AS

  GC$SearchLabel   VARCHAR2(100) := 'Define search column' ;
  GC$Sep           VARCHAR2(1) := '^' ;

  GC$source_cursor INTEGER ;
  GN$Current_Page  PLS_INTEGER := 1 ;
  GC$rec_tab       DBMS_SQL.DESC_TAB;


 -----------------------
 --  Prepare the LOV  --
 -----------------------
 FUNCTION Prepare_LOV
 (
    PC$LOV_Name    IN  VARCHAR2
   ,PC$LOV_Form    IN  VARCHAR2
   ,PN$NbCols      OUT PLS_INTEGER
   ,PN$NbRows      OUT PLS_INTEGER
   ,PC$Head        OUT VARCHAR2
   ,PC$Type        OUT VARCHAR2
   ,PN$MaxWidth    OUT PLS_INTEGER
   ,PC$Prompt      OUT VARCHAR2
   ,PC$Title       OUT VARCHAR2
   ,PC$Bounds      OUT VARCHAR2
   ,PC$Query       OUT VARCHAR2
   ,PC$ColSearch   OUT VARCHAR2
   ,PC$Validation  OUT VARCHAR2
   ,PC$Scheme      OUT VARCHAR2
   ,PN$Paging      OUT PLS_INTEGER
   ,PN$NbPages     OUT PLS_INTEGER
   ,PC$Button1     OUT VARCHAR2
   ,PC$Button2     OUT VARCHAR2
 )
  RETURN VARCHAR2
  IS
   iNbcols       PLS_INTEGER := 0 ;
   iNbrows       PLS_INTEGER := 0 ;
   LC$Line       VARCHAR2(32767) ;
   LC$Head       VARCHAR2(4000) ;
   LC$Type       VARCHAR2(4000) ;
   c             NUMBER;
   d             NUMBER;
   col_cnt       PLS_INTEGER;
   v             VARCHAR2(4000) ;
   col_num       NUMBER;
   source_cursor INTEGER;
   result        INTEGER;
   LC$Qry        VARCHAR2(32000) ;
   LC$Query      VARCHAR2(32000) ;
   LC$Count      VARCHAR2(1000) ;
   LC$Filter     VARCHAR2(1000) ;
   LC$Rowid      VARCHAR2(100) ;
   LC$Form       VARCHAR2(100);
   LC$Button1    VARCHAR2(100) ;
   LC$Button2    VARCHAR2(100) ;
   LC$BeanName   VARCHAR2(128);
   LC$Image      VARCHAR2(32767) ;
   LB$Found      BOOLEAN := FALSE ;
   LN$Width      PLS_INTEGER ;
   LN$Height     PLS_INTEGER ;
   LN$X_Pos      PLS_INTEGER := 0 ;
   LN$Y_Pos      PLS_INTEGER := 0 ;
   LN$Paging     PLS_INTEGER ;
   LC$Title      VARCHAR2(256) ;
   LC$Prompt     VARCHAR2(512) ;
   LC$ColSearch  VARCHAR2(100) ;
   LC$Validation VARCHAR2(1) ;
   LC$Scheme     VARCHAR2(100) ;
   LN$MaxWidth   PLS_INTEGER ;
   LN$Indice     PLS_INTEGER ;
   LN$MaxPages   PLS_INTEGER := 1 ;
   LR$Types      TYP_TAB_LOV_COL_TYPE ;
   LI$Start      PLS_INTEGER := 1;
   LI$End        PLS_INTEGER := -1;
   LI$Count1     PLS_INTEGER ;
   LI$Count2     PLS_INTEGER ;
   LC$Op         VARCHAR2(10) := ' WHERE ';

 BEGIN

   --  find the LOV --
   IF TabLOVs.COUNT = 0 THEN
       RETURN 'No LOV defined';
   END IF ;
   FOR i IN 1 .. TabLOVs.COUNT LOOP
       IF TabLOVs.EXISTS(i) THEN
           IF  LOWER(PC$LOV_Name) = LOWER(TabLOVs(i).LOV_NAME)
           AND LOWER(PC$LOV_Form) = LOWER(TabLOVs(i).LOV_FORM)THEN
             LC$Qry         := TabLOVs(i).LOV_SELECT ;
             LC$BeanName    := TabLOVs(i).LOV_BEAN_NAME ;
             LN$Width       := TabLOVs(i).LOV_WIDTH ;
             LN$Height      := TabLOVs(i).LOV_HEIGHT ;
             LN$X_Pos       := TabLOVs(i).LOV_X_POSITION ;
             LN$Y_Pos       := TabLOVs(i).LOV_Y_POSITION ;
             LC$Title       := TabLOVs(i).LOV_TITLE ;
             LC$Prompt      := TabLOVs(i).LOV_PROMPT ;
             LC$ColSearch   := TabLOVs(i).LOV_COL_SEARCH ;
             LC$Validation  := TabLOVs(i).LOV_VALIDATION ;
             LC$Scheme      := TabLOVs(i).LOV_SCHEME ;
             LN$Paging      := TabLOVs(i).LOV_PAGING ;
             LC$Button1     := TabLOVs(i).LOV_BUTTON1 ;
             LC$Button2     := TabLOVs(i).LOV_BUTTON2 ;
             LC$Filter      := TabLOVs(i).LOV_FILTER ;
             LN$MaxWidth    := TabLOVs(i).LOV_MAX_COL_WIDTH ;
             LN$MaxPages    := TabLOVs(i).LOV_NB_PAGES ;
             LB$Found       := TRUE ;
             LN$Indice := i ;
             EXIT ;
           END IF ;
       END IF ;
      END LOOP ;
      IF NOT LB$Found THEN
         RETURN 'LAF LOV : '|| PC$LOV_Form || '.' || PC$LOV_Name || ' not found';
      END IF ;

      GC$rec_tab.DELETE;

     LC$Query := LC$Qry ;

     IF LC$Filter IS NOT NULL THEN
        IF SubStr( LC$Filter, Length(LC$Filter), 1) <> '%' THEN
           LC$Filter := LC$Filter || '%' ;
        END IF;

        If Instr( Lower( LC$Qry ), 'where' ) > 0 Then
           LC$Op := ' AND ' ;
        End if ;
        LI$Count1 := Instr( Lower( LC$Qry ), 'group by' ) ;
        If LI$Count1 > 0 Then
           LC$Qry := Substr( LC$Qry, 1, LI$Count1 - 1)
                       || LC$Op || LC$ColSearch || ' LIKE ''' || LC$Filter || ''' '
                       || Substr( LC$Qry, LI$Count1 ) ;
        End if ;
        LI$Count2 := Instr( Lower( LC$Qry ), 'order by' ) ;
        If LI$Count2 > 0 Then
           LC$Qry := Substr( LC$Qry, 1, LI$Count2 - 1)
                       || LC$Op || LC$ColSearch || ' LIKE ''' || LC$Filter || ''' '
                       || Substr( LC$Qry, LI$Count2 ) ;
        End if ;
        IF LI$Count1 + LI$Count2 = 0 THEN
           LC$Qry := LC$Qry || LC$Op || LC$ColSearch || ' LIKE ''' || LC$Filter || ''' ' ;
        END IF ;
     END IF;


     IF LN$Paging > -1 THEN
       LI$Start :=  1 ;
       LI$End   := LN$Paging ;
     Else
       LI$End   := 9999999 ;
     END IF ;
     LC$Query := 'SeLeCt * FrOm ( select subreq.*, rownum r from ( '
              || LC$Qry
              || ' ) subreq where rownum <= ' || LI$End || ') where r >= ' || LI$Start ;

     --LC$Count := LOWER(Replace( LC$Query, 'SeLeCt * FrOm', 'select count(*) from'));
     LC$Count := Replace( LC$Query, 'SeLeCt * FrOm', 'select count(*) from');
     -- Count the rows --
     source_cursor := DBMS_SQL.OPEN_CURSOR;
     DBMS_SQL.PARSE(source_cursor,  LC$Count, 1);
     DBMS_SQL.DEFINE_COLUMN(source_cursor, 1, v,4000);
     result := DBMS_SQL.EXECUTE(source_cursor);
     IF DBMS_SQL.FETCH_ROWS(source_cursor)>0 THEN
          DBMS_SQL.COLUMN_VALUE(source_cursor, 1, v);
          iNbrows := v ;
     END IF;
     DBMS_SQL.CLOSE_CURSOR(source_cursor);

     -- count the number of pages --
     IF TabLOVs(LN$Indice).LOV_PAGING > -1 THEN
       LN$MaxPages := TRUNC(iNbrows / TabLOVs(LN$Indice).LOV_PAGING, 0) ;
       IF LN$MaxPages * TabLOVs(LN$Indice).LOV_PAGING < iNbrows THEN
          LN$MaxPages := LN$MaxPages + 1 ;
       END IF ;
       TabLOVs(LN$Indice).LOV_NB_PAGES := LN$MaxPages ;
  ELSE
       TabLOVs(LN$Indice).LOV_NB_PAGES := 1 ;
     END IF ;

     -- retrieve the columns of the query --
     c := DBMS_SQL.OPEN_CURSOR;

     DBMS_SQL.PARSE(c, LC$Query , 1);

     d := DBMS_SQL.EXECUTE(c);

     DBMS_SQL.DESCRIBE_COLUMNS(c, col_cnt, GC$rec_tab);

     iNbcols := 0 ;

     Pkg_Db_Laf_Lov.TabCols.DELETE ;

     d := 1 ;

     FOR i IN GC$rec_tab.FIRST .. GC$rec_tab.LAST-1 LOOP
       -- BLOB -> 113
       IF GC$rec_tab(i).col_type IN (1,2,11,12,96,113) THEN
         IF d > 1 THEN
            LC$Type := LC$Type || GC$Sep ;
         ELSE
            If LC$ColSearch Is Null Then
               LC$ColSearch := GC$rec_tab(i).col_name ;
               Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH', LC$ColSearch);
               If GC$rec_tab(i).col_type in (2) Then
                  If GC$rec_tab(i).col_scale =0 Then
                    Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH_TYPE', 'INTEGER');
                  ELSE
                    Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH_TYPE', 'NUMBER');
                  END IF ;
               ElsIf GC$rec_tab(i).col_type in (12) Then
                  Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH_TYPE', 'DATE');
               Else
                  Set_LOV_Property(PC$lov_name, PC$lov_form, 'LOV_COL_SEARCH_TYPE', 'CHAR');
               End if ;
            End if ;
         END IF ;

         -- column type --
         LR$Types(i).LOV_COLUMN := GC$rec_tab(i).col_name ;
         IF GC$rec_tab(i).col_type = 2 THEN
           If GC$rec_tab(i).col_scale =0 Then
             LR$Types(i).LOV_COL_TYPE := 'INTEGER' ;
           ELSE
             LR$Types(i).LOV_COL_TYPE := 'NUMBER' ;
           END IF ;
         ELSIF GC$rec_tab(i).col_type = 12 THEN
           LR$Types(i).LOV_COL_TYPE := 'DATE' ;
         ELSIF GC$rec_tab(i).col_type = 113 THEN
           LR$Types(i).LOV_COL_TYPE := 'IMAGE' ;
         ELSE
           LR$Types(i).LOV_COL_TYPE := 'CHAR' ;
         END IF ;

         LC$Type := LC$Type || LR$Types(i).LOV_COL_TYPE; --'CHAR' ;
         IF d > 1 THEN LC$Head := LC$Head || GC$Sep ; END IF ;
         LC$Head := LC$Head || INITCAP(GC$rec_tab(i).col_name) ;
         Pkg_Db_Laf_Lov.TabCols(d) := GC$rec_tab(i).col_name ;
         d := d + 1 ;
         iNbcols := iNbcols + 1 ;
       END IF ;
     END LOOP ;


     DBMS_SQL.CLOSE_CURSOR(c);

     TabLOVs(LN$Indice).LOV_TYPES := LR$Types ;

     -- Init all variables --
     PC$Query       := LC$Qry ;
     PN$NbCols      := iNbcols ;
     PN$NbRows      := iNbrows ;
     PC$Head        := LC$Head ;
     PC$Type        := LC$Type ;
     PN$MaxWidth    := LN$MaxWidth ;
     PC$Prompt      := LC$Prompt ;
     PC$Title       := LC$Title ;
     PC$ColSearch   := LC$ColSearch ;
     PC$Validation  := LC$Validation ;
     PC$Scheme      := LC$Scheme ;
     PN$Paging      := LN$Paging ;
     PC$Button1     := LC$Button1 ;
     PC$Button2     := LC$Button2 ;
     PN$NBPages     := LN$MaxPages ;
     PC$Bounds      := Ltrim(To_Char(LN$X_Pos)) || ',' || Ltrim(To_Char(LN$Y_Pos)) || ',' || LTRIM(TO_CHAR(LN$Width)) || ',' || LTRIM(TO_CHAR(LN$Height)) ;

     RETURN 'OK' ;

  EXCEPTION
    WHEN OTHERS THEN
      RETURN 'LAF LOV error: ' || SQLERRM ;

  END Prepare_LOV;

 -----------------------
 --   Open a cursor   --
 -----------------------
 FUNCTION Open_Cursor
 (
     PC$Query      IN  VARCHAR2
    ,PN$Paging     IN PLS_INTEGER DEFAULT -1
    ,PN$Page       IN PLS_INTEGER DEFAULT 1
    ,PC$Search     IN VARCHAR2  DEFAULT NULL
 )
  RETURN PLS_INTEGER
  IS
   v             VARCHAR2(4000) ;
   col_num       NUMBER;
   LC$Query      VARCHAR2(32000) ;
   LI$Start      PLS_INTEGER := 1 ;
   LI$End        PLS_INTEGER := 999999999;
   LI$Count1     PLS_INTEGER := 0 ;
   LI$Count2     PLS_INTEGER := 0 ;
   LC$Op         VARCHAR2(10) := ' WHERE ' ;
   result        INTEGER;
 BEGIN
     -----------------------
     --  Open the cursor  --
     -----------------------
     --f_trace('Open_Cursor() search:' || PC$Search,'T');
     LC$Query := PC$Query ;

     IF PN$Paging > -1 THEN
       LI$Start := (PN$Paging * (PN$Page - 1)) + 1 ;
       LI$End   := PN$Paging * PN$Page ;
     END IF ;
     -- Where clause filter to add ? --
     If PC$Search Is Not Null Then
        If Instr( Lower( LC$Query ), 'where' ) > 0 Then
           LC$Op := ' AND ' ;
        End if ;
        LI$Count1 := Instr( Lower( LC$Query ), 'group by' ) ;
        If LI$Count1 > 0 Then
           LC$Query := Substr( LC$Query, 1, LI$Count1 - 1)
                       || LC$Op || PC$Search
                       || Substr( LC$Query, LI$Count1 ) ;
        End if ;
        LI$Count2 := Instr( Lower( LC$Query ), 'order by' ) ;
        If LI$Count2 > 0 Then
           LC$Query := Substr( LC$Query, 1, LI$Count2 - 1)
                       || LC$Op || PC$Search
                       || Substr( LC$Query, LI$Count2 ) ;
        End if ;
        IF LI$Count1 + LI$Count2 = 0 THEN
           LC$Query := LC$Query || LC$Op || PC$Search ;
        END IF ;
     End if ;

     LC$Query := 'SELECT * from ( select subreq.*, rownum r from ( '
              || LC$Query
              || ' ) subreq where rownum <= ' || LI$End || ') where r >= ' || LI$Start ;
     --f_trace('PKG_DB_LAF_LOV.Open_Cursor() :' || LC$Query,'T');
     GC$source_cursor := DBMS_SQL.OPEN_CURSOR;
     DBMS_SQL.PARSE(GC$source_cursor,  LC$Query, 1);
     -- Define the columns --
     FOR i IN 1 .. GC$rec_tab.LAST LOOP
       IF GC$rec_tab(i).col_type IN (1,2,11,12,96) THEN
         DBMS_SQL.DEFINE_COLUMN(GC$source_cursor, i, v,4000);
       END IF ;
     END LOOP ;

     result := DBMS_SQL.EXECUTE(GC$source_cursor);

     LI$Count1 := 0 ;
     Loop
       If DBMS_SQL.FETCH_ROWS(GC$source_cursor)>0 THEN
         LI$Count1 := LI$Count1 + 1 ;
       else
         exit ;
       End if ;
     End loop ;

     result := DBMS_SQL.EXECUTE(GC$source_cursor);


      RETURN LI$Count1 ;

  EXCEPTION
    WHEN OTHERS THEN
      RETURN -1 ;
  END Open_Cursor ;


  ------------------------------------
  --  Fetch a line from the cursor  --
  ------------------------------------
  FUNCTION Fetch_Cursor
  RETURN VARCHAR2
  IS
   d             NUMBER;
   v             VARCHAR2(4000) ;
   result        INTEGER;
   LC$Rowid      VARCHAR2(100) ;
   LC$Line       VARCHAR2(32767) ;
  BEGIN
       IF DBMS_SQL.FETCH_ROWS(GC$source_cursor)>0 THEN
         ----------------------------------
         -- get column values of the row --
         ----------------------------------
         LC$Line := '' ;
         d := 1 ;
         FOR i IN 1.. GC$rec_tab.LAST-1 LOOP
           IF GC$rec_tab(i).col_type IN (1,2,11,12,96,113) THEN
             IF GC$rec_tab(i).col_type IN (1,2,11,12,96) THEN
               DBMS_SQL.COLUMN_VALUE(GC$source_cursor, i, v);
             ELSE
              v := NULL ;
             END IF ;
             IF d > 1 THEN LC$Line := LC$Line || GC$Sep ; END IF ;
             IF GC$rec_tab(i).col_type = 11 THEN
               LC$Rowid := v ;
             END IF ;
              LC$Line := LC$Line || NVL(v,' ') ;
           END IF ;
           /*
           If GC$rec_tab(i).col_type = 113 Then
              --------------------
              -- send the image --
              --------------------
              LC$Query := 'Select ' || GC$rec_tab(i).col_name || ' From ' || PC$TableName
                || ' Where ROWID=''' || LC$Rowid || '''';
              If Pkg_Read_Blob_Image.Select_Blob(LC$Query) Then
              Set_Custom_Property( 'BL1.JTABLE', 1, 'SET_IMAGE', '[INDEX_IMAGE],'|| iNbRows || ',' || d) ;
              Loop
                 LC$Image := Pkg_Read_Blob_Image.Get_B64_Chunk ;
                 If LC$Image Is Not Null Then
                   Set_Custom_Property( 'BL1.JTABLE', 1, 'SET_IMAGE', LC$Image ) ;
                 Else
                   Set_Custom_Property( 'BL1.JTABLE', 1, 'SET_IMAGE', '[END_IMAGE]' ) ;
                   Exit ;
                 End if ;
              End loop ;
              End if ;
           End if ;
           */
           d := d + 1 ;
         END LOOP ;

         -- return data --
          RETURN LC$Line ;

       ELSE
         -- No more rows --
         RETURN NULL ;
       END IF;

  EXCEPTION
    WHEN OTHERS THEN
      RETURN NULL ;
  END Fetch_Cursor ;


  -------------------------------
  --  close the opened cursor  --
  -------------------------------
  PROCEDURE Close_Cursor
  IS
  BEGIN
    DBMS_SQL.CLOSE_CURSOR(GC$source_cursor);
  END Close_Cursor ;

 ---------------------------------
 --  add a new LOV description  --
 ---------------------------------
 FUNCTION   Add_Lov
 (
    PC$BeanName   IN VARCHAR2
   ,PC$LOV_Name   IN VARCHAR2
   ,PC$LOV_Form   IN VARCHAR2
   ,PR$Attributes IN LOV_RECORD
   ,PC$Replace    IN VARCHAR2 DEFAULT 'R'
 )
 RETURN     VARCHAR2
 IS
   LN$TotRow   PLS_INTEGER := TabLOVs.COUNT() ;
   LB$Return   BOOLEAN := TRUE ;
   LB$Found    BOOLEAN := FALSE ;
 BEGIN
   IF PR$Attributes.LOV_NAME IS NULL THEN
      RETURN 'LAF LOV description: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV Name must be defined' ;
   END IF ;
   IF PR$Attributes.LOV_BEAN_NAME IS NULL THEN
      RETURN 'LAF LOV description: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV Bean Area Name must be defined' ;
   END IF ;
   IF LN$TotRow > 0 THEN
      FOR i IN 1 .. TabLOVs.COUNT LOOP
        -- LOV already exists ? --
        IF TabLOVs.EXISTS(i) THEN
           IF  LOWER(PC$LOV_Name) = LOWER(TabLOVs(i).LOV_NAME)
           AND LOWER(PC$LOV_Form) = LOWER(TabLOVs(i).LOV_Form)THEN
              IF UPPER(PC$Replace) = 'C' THEN -- cancel
                   RETURN 'LAF LOV description: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV already defined' ;
              ELSIF UPPER(PC$Replace) = 'R' THEN -- replace
                --  LOV inscription --
                 TabLOVs(LN$TotRow + 1) := PR$Attributes ;
                 LB$Found := TRUE ;
                 EXIT ;
              ELSE -- Ignore creation (keep former definition)
                     RETURN 'OK' ;
                  END IF ;
           END IF ;
        END IF ;
      END LOOP ;
      IF NOT LB$Found THEN
        --  LOV inscription --
        TabLOVs(LN$TotRow + 1) := PR$Attributes ;
      END IF ;
   ELSE
      --  LOV inscription --
      TabLOVs(LN$TotRow + 1) := PR$Attributes ;
   END IF ;
   RETURN 'OK' ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
  END Add_Lov ;


  FUNCTION   Add_Lov
 (
    LOV_BEAN_NAME      IN VARCHAR2
   ,LOV_NAME           IN VARCHAR2
   ,LOV_FORM           IN VARCHAR2
   ,LOV_TITLE          IN VARCHAR2
   ,LOV_SELECT         IN VARCHAR2
   ,LOV_PROMPT         IN VARCHAR2    DEFAULT NULL
   ,LOV_CHECK          IN BOOLEAN     DEFAULT FALSE
   ,LOV_WIDTH          IN PLS_INTEGER DEFAULT 400
   ,LOV_HEIGHT         IN PLS_INTEGER DEFAULT 400
   ,LOV_X_POSITION     IN PLS_INTEGER DEFAULT 0
   ,LOV_Y_POSITION     IN PLS_INTEGER DEFAULT 0
   ,LOV_MAX_COL_WIDTH  IN PLS_INTEGER DEFAULT -1
   ,LOV_COL_SEARCH     IN VARCHAR2    DEFAULT NULL
   ,LOV_VALIDATION     IN VARCHAR2    DEFAULT 'N'
   ,LOV_SCHEME         IN VARCHAR2    DEFAULT NULL
   ,LOV_PAGING         IN PLS_INTEGER DEFAULT -1
   ,LOV_BUTTON1        IN VARCHAR2    DEFAULT NULL
   ,LOV_BUTTON2        IN VARCHAR2    DEFAULT NULL
   ,REPLACE            IN VARCHAR2    DEFAULT 'R'
 )
  RETURN     VARCHAR2
  IS
     LR$RecLOV   LOV_RECORD ;
  BEGIN
     LR$RecLOV.LOV_NAME           := LOV_NAME ;
     LR$RecLOV.LOV_FORM           := LOV_FORM ;
     LR$RecLOV.LOV_BEAN_NAME      := LOV_BEAN_NAME ;
     LR$RecLOV.LOV_TITLE          := LOV_TITLE ;
     LR$RecLOV.LOV_SELECT         := LOV_SELECT ;
     LR$RecLOV.LOV_PROMPT         := LOV_PROMPT ;
     LR$RecLOV.LOV_CHECK          := LOV_CHECK ;
     LR$RecLOV.LOV_WIDTH          := LOV_WIDTH ;
     LR$RecLOV.LOV_X_POSITION     := LOV_X_POSITION ;
     LR$RecLOV.LOV_Y_POSITION     := LOV_Y_POSITION ;
     LR$RecLOV.LOV_HEIGHT         := LOV_HEIGHT ;
     LR$RecLOV.LOV_MAX_COL_WIDTH  := LOV_MAX_COL_WIDTH ;
     LR$RecLOV.LOV_COL_SEARCH     := LOV_COL_SEARCH ;
     LR$RecLOV.LOV_VALIDATION     := LOV_VALIDATION ;
     LR$RecLOV.LOV_SCHEME         := LOV_SCHEME ;
     LR$RecLOV.LOV_PAGING         := LOV_PAGING ;
     LR$RecLOV.LOV_BUTTON1        := LOV_BUTTON1 ;
     LR$RecLOV.LOV_BUTTON2        := LOV_BUTTON2 ;
     RETURN
     Add_Lov
     (
       PC$BeanName    => LOV_BEAN_NAME
      ,PC$LOV_Name    => LOV_NAME
      ,PC$LOV_Form    => LOV_FORM
      ,PR$Attributes  => LR$RecLOV
      ,PC$Replace     => REPLACE
     ) ;
  EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
  END Add_Lov ;


 ----------------------------------
 --  build the LOV Where clause  --
 ----------------------------------
 PROCEDURE  Build_LOV_Where_Clause
           (
                PC$LOV_Name  IN VARCHAR2
               ,PC$LOV_Form  IN VARCHAR2
               ,PC$Value     IN VARCHAR2
     )
 IS
   LN$Indice  PLS_INTEGER ;
   LC$ColSearch   Varchar2(100 ) ;
   LC$ColType     Varchar2(10) ;
   LC$Clause      Varchar2(1000);
 Begin

   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
     If TabLOVs(LN$Indice).LOV_COL_SEARCH Is Null Then Return ; End if ;
     If PC$Value Is Null Then
        TabLOVs(LN$Indice).LOV_WHERE_CLAUSE := null ;
        Return ;
     End if ;
     LC$ColSearch := TabLOVs(LN$Indice).LOV_COL_SEARCH ;
     LC$ColType   := TabLOVs(LN$Indice).LOV_COL_SEARCH_TYPE ;
     If LC$ColType = 'NUMBER' Then
        LC$Clause := LC$ColSearch || ' LIKE ' || PC$Value || ' ' ;
     Else
        LC$Clause := LC$ColSearch || ' LIKE ''' || PC$Value || ''' ' ;
     End if ;
     TabLOVs(LN$Indice).LOV_WHERE_CLAUSE := LC$Clause ;

   End if ;
 End  Build_LOV_Where_Clause ;


  ----------------------------
  --  set the LOV Order By  --
  ----------------------------
 PROCEDURE  Set_LOV_OrderBy
     (
        PC$LOV_Name  IN VARCHAR2
       ,PC$LOV_Form  IN VARCHAR2
       ,PC$Value     IN VARCHAR2
     )
 Is
   LN$Indice  PLS_INTEGER ;
   LN$I       PLS_INTEGER ;
   LC$Query   Varchar(32000);
 Begin
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      TabLOVs(LN$Indice).LOV_ORDER_BY := PC$Value ;
      -- previous order by in query to replace ?
      LC$Query := Lower(TabLOVs(LN$Indice).LOV_SELECT);
      LN$I := Instr( LC$Query, 'order by' ) ;
      If LN$I > 0 Then
         LC$Query := Substr( TabLOVs(LN$Indice).LOV_SELECT, 1, LN$I-1 )
                     || ' ORDER BY ' || PC$Value ;
      Else
         LC$Query := TabLOVs(LN$Indice).LOV_SELECT || ' ORDER BY ' || PC$Value ;
      End if ;
      TabLOVs(LN$Indice).LOV_SELECT := LC$Query ;
      --f_trace('Set_LOV_OrderBy():' || TabLOVs(LN$Indice).LOV_SELECT ,'T');
   End if ;
 End  Set_LOV_OrderBy;


 -------------------------------------
 --  add a LOV mapping description  --
 -------------------------------------
 FUNCTION   Set_Lov_Mapping
 (
    PC$LOV_Name    IN VARCHAR2
   ,PC$LOV_Form    IN VARCHAR2
   ,PR$Mappings    IN TYP_TAB_LOV_MAPPING
 )
 RETURN     VARCHAR2
 IS
   LB$Return  BOOLEAN := TRUE ;
   LB$Found   BOOLEAN := FALSE ;
   LR$Mapp    TYP_TAB_LOV_MAPPING := PR$Mappings ;
   LN$Indice  PLS_INTEGER ;
 BEGIN
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      FOR j IN 1 .. PR$Mappings.COUNT LOOP
          LR$Mapp(j).LOV_COLUMN := '<' || LR$Mapp(j).LOV_COLUMN || '>' ;
      END LOOP;
      --  LOV mapping inscription --
      TabLOVs(LN$Indice).LOV_MAPPING := LR$Mapp ;
   ELSE
      RETURN 'LAF LOV Mapping: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV not found';
   END IF ;
   RETURN 'OK' ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
 END Set_Lov_Mapping ;



 ----------------------------------------
 --  add a LOV validation description  --
 ----------------------------------------
 FUNCTION   Set_Lov_Validations
 (
    PC$LOV_Name    IN VARCHAR2
   ,PC$LOV_Form    IN VARCHAR2
   ,PR$Valids      IN TYP_TAB_LOV_COL_ITEM_VAL
 )
 RETURN     VARCHAR2
 IS
   LB$Return  BOOLEAN := TRUE ;
   LB$Found   BOOLEAN := FALSE ;
   LN$Indice  PLS_INTEGER ;
 BEGIN
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      --  LOV validations inscription --
      TabLOVs(LN$Indice).LOV_ITEM_VALID := PR$Valids;
   ELSE
      RETURN 'LAF LOV Validations: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV not found';
   END IF ;
   RETURN 'OK' ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
 END Set_Lov_Validations ;



 --------------------------------
 --  remove a LOV description  --
 --------------------------------
 PROCEDURE  Remove_Lov ( PC$LOV_Name  IN VARCHAR2, PC$LOV_Form IN  VARCHAR2)
 IS
   LN$Indice   PLS_INTEGER ;
   tb_sav      TYP_TAB_LOV_RECORD ;
   j           PLS_INTEGER := 1 ;
 BEGIN
   IF UPPER(PC$LOV_Name) = 'ALL_LOVS' THEN
      IF tabLOVs.COUNT > 0 THEN
        FOR i IN tabLOVs.FIRST .. tabLOVs.LAST LOOP
           IF tabLOVs.EXISTS(i) THEN
             IF UPPER(tabLOVs(i).LOV_FORM) = UPPER(PC$LOV_Form) THEN
                tabLOVs.DELETE(i);
             END IF ;
           END IF ;
        END LOOP;
      END IF ;
      IF tabLOVs.COUNT > 0 THEN
        FOR i IN tabLOVs.FIRST .. tabLOVs.LAST LOOP
           IF tabLOVs.EXISTS(i) THEN
             tb_sav(j) := tabLOVs(i) ;
             j := j + 1 ;
           END IF ;
        END LOOP;
        tabLOVs := tb_sav ;
      END IF ;
   ELSE
     LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
     IF LN$Indice > 0 THEN
        tabLOVs.DELETE(LN$Indice) ;
     END IF ;
   END IF ;
 END Remove_Lov ;


 --------------------------
 --  clear LOV mappings  --
 --------------------------
 PROCEDURE  Clear_Mappings( PC$LOV_Name  IN VARCHAR2, PC$LOV_Form IN VARCHAR2)
 IS
   LN$Indice   PLS_INTEGER ;
 BEGIN
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      tabLOVs(LN$Indice).LOV_MAPPING.DELETE ;
   END IF ;
 END Clear_Mappings ;


 -- add a LOV mapping description --
 FUNCTION   Add_Lov_Mapping_Item
 (
    LOV_Name           IN VARCHAR2
   ,LOV_Form           IN VARCHAR2
   ,LOV_COLUMN         IN VARCHAR2
   ,LOV_COL_MIN_WIDTH  IN PLS_INTEGER  DEFAULT NULL
   ,LOV_COL_MAX_WIDTH  IN PLS_INTEGER  DEFAULT NULL
   ,LOV_ITEM1          IN VARCHAR2
   ,LOV_ITEM2          IN VARCHAR2     DEFAULT NULL
   ,LOV_ITEM3          IN VARCHAR2     DEFAULT NULL
 )
 RETURN     VARCHAR2
 IS
   LN$Indice   PLS_INTEGER ;
   LR$Mapping  LOV_MAPPING ;
   LN$Pos      PLS_INTEGER ;
 BEGIN
     LN$Indice := Get_LOV_Indice( LOV_Name, LOV_Form ) ;
     IF LN$Indice > 0 THEN

 LN$Pos   := TabLOVS(LN$Indice).LOV_MAPPING.COUNT ;

 LR$Mapping.LOV_COLUMN         := '<' || LOV_COLUMN || '>';
        LR$Mapping.LOV_COL_MIN_WIDTH  := LOV_COL_MIN_WIDTH ;
        LR$Mapping.LOV_COL_MAX_WIDTH  := LOV_COL_MAX_WIDTH ;
        LR$Mapping.LOV_ITEM1          := LOV_ITEM1 ;
        LR$Mapping.LOV_ITEM2          := LOV_ITEM2 ;
        LR$Mapping.LOV_ITEM3          := LOV_ITEM3 ;

 TabLOVS(LN$Indice).LOV_MAPPING(LN$Pos + 1) := LR$Mapping ;

 RETURN ('OK') ;
     END IF ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV Add  Mapping error: ' || SQLERRM ;
 END  Add_Lov_Mapping_Item ;


 -- get a copy of tab LOVs --
  FUNCTION Get_Table_LOVS
  RETURN TYP_TAB_LOV_RECORD
  IS
  BEGIN
    RETURN TabLOVs ;
  END   Get_Table_LOVS ;



 --------------------------
  --  get a string token  --
  --------------------------
 FUNCTION SPLIT
 (
    PC$Chaine IN VARCHAR2,         -- input string
    PN$Pos IN PLS_INTEGER,         -- token number
    PC$Sep IN VARCHAR2 DEFAULT ',' -- separator character
 )
 RETURN VARCHAR2
 IS
   LC$Chaine VARCHAR2(32767) := PC$Sep || PC$Chaine ;
   LI$I      PLS_INTEGER ;
   LI$I2     PLS_INTEGER ;
 BEGIN
   LI$I := INSTR( LC$Chaine, PC$Sep, 1, PN$Pos ) ;
   IF LI$I > 0 THEN
     LI$I2 := INSTR( LC$Chaine, PC$Sep, 1, PN$Pos + 1) ;
     IF LI$I2 = 0 THEN LI$I2 := LENGTH( LC$Chaine ) + 1 ; END IF ;
     RETURN( SUBSTR( LC$Chaine, LI$I+1, LI$I2 - LI$I-1 ) ) ;
   ELSE
     RETURN NULL ;
   END IF ;
 END SPLIT;


  -------------------------------------------------
  --  get a LOV_MAPPING record from a given LOV  --
  -------------------------------------------------
  FUNCTION Get_LOV_Mapping ( LN$RecNum   IN PLS_INTEGER )
  RETURN TYP_TAB_LOV_MAPPING
  IS
  BEGIN
    RETURN tabLOVs(LN$RecNum).LOV_MAPPING ;
  END ;

  -------------------------------------------
  --  Find LOV indice in the PL/SQL table  --
  -------------------------------------------
  FUNCTION Get_LOV_Indice
 (
    PC$LOV_Name  IN VARCHAR2,
    PC$LOV_Form  IN VARCHAR2
 )
  RETURN   PLS_INTEGER
  IS
  BEGIN
     IF TabLOVs.COUNT > 0 THEN
        FOR i IN 1 .. TabLOVs.COUNT LOOP
            -- Find the LOV --
            IF TabLOVs.EXISTS(i) THEN
               IF  LOWER(PC$LOV_Name) = LOWER(TabLOVs(i).LOV_NAME)
               AND LOWER(PC$LOV_Form) = LOWER(TabLOVs(i).LOV_FORM) THEN
                 RETURN  i ;
                 EXIT ;
               END IF ;
            END IF ;
        END LOOP ;
        RETURN 0 ;
     ELSE
        RETURN 0 ;
     END IF ;
  EXCEPTION
     WHEN OTHERS THEN
     RETURN 0 ;
  END Get_LOV_Indice ;


  -------------------------------
  --  set a specific property  --
  -------------------------------
  PROCEDURE Set_LOV_Property
 (
    PC$LOV_Name       IN VARCHAR2
   ,PC$LOV_Form       IN VARCHAR2
   ,PC$PropertyName   IN VARCHAR2
   ,PC$PropertyValue  IN VARCHAR2
 )
  IS
    LN$Indice   PLS_INTEGER   ;
    LC$Property VARCHAR2(100) := UPPER(PC$PropertyName);
  BEGIN
     LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
     IF LN$Indice > 0 THEN
        IF LC$Property = 'LOV_NAME' THEN TabLOVs(LN$Indice).LOV_NAME := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_BEAN_NAME' THEN TabLOVs(LN$Indice).LOV_BEAN_NAME := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_TITLE' THEN TabLOVs(LN$Indice).LOV_TITLE := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_SELECT' THEN TabLOVs(LN$Indice).LOV_SELECT := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_PROMPT' THEN TabLOVs(LN$Indice).LOV_PROMPT := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_CHECK' THEN
           IF LOWER(PC$PropertyValue) = 'true' THEN
              TabLOVs(LN$Indice).LOV_CHECK := TRUE ;
           ELSE
              TabLOVs(LN$Indice).LOV_CHECK := FALSE ;
        END IF ;
        ELSIF LC$Property = 'LOV_WIDTH' THEN TabLOVs(LN$Indice).LOV_WIDTH := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_HEIGHT' THEN TabLOVs(LN$Indice).LOV_HEIGHT := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_MAX_COL_WIDTH' THEN TabLOVs(LN$Indice).LOV_MAX_COL_WIDTH := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_COL_SEARCH' THEN TabLOVs(LN$Indice).LOV_COL_SEARCH := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_VALIDATION' THEN TabLOVs(LN$Indice).LOV_VALIDATION := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_SCHEME' THEN TabLOVs(LN$Indice).LOV_SCHEME := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_PAGING' THEN TabLOVs(LN$Indice).LOV_PAGING := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_WHERE_CLAUSE' THEN TabLOVs(LN$Indice).LOV_WHERE_CLAUSE := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_ORDER_BY' THEN TabLOVs(LN$Indice).LOV_ORDER_BY := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_BUTTON1' THEN TabLOVs(LN$Indice).LOV_BUTTON1 := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_BUTTON2' THEN TabLOVs(LN$Indice).LOV_BUTTON2 := PC$PropertyValue ;
        ELSIF LC$Property = 'LOV_FILTER' THEN TabLOVs(LN$Indice).LOV_FILTER := PC$PropertyValue ;
 END IF ;
     END IF ;
  END Set_LOV_Property;


  --------------------------
  --  get a LOV property  --
  --------------------------
  FUNCTION  Get_Lov_Property
 (
    PC$LOV_Name     IN VARCHAR2
   ,PC$LOV_Form     IN VARCHAR2
   ,PC$Property     IN VARCHAR2
 )
  RETURN VARCHAR2
  IS
    LN$Indice   PLS_INTEGER   ;
    LC$Property VARCHAR2(100) := UPPER(PC$Property);
  BEGIN
     LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
     IF LN$Indice > 0 THEN
        IF LC$Property = 'LOV_NAME' THEN RETURN TabLOVs(LN$Indice).LOV_NAME  ;
        ELSIF LC$Property = 'LOV_BEAN_NAME' THEN RETURN TabLOVs(LN$Indice).LOV_BEAN_NAME  ;
        ELSIF LC$Property = 'LOV_TITLE' THEN RETURN TabLOVs(LN$Indice).LOV_TITLE  ;
        ELSIF LC$Property = 'LOV_SELECT' THEN RETURN TabLOVs(LN$Indice).LOV_SELECT  ;
        ELSIF LC$Property = 'LOV_PROMPT' THEN RETURN TabLOVs(LN$Indice).LOV_PROMPT  ;
        ELSIF LC$Property = 'LOV_CHECK' THEN
           IF TabLOVs(LN$Indice).LOV_CHECK = TRUE THEN RETURN 'TRUE' ;
           ELSE RETURN 'FALSE' ;
           END IF ;
        ELSIF LC$Property = 'LOV_WIDTH' THEN RETURN TabLOVs(LN$Indice).LOV_WIDTH  ;
        ELSIF LC$Property = 'LOV_HEIGHT' THEN RETURN TabLOVs(LN$Indice).LOV_HEIGHT  ;
        ELSIF LC$Property = 'LOV_MAX_COL_WIDTH' THEN RETURN TabLOVs(LN$Indice).LOV_MAX_COL_WIDTH  ;
        ELSIF LC$Property = 'LOV_COL_SEARCH' THEN RETURN TabLOVs(LN$Indice).LOV_COL_SEARCH  ;
        ELSIF LC$Property = 'LOV_VALIDATION' THEN RETURN TabLOVs(LN$Indice).LOV_VALIDATION  ;
        ELSIF LC$Property = 'LOV_SCHEME' THEN RETURN TabLOVs(LN$Indice).LOV_SCHEME  ;
        ELSIF LC$Property = 'LOV_PAGING' THEN RETURN TabLOVs(LN$Indice).LOV_PAGING  ;
        ELSIF LC$Property = 'LOV_WHERE_CLAUSE' THEN RETURN TabLOVs(LN$Indice).LOV_WHERE_CLAUSE  ;
        ELSIF LC$Property = 'LOV_ORDER_BY' THEN RETURN TabLOVs(LN$Indice).LOV_ORDER_BY  ;
        ELSIF LC$Property = 'LOV_NB_PAGES' THEN RETURN TabLOVs(LN$Indice).LOV_NB_PAGES  ;
        ELSIF LC$Property = 'LOV_BUTTON1' THEN RETURN TabLOVs(LN$Indice).LOV_BUTTON1  ;
        ELSIF LC$Property = 'LOV_BUTTON2' THEN RETURN TabLOVs(LN$Indice).LOV_BUTTON2  ;
        ELSIF LC$Property = 'LOV_FILTER' THEN RETURN TabLOVs(LN$Indice).LOV_FILTER  ;
 END IF ;
     END IF ;
     RETURN NULL ;
  END Get_Lov_Property ;


   -----------------------------------------------
   --  Test if record exists for LOV validation --
   -----------------------------------------------
   FUNCTION Check_Value
   (
  PC$LOV_Name  IN  VARCHAR2
 ,PC$LOV_Form  IN  VARCHAR2
 ,PC$LOV_Col   IN  VARCHAR2
 ,PC$Value     IN  VARCHAR2
 ,PC$ErrorMsg  OUT VARCHAR2
   ) RETURN PLS_INTEGER
   IS
      TYPE T_CUR IS REF CURSOR ;
   Cur         T_CUR ;
   LN$Nbre     PLS_INTEGER ;
   LC$Select   VARCHAR2(10000) ;
   LC$Order    VARCHAR2(10000) ;
   LN$Indice   PLS_INTEGER   ;
   LC$ColType  VARCHAR2(30) ;
   BEGIN

 -- find the LOV --
 LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
 IF LN$Indice > 0 THEN
  LC$Select := LOWER(tabLOVs(LN$Indice).LOV_SELECT) ;
  IF INSTR(LC$Select, 'order by' ) > 0 THEN
   LC$Select := SUBSTR( LC$Select, 1, INSTR(LC$Select, 'order by' ) - 1 ) ;
  END IF ;
  LC$Order := 'Select 1 From DUAL Where exists( Select 1 ' || SUBSTR( LC$Select, INSTR( LC$Select, 'from' ) ) ;
  IF INSTR( LC$Select, 'where' ) > 0 THEN
   LC$Order := LC$Order || ' AND ' ;
  ELSE
   LC$Order := LC$Order || ' WHERE ' ;
  END IF ;

  LC$ColType := Get_LOV_Col_Type( PC$LOV_Name, PC$LOV_Form, PC$LOV_Col) ;

  IF LC$ColType = 'NUMBER' THEN
   LC$Order := LC$Order || PC$LOV_Col || ' = ' || PC$Value  ;
  ELSE
   LC$Order := LC$Order || PC$LOV_Col || ' = ''' || PC$Value || '''' ;
  END IF ;

  LC$Order := LC$Order || ' )' ;

  OPEN  Cur FOR LC$Order ;
  FETCH Cur INTO LN$Nbre ;
  LN$Nbre := Cur%ROWCOUNT ;
  CLOSE Cur ;
  RETURN LN$Nbre ;
 ELSE
  PC$ErrorMsg := 'LAF LOV Validation LOV not found: ' || PC$LOV_Form || '.' || PC$LOV_Name ;
  RETURN -1 ;
 END IF ;
   EXCEPTION
      WHEN OTHERS THEN
         PC$ErrorMsg := 'LAF LOV Validation error: ' || SQLERRM ;
   RETURN -1 ;
   END Check_Value ;


  --------------------------------
  --  return the column's type  --
  --------------------------------
  FUNCTION Get_LOV_Col_Type
 (
    PC$LOV_Name  IN VARCHAR2
   ,PC$LOV_Form  IN VARCHAR2
   ,PC$Col_Name  IN VARCHAR2
 )
  RETURN   VARCHAR2
  IS
    LN$Indice  PLS_INTEGER ;
  BEGIN
      -- find the LOV --
      LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
      IF LN$Indice > 0 THEN
        FOR i IN 1 .. tabLOVs(LN$Indice).LOV_TYPES.COUNT() LOOP
           IF LOWER(tabLOVs(LN$Indice).LOV_TYPES(i).LOV_COLUMN) = LOWER(PC$Col_Name) THEN
              RETURN tabLOVs(LN$Indice).LOV_TYPES(i).LOV_COL_TYPE ;
           END IF ;
        END LOOP ;
      END IF ;
      RETURN '' ;
  END Get_LOV_Col_Type ;


  ------------------------------------------
  --  List of items that support the LOV  --
  ------------------------------------------
  FUNCTION   Set_Lov_Items
 (
    PC$LOV_Name    IN VARCHAR2
   ,PC$LOV_Form    IN VARCHAR2
   ,PR$Items       IN TYP_TAB_CHAR
 )
  RETURN     VARCHAR2
  IS
     LN$Indice  PLS_INTEGER ;
  BEGIN
   -- find the LOV --
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
    tabLOVs(LN$Indice).LOV_ITEMS := PR$Items ;
   END IF ;
   RETURN 'OK' ;
  EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV Adding Item List error: ' || SQLERRM ;
  END Set_Lov_Items ;



  ------------------------------------------
  --  List of items that support the LOV  --
  ------------------------------------------
  FUNCTION   Add_Lov_Item
 (
    PC$LOV_Name    IN VARCHAR2
   ,PC$LOV_Form    IN VARCHAR2
   ,PC$LOV_Item    IN VARCHAR2
 )
  RETURN     VARCHAR2
  IS
 LN$Indice  PLS_INTEGER ;
 LR$Items   TYP_TAB_CHAR ;
  BEGIN
   -- find the LOV --
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
     LR$Items := tabLOVs(LN$Indice).LOV_ITEMS ;
     LR$Items(LR$Items.COUNT + 1) := PC$LOV_Item ;
     tabLOVs(LN$Indice).LOV_ITEMS := LR$Items ;
   END IF ;
   RETURN 'OK' ;
  EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV Adding Item error: ' || SQLERRM ;
  END Add_Lov_Item ;



  -------------------------------
  --  Find LOV for given item  --
  -------------------------------
  FUNCTION Get_Lov_Item
 (
    PC$Item_Name    IN VARCHAR2
   ,PC$LOV_Form     IN VARCHAR2
   ,PC$LOV_Item     IN VARCHAR2
   ,PC$LOV_Name    OUT VARCHAR2
   ,PC$BeanName    OUT VARCHAR2
   ,PC$Column      OUT VARCHAR2
   ,PC$Validation  OUT VARCHAR2
 )
  RETURN VARCHAR2
  IS
  BEGIN
     IF tabLOVs.COUNT > 0 THEN
     FOR i IN 1 .. tabLOVs.COUNT LOOP
     IF TabLOVs.EXISTS(i) THEN
        IF TabLOVS(i).LOV_ITEMS.COUNT > 0 THEN
           FOR j IN 1 .. TabLOVS(i).LOV_ITEMS.COUNT LOOP
           IF LOWER(PC$Item_Name) = LOWER(TabLOVS(i).LOV_ITEMS(j)) THEN
           --  item found --
           PC$LOV_Name   :=  TabLOVS(i).LOV_NAME ;
           PC$BeanName   :=  TabLOVS(i).LOV_BEAN_NAME ;
           PC$Validation :=  TabLOVS(i).LOV_VALIDATION ;
           IF PC$Validation = 'Y' THEN
              FOR k IN 1 .. TabLOVS(i).LOV_ITEM_VALID.COUNT LOOP
                IF LOWER(TabLOVS(i).LOV_ITEM_VALID(k).LOV_ITEM) = LOWER(PC$Item_Name) THEN
                   PC$Column := TabLOVS(i).LOV_ITEM_VALID(k).LOV_COLUMN ;
                END IF ;
              END LOOP;
           END IF ;
           RETURN 'OK' ;
        END IF ;
        END LOOP ;
        END IF ;
     END IF ;
  END LOOP;
  END IF ;
  RETURN 'KO' ;
  EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV getting LOV item error: ' || SQLERRM ;
  END;

  ------------------------
  --  get a LOV record  --
  ------------------------
  FUNCTION   Get_Lov_Record
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
            )
  RETURN     LOV_RECORD
  Is
     LN$Indice  PLS_INTEGER ;
  BEGIN
    -- find the LOV --
    LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
    IF LN$Indice > 0 THEN
       return tabLOVs(LN$Indice) ;
    Else
       return Null ;
    END IF ;
  End Get_Lov_Record;


  PROCEDURE setSearchLabel ( PC$Label IN VARCHAR2 )
  IS
  BEGIN
    GC$SearchLabel := PC$Label ;
  END setSearchLabel ;

  PROCEDURE setSeparator ( PC$Separator IN VARCHAR2 )
  IS
  BEGIN
    GC$Sep := PC$Separator ;
  END setSeparator ;

  FUNCTION getSearchLabel
  RETURN VARCHAR2
  IS
  BEGIN
    RETURN GC$SearchLabel ;
  END  getSearchLabel ;


  FUNCTION getSeparator
  RETURN VARCHAR2
  IS
  BEGIN
    RETURN GC$Sep ;
  END  getSeparator ;


 ---------------------------------
 --   add a LOV Columns width   --
 ---------------------------------
 FUNCTION   Set_Lov_Col_Width
            (
               PC$LOV_Name    IN VARCHAR2
              ,PC$LOV_Form    IN VARCHAR2
              ,PR$Widths      IN TYP_TAB_LOV_COL_WIDTH
            )
 RETURN     VARCHAR2
 IS
   LB$Return  BOOLEAN := TRUE ;
   LB$Found   BOOLEAN := FALSE ;
   LR$Width   TYP_TAB_LOV_COL_WIDTH := PR$Widths ;
   LN$Indice  PLS_INTEGER ;
 BEGIN
   LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
   IF LN$Indice > 0 THEN
      --  LOV mapping inscription --
      TabLOVs(LN$Indice).COL_WIDTH := LR$Width ;
   ELSE
      RETURN 'LAF LOV Column width: '|| PC$LOV_Form || '.' || PC$LOV_Name || ' : LOV not found';
   END IF ;
   RETURN 'OK' ;
 EXCEPTION
     WHEN OTHERS THEN
       RETURN 'LAF LOV error: ' || SQLERRM ;
 END Set_Lov_Col_Width ;

  -------------------------------
  --  get a LOV columns width  --
  -------------------------------
 FUNCTION  Get_Lov_Col_Width
 (
    PC$LOV_Name  IN  VARCHAR2
   ,PC$LOV_Form  IN  VARCHAR2
 ) RETURN TYP_TAB_LOV_COL_WIDTH
  Is
     LN$Indice  PLS_INTEGER ;
     LN$T       TYP_TAB_LOV_COL_WIDTH ;
  BEGIN
    -- find the LOV --
    LN$Indice := Get_LOV_Indice( PC$LOV_Name, PC$LOV_Form ) ;
    IF LN$Indice > 0 THEN
       LN$T := TabLOVS(LN$Indice).COL_WIDTH ;
    END IF ;
    Return LN$T ;
  End Get_Lov_Col_Width;


END Pkg_Db_Laf_Lov;
/

