create table if not exists tb_user
(
    id                  bigint generated by default as identity,
    creation_date       timestamp,
    update_date         timestamp,
    uuid                varchar(255),
    username            varchar(255),
    password            varchar(255),
    email               varchar(255),
    person_id           bigint,
    user_role_id        bigint,
    primary key (id)
);

create table if not exists tb_person
(
    id                  bigint generated by default as identity,
    creation_date       timestamp,
    update_date         timestamp,
    uuid                varchar(255),
    name                varchar(255),
    birthday            varchar(255),
    phone               varchar(255),
    user_id             bigint,
    primary key (id)
);


alter table if exists tb_person
    add constraint if not exists UK_86cal5mg9j0t8j9yn28n9iulc unique (uuid);
-- alter table if exists tb_person
--     add constraint if not exists FK349pmtlc4ukgn1so04k2ls4gg foreign key (user_id) references tb_user;

alter table if exists tb_user
    add constraint if not exists UK_86cal5mg9j0t8j9yn28n9iulq unique (uuid);
alter table if exists tb_user
    add constraint if not exists FK349pmtlc4ukgn1so04k2ls4jj foreign key (person_id) references tb_person;
alter table if exists tb_user
    add constraint if not exists FK349pmtlc4ukgn1so04k2ls4gb foreign key (user_role_id) references tb_user_role;
