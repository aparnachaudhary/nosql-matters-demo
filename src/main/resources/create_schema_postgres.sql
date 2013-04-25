CREATE TABLE health_record
(
  id bigint NOT NULL,
  dockey character varying(255) NOT NULL,
  contenttype character varying(100),
  filename character varying(100),
  field1 character varying(100),
  field2 character varying(100),
  field3 character varying(100),
  CONSTRAINT health_record_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);


ALTER TABLE health_record OWNER TO postgres;

ALTER TABLE health_record ADD CONSTRAINT DOCKEY_CONSTRAINT UNIQUE (dockey);

CREATE TABLE health_record_data
(
  id bigint NOT NULL,
  dockey character varying(255) NOT NULL,
  data oid,
  CONSTRAINT health_record_data_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE health_record_data OWNER TO postgres;

CREATE SEQUENCE health_record_data_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
  ALTER TABLE health_record_data_seq
  OWNER TO postgres;

CREATE SEQUENCE health_record_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
  ALTER TABLE health_record_seq
  OWNER TO postgres;

