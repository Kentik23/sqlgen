--liquibase formatted sql
--changeset aleandivanov:1-0-0-CD-GISMUBI-27988-init.sql runInTransaction:true

create table dm_ml.dim_elap_provision_hist (
    src_id                      BIGINT              not null default -1,
    task_id                     BIGINT              not null default -1,
    task_dttm                   TIMESTAMP WITH TIME ZONEnot null default now(),
    create_dttm                 TIMESTAMP WITH TIME ZONEnot null default now(),
    modify_dttm                 TIMESTAMP WITH TIME ZONEnot null default now(),
    action_ind                  VARCHAR(1)          not null default 'X',
    eff_dttm                    TIMESTAMP WITH TIME ZONEnot null default now(),
    exp_dttm                    TIMESTAMP WITH TIME ZONEnot null default now(),
    elap_guid                   UUID                ,
    elap_id                     BIGINT              not null default -1,
    digital_profile_id          BIGINT              not null default -1,
    visit_region_cval           TEXT                ,
    arrival_purpose_id          BIGINT              not null default -1,
    plan_entry_dt               DATE                ,
    plan_exit_dt                DATE                ,
    country_exit_id             BIGINT              not null default -1,
    elap_create_dt              DATE                
)
with
( appendonly = true,
  orientation = column,
  compresstype = zstd,
  compresslevel = 5
)
distributed by ();

--rollback drop table dm_ml.dim_elap_provision_hist;