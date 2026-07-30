-- Supabase/Postgres schema for procedure log storage
-- This table mirrors the Open Dental procedurelog structure for API sync

create table if not exists public.procedure_logs (
  proc_num bigint primary key,
  pat_num bigint not null references public.patients(pat_num) on delete cascade,
  apt_num bigint default 0 references public.appointments(apt_num) on delete set null,
  proc_date date,
  proc_fee numeric(12,2) default 0,
  surf text default '',
  tooth_num text default '',
  tooth_range text default '',
  priority bigint default 0,
  proc_status text default '',
  prov_num bigint default 0,
  prov_abbr text default '',
  dx bigint default 0,
  dx_name text default '',
  planned_apt_num bigint default 0,
  place_service text default '',
  prosthesis text default '',
  date_original_prosth date,
  claim_note text default '',
  date_entry_c date,
  clinic_num bigint default 0,
  diagnostic_code text default '',
  is_princ_diag text default '',
  code_num bigint default 0,
  proc_code text default '',
  descript text default '',
  unit_qty integer default 0,
  base_units integer default 0,
  date_tp date,
  site_num bigint default 0,
  hide_graphics text default '',
  canadian_type_codes text default '',
  proc_time text default '',
  proc_time_end text default '',
  date_t_stamp timestamp,
  prognosis bigint default 0,
  is_locked text default '',
  billing_note text default '',
  snomed_body_site text default '',
  diagnostic_code2 text default '',
  diagnostic_code3 text default '',
  diagnostic_code4 text default '',
  discount numeric(12,2) default 0,
  is_date_prosth_est text default '',
  icd_version integer default 0,
  sec_date_entry timestamp,
  discount_plan_amt numeric(12,2) default 0,
  created_at timestamp with time zone default now(),
  updated_at timestamp with time zone default now()
);

create index if not exists procedure_logs_pat_num_idx on public.procedure_logs (pat_num);
create index if not exists procedure_logs_apt_num_idx on public.procedure_logs (apt_num);
create index if not exists procedure_logs_proc_date_idx on public.procedure_logs (proc_date);
create index if not exists procedure_logs_proc_status_idx on public.procedure_logs (proc_status);
create index if not exists procedure_logs_prov_num_idx on public.procedure_logs (prov_num);
create index if not exists procedure_logs_clinic_num_idx on public.procedure_logs (clinic_num);
create index if not exists procedure_logs_proc_code_idx on public.procedure_logs (proc_code);

comment on table public.procedure_logs is 'Procedure log records synced from Open Dental API.';
comment on column public.procedure_logs.proc_num is 'Open Dental procedure identifier.';
comment on column public.procedure_logs.pat_num is 'FK to patients table.';
comment on column public.procedure_logs.apt_num is 'FK to appointments table.';
comment on column public.procedure_logs.proc_status is 'Procedure status (e.g. C = Completed, TP = Treatment Planned, etc.).';
comment on column public.procedure_logs.proc_code is 'Procedure code (e.g. D0120, D0150).';