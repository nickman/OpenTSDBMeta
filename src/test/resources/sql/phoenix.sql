CREATE VIEW "tsdb-uid" ( "id".val VARBINARY) --default_column_family='t'

DROP VIEW "tsdb-uid"

CREATE VIEW "tsdb-uid" (pk VARCHAR PRIMARY KEY, "id"."tagv" VARBINARY, "id"."tagk" VARBINARY, "id"."metrics" VARBINARY) 

CREATE VIEW "tsdb" (pk VARBINARY PRIMARY KEY, "t"."id" VARBINARY) 

select PK, '' || ("id"."tagv") from "tsdb-uid" where "tagv" is not null order by pk desc

select length(PK), "tagv", "tagk", "metrics" from "tsdb-uid" where "tagv" is not null limit 10

select * from "tsdb-uid" where "tagv" is not null and PK != ' ' limit 10

select * from "tsdb"

select count(*) from "tsdb"

drop TABLE TAGVTX

create view TAGVTX (PK VARCHAR PRIMARY KEY, "id"."tagv" VARBINARY) as select * from "tsdb-uid" where "tagv" is not null

create TABLE TAGVTX (NAME VARCHAR PRIMARY KEY, ID VARCHAR) 

UPSERT INTO TAGVTX select * from "tsdb-uid" where "tagv" is not null

select * from TAGVTX

select * from SYSTEM."FUNCTION"

CREATE FUNCTION bintoint(varbinary) returns integer as 'com.heliosapm.phoenix.udf.BinaryToInteger'

select PK, bintoint("tagv") from "tsdb-uid" where "tagv" is not null and PK = '/proc'

select PK, "tagv" from "tsdb-uid" where "tagv" is not null and PK = '/proc'

select * from "tsdb-uid" where "tagv" is not null

DROP function bintoint