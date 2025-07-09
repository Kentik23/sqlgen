--liquibase formatted sql
--changeset aleandivanov:1-0-0-CD-GISMUBI-27988-init.sql runInTransaction:true

create table dm_ml.ap_soop_ban_case (
    case_id                     BIGINT              not null default -1,
    soop_case_id                BIGINT              not null default -1,
    department_name             VARCHAR(256)        not null default 'Не определено',
    department_id               BIGINT              not null default -1,
    decision_no                 VARCHAR(128)        not null default 'N/D',
    decision_dt                 DATE                ,
    decision_circumstances_desc TEXT                ,
    decision_employee_first_nameVARCHAR(256)        not null default 'Не определено',
    decision_employee_last_name VARCHAR(256)        not null default 'Не определено',
    decision_employee_middle_nameVARCHAR(256)        not null default 'Не определено',
    restr_days_qnt              BIGINT              not null default 0,
    restr_end_dt                DATE                ,
    cancel_dt                   DATE                ,
    cancel_circumstances_desc   TEXT                ,
    cancel_employee_first_name  VARCHAR(256)        not null default 'Не определено',
    cancel_employee_last_name   VARCHAR(256)        not null default 'Не определено',
    cancel_employee_middle_name VARCHAR(256)        not null default 'Не определено',
    soop_person_id              BIGINT              not null default -1,
    esfl_person_id              BIGINT              not null default -1,
    create_dttm                 TIMESTAMP WITH TIME ZONEnot null default now(),
    modify_dttm                 TIMESTAMP WITH TIME ZONEnot null default now(),
    delete_dttm                 TIMESTAMP WITH TIME ZONEnot null default now(),
    action_ind                  VARCHAR(1)          not null default 'X',
    version                     INTEGER             ,
    soop_request_id             BIGINT              not null default -1,
    department_code_no          VARCHAR(128)        not null default 'N/D',
    case_status_id              BIGINT              not null default -1
)
with
( appendonly = true,
  orientation = column,
  compresstype = zstd,
  compresslevel = 5
)
distributed by (case_id);

--rollback drop table dm_ml.ap_soop_ban_case;