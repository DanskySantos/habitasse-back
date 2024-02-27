create table if not exists token
(
    id                      bigint generated by default as identity,
    creation_date           timestamp,
    update_date             timestamp,
    uuid                    varchar(255),
    token                   varchar(255),
    token_type              varchar(255),
    revoked                 boolean,
    expired                 boolean,
    user_id                 bigint,
    primary key (id)
);

alter table if exists token
    add constraint if not exists FK349pmtlc4ukgn1so04k2lstyu foreign key (user_id) references tb_user;