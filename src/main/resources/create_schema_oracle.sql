CREATE TABLE health_record_data ( 
  	id NUMBER NOT NULL PRIMARY KEY,
	dockey VARCHAR2(255) NOT NULL,
    "DATA" BLOB
);


CREATE TABLE health_record
(
  id NUMBER NOT NULL PRIMARY KEY,
  dockey VARCHAR2(100),
  contenttype VARCHAR2(100),
  filename VARCHAR2(100),
  field1 VARCHAR2(100),
  field2 VARCHAR2(100),
  field3 VARCHAR2(100)
);


CREATE SEQUENCE health_record_data_seq;
  
CREATE SEQUENCE health_record_seq;
