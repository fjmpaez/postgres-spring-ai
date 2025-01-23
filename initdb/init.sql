CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE furniture
(
    id           uuid not null  PRIMARY KEY,
    name         varchar(255)   not null,
    description  text           not null,
    price        numeric(19, 2) not null,
    type         varchar(255)   not null,
    style        varchar(255)   not null,
    material     varchar(255)   not null,
    color        varchar(255)   not null,
    height       numeric(19, 2) not null,
    width        numeric(19, 2) not null,
    depth        numeric(19, 2) not null,
    embedding    vector(384)
);

CREATE INDEX ON furniture USING HNSW (embedding vector_cosine_ops);