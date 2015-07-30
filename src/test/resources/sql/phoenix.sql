CREATE VIEW "tsdb-uid" ( "id".val VARBINARY) --default_column_family='t'

DROP VIEW "tsdb-uid"

CREATE VIEW "tsdb-uid" (
	--PK VARCHAR PRIMARY KEY
	PK VARCHAR PRIMARY KEY
--	,"name"."value" VARCHAR
--	,"timestamp"."value" VARBINARY
	,"id"."value" VARCHAR
	,"id"."tagv" VARBINARY
	,"id"."tagk" VARBINARY 
	,"id"."metrics" VARBINARY 
	,"name"."tagv_meta" VARCHAR
	,"name"."tagk_meta" VARCHAR
	,"name"."metrics_meta" VARCHAR
	,"name"."tagv" VARCHAR
	,"name"."tagk" VARCHAR
	,"name"."metrics" VARCHAR
	
) default_column_family='id'

select * from "tsdb-uid" where PK = '/boot'
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

CREATE FUNCTION TOHEX(varbinary, integer, integer) returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$ToHex'

CREATE FUNCTION TSUID(varbinary) returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$TSRowKeyToTSUID'

CREATE FUNCTION DUMP(varbinary) returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$DumpMeta'

CREATE FUNCTION TS(varbinary) returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$CellTimestamp'



-- com.heliosapm.phoenix.udf.OpenTSDBFunctions.ToInt

select PK, TS("name"."tagv_meta") TS from "tsdb-uid" where "tagv" is not null order by TS desc  -- and PK = '/proc'

select PK, DUMP(PK)  from "tsdb-uid" 

where "tagv" is null and "tagk" is null and "metrics" is null

select * from "tsdb-uid"("name"."tagv" VARCHAR) where PK = '  *'

select PK, TOINT("id"."tagv") from "tsdb-uid"  --where "tagv" is not null --and PK = '/proc'

select PK, "tagv" from "tsdb-uid" where "tagv" is not null and PK like '/%'

select * from "tsdb-uid" where "tagv" is not null

DROP function bintoint
