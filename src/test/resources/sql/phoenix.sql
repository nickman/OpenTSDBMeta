CREATE VIEW "tsdb-uid" ( "id".val VARBINARY) --default_column_family='t'

DROP VIEW "tsdb-uid"

CREATE VIEW "tsdb-uid" (pk VARCHAR PRIMARY KEY, "id"."tagv" VARBINARY, "id"."tagk" VARBINARY, "id"."metrics" VARBINARY) 

CREATE VIEW "tsdb" (pk VARBINARY PRIMARY KEY, "t"."id" VARBINARY) 

select PK, '' || ("id"."tagv") from "tsdb-uid" where "tagv" is not null order by pk desc

select length(PK), "tagv", "tagk", "metrics" from "tsdb-uid" where "tagv" is not null limit 10

select * from "tsdb-uid" where "tagv" is not null and PK != ' ' limit 10

select * from "tsdb-uid"

select * from "tsdb"

select BINTOINT(PK) from "tsdb"

select * from "tsdb-uid" where BINTOINT("tagk") = 341

select count(*) from "tsdb"

drop VIEW TAGVTX

create view TAGVTX (PK VARCHAR PRIMARY KEY, TAGV VARBINARY) as select PK, "id"."tagv" from "tsdb-uid" where "id"."tagv" is not null default_column_family='id'

select * from TAGVTX

create TABLE TAGVTX (NAME VARCHAR PRIMARY KEY, ID INTEGER) 

UPSERT INTO TAGVTX select PK, BINTOINT("id"."tagv") from "tsdb-uid" where "id"."tagv" is not null

select * from TAGVTX

select * from SYSTEM."FUNCTION"

--CREATE FUNCTION bintoint(varbinary) returns integer as 'com.heliosapm.phoenix.udf.BinaryToInteger'
CREATE FUNCTION TOINT(varbinary) returns integer as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$ToInt'

CREATE FUNCTION TOHEX(varbinary, integer, integer) returns varchar as 'com.heliosapm.phoenix.udf.OpenTSDBFunctions$ToHex'

-- com.heliosapm.phoenix.udf.OpenTSDBFunctions.ToInt

select PK, tohex("tagv") from "tsdb-uid" where "tagv" is not null and PK = '/proc'

select PK, "tagv" from "tsdb-uid" where "tagv" is not null and PK = '/proc'

select * from "tsdb-uid" where "tagv" is not null

DROP function bintoint
