create table tb_confirmation_code
(
    id                      bigint generated by default as identity,
    creation_date           timestamp,
    update_date             timestamp,
    uuid                    varchar(255),
    user_id                 bigint,
    code                    bigint,
    primary key (id)
);

alter table tb_confirmation_code
    add constraint UK_86cal5mg9j0t8j9yn28n9it43 unique (uuid);
alter table tb_confirmation_code
    add constraint FK349pmtlc4ukgn1so04k2l00ty foreign key (user_id) references tb_user;
