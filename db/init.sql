CREATE TABLE list
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name text not null
);

CREATE TABLE task
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    text   not null,
    list_id BIGINT NOT NULL REFERENCES list
);
