CREATE VIEW "tsdb-uid" ( "id".val VARBINARY) --default_column_family='t'

DROP VIEW "tsdb-uid"

CREATE VIEW "tsdb-uid" (
	--PK VARCHAR PRIMARY KEY
	PK VARBINARY PRIMARY KEY
--	,"name"."value" VARCHAR
--	,"timestamp"."value" VARBINARY
	,"name"."tagv" VARCHAR
	,"id"."tagv" VARBINARY	
	,"name"."tagv_meta" VARCHAR

	,"name"."tagk" VARCHAR
	,"id"."tagk" VARBINARY 	
	,"name"."tagk_meta" VARCHAR

	,"name"."metrics" VARCHAR
	,"id"."metrics" VARBINARY 
	,"name"."metrics_meta" VARCHAR
) default_column_family='id'

select TOHEX(PK, 0, 3), "name"."tagv", "id"."tagv" ITAGV, "name"."tagv_meta" from "tsdb-uid"   where "name"."tagv" is not null and "name"."tagv" = '17'

SELECT PK UID,  "name"."tagv" NAME, "id"."tagv" ID, "name"."tagv_meta" META FROM "tsdb-uid" WHERE "name"."tagv" is not null

select TOHEX(PK), "name"."tagk", "id"."tagk", "name"."tagk_meta" from "tsdb-uid"   where "name"."tagk" is not null

select TOHEX(PK, 0, 3), "name"."metrics", "id"."metrics", "name"."metrics_meta" from "tsdb-uid"   where "name"."metrics" is not null

select * from "tsdb-uid" 


where PK = '/boot'
UNION ALL
select * from "tsdb-uid" where PK = '  *'

select count(*) from "tsdb-uid" 


(UID.tagv_meta VARCHAR)

CREATE VIEW "tsdb" (pk VARBINARY PRIMARY KEY, "t"."id" VARBINARY) 

select PK, '' || ("id"."tagv") from "tsdb-uid" where "tagv" is not null order by pk desc

select length(PK), "tagv", "tagk", "metrics" from "tsdb-uid" where "tagv" is not null limit 10

select * from "tsdb-uid" where "tagv" is not null and PK != ' ' limit 10

select * from "tsdb-uid"

select TSUID(PK) from "tsdb" limit 3

select BINTOINT(PK) from "tsdb"

select * from "tsdb-uid" where BINTOINT("tagk") = 341

select count(*) from "tsdb"

drop VIEW TAGVTX

create view TAGVTX (PK VARCHAR PRIMARY KEY, TAGV VARBINARY) as select PK, "id"."tagv" from "tsdb-uid" where "id"."tagv" is not null default_column_family='id'

select * from TAGVTX

create TABLE TAGVTX (NAME VARCHAR PRIMARY KEY, ID INTEGER) 

UPSERT INTO TAGVTX select PK, BINTOINT("name"."tagv_meta") from "tsdb-uid" where "id"."tagv" is not null

select * from TAGVTX

select * from SYSTEM."FUNCTION"

--CREATE FUNCTION bintoint(varbinary) returns integer as 'com.heliosapm.phoenix.udf.BinaryToInteger'
CREATE FUNCTION TOINT(varbinary) returns integer as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$ToInt'

DROP FUNCTION TOHEX

CREATE FUNCTION TOHEX(varbinary, integer constant defaultvalue = 'null', integer constant defaultvalue = 'null') returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$ToHex'

CREATE FUNCTION TSUID(varbinary) returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$TSRowKeyToTSUID'

CREATE FUNCTION DUMP(varbinary) returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$DumpMeta'

CREATE FUNCTION TS(varbinary) returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$CellTimestamp'



-- com.heliosapm.phoenix.udf.OpenTSDBFunctions.ToInt

select PK, TS("name"."tagv_meta") TS from "tsdb-uid" where "tagv" is not null order by TS desc  -- and PK = '/proc'

select PK, DUMP(PK) from "tsdb-uid" 

select DUMP("name"."tagk") from "tsdb-uid" where PK = 'disk'

where "tagv" is null and "tagk" is null and "metrics" is null

select * from "tsdb-uid"("name"."tagv" VARCHAR) where PK = '  *'

select PK, TOINT("id"."tagv") from "tsdb-uid"  --where "tagv" is not null --and PK = '/proc'

select PK, "tagv" from "tsdb-uid" where "tagv" is not null and PK like '/%'

select * from "tsdb-uid" where "tagv" is not null

DROP function bintoint



set PHOENIXCP=c:\libs\java\phoenix\phoenix-4.4.0-HBase-0.98-bin\phoenix-4.4.0-HBase-0.98-server.jar;c:\libs\java\phoenix\phoenix-4.4.0-HBase-0.98-bin\phoenix-4.4.0-HBase-0.98-client.jar;c:\hprojects\OpenTSDBMeta\target\tsdbmeta-1.0-SNAPSHOT.jar
set H2DRIVERS=%PHOENIXCP%
@start javaw -cp "h2-1.4.187.jar;%H2DRIVERS%;%CLASSPATH%" -Dphoenix.functions.allowUserDefinedFunctions=true org.h2.tools.Console %*
@if errorlevel 1 pause

@rem CREATE LINKED TABLE LINK('org.apache.phoenix.jdbc.PhoenixDriver', 'jdbc:phoenix:localhost', '', '','(SELECT * FROM "tsdb-uid")');
@rem CREATE LINKED TABLE TAGV('org.apache.phoenix.jdbc.PhoenixDriver', 'jdbc:phoenix:localhost', '', '','"tsdb-uid"');

-- FOR H2 LINK
-- CREATE LINKED TABLE PTAGV('org.apache.phoenix.jdbc.PhoenixDriver', 'jdbc:phoenix:localhost', '', '','(SELECT PK UID,  "name"."tagv" NAME, "id"."tagv" ID, "name"."tagv_meta" META FROM "tsdb-uid" WHERE "name"."tagv" is not null)');

-- CREATE LINKED TABLE PTAGK('org.apache.phoenix.jdbc.PhoenixDriver', 'jdbc:phoenix:localhost', '', '','(SELECT PK UID,  "name"."tagk" NAME, "id"."tagk" ID, "name"."tagk_meta" META FROM "tsdb-uid" WHERE "name"."tagk" is not null)');

-- CREATE LINKED TABLE PMETRIC('org.apache.phoenix.jdbc.PhoenixDriver', 'jdbc:phoenix:localhost', '', '','(SELECT PK UID,  "name"."metrics" NAME, "id"."metrics" ID, "name"."metrics_meta" META FROM "tsdb-uid" WHERE "name"."metrics" is not null)');