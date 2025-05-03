--liquibase formatted sql

--changeset Enzhine:1.0.0:2
create table server (
    id bigserial primary key,
    created_at timestamptz not null default now(),
    ---
    name varchar(32) not null,
    region varchar(32) not null,
    host_address text not null,
    active boolean not null
);
--rollback drop table if exists server;

create index ix_server__name_region on server (name, region);
--rollback drop index concurrently if exists ix_server__name_region;

create table voice_session (
    id bigserial primary key,
    created_at timestamptz not null default now(),
    ---
    server_id bigint not null,
    channel_id bigint not null,
    voice_id bigint not null unique,
    secret text not null,
    ---
    foreign key (server_id) references server (id)
);
--rollback drop table if exists voice_session;

create index ix_voice_session__voice_id on voice_session (voice_id);
--rollback drop index concurrently if exists ix_voice_session__voice_id;

create table attendant (
    id bigserial primary key,
    joined_at timestamptz not null default now(),
    --
    user_id bigint not null,
    voice_session_id bigint not null,
    shadow_id int not null,
    ---
    foreign key (voice_session_id) references voice_session (id)
);
--rollback drop table if exists attendant;

create index ix_attendant__user_id on attendant (user_id);
--rollback drop index concurrently if exists ix_attendant__user_id;

create index ix_attendant__voice_session_id on attendant (voice_session_id);
--rollback drop index concurrently if exists ix_voice_session__channel_id;
