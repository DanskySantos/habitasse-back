create table if not exists tb_address
(
    id                  bigint generated by default as identity,
    creation_date       timestamp,
    update_date         timestamp,
    uuid                varchar(255),
    country             varchar(255),
    state               varchar(255),
    city                varchar(255),
    primary key (id)
);

alter table if exists tb_user
    add constraint if not exists UK_86cal5mg9j0t8j9yn28n9iulq unique (uuid);