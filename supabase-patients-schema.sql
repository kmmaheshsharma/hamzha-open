-- Supabase/Postgres schema suggestion for storing patient API payloads
-- This is intentionally simple and practical for a first implementation.

create table if not exists public.patients (
  pat_num bigint primary key,
  l_name text not null,
  f_name text not null,
  middle_i text default '',
  preferred text default '',
  pat_status text default 'Patient',
  birthdate date,
  ssn text default '',
  address text default '',
  address2 text default '',
  city text default '',
  state text default '',
  zip text default '',
  hm_phone text default '',
  wk_phone text default '',
  wireless_phone text default '',
  guarantor bigint default 0,
  email text default '',
  pri_prov bigint default 1,
  sec_prov bigint default 0,
  fee_sched bigint default 0,
  billing_type text default 'Standard',
  chart_number text default '',
  medicaid_id text default '',
  employer_num bigint default 0,
  date_first_visit date,
  clinic_num bigint default 0,
  clinic_abbr text default '',
  has_ins text default '',
  premed boolean default false,
  ward text default '',
  prefer_confirm_method text default 'None',
  prefer_contact_method text default 'None',
  prefer_recall_method text default 'None',
  language text default '',
  admit_date date,
  site_num bigint default 0,
  site_desc text default '',
  super_family bigint default 0,
  txt_msg_ok text default 'Unknown',
  sec_user_num_entry bigint default 0,
  sec_date_entry date,
  est_balance numeric(12,2) default 0,
  bal_0_30 numeric(12,2) default 0,
  bal_31_60 numeric(12,2) default 0,
  bal_61_90 numeric(12,2) default 0,
  bal_over_90 numeric(12,2) default 0,
  ins_est numeric(12,2) default 0,
  bal_total numeric(12,2) default 0,
  date_time_last_aging timestamp,
  created_at timestamp with time zone default now(),
  updated_at timestamp with time zone default now()
);

create index if not exists patients_last_name_idx on public.patients (l_name);
create index if not exists patients_first_name_idx on public.patients (f_name);
create index if not exists patients_birthdate_idx on public.patients (birthdate);

comment on table public.patients is 'Canonical patient records for API sync and integration workflows.';
comment on column public.patients.pat_num is 'Open Dental style patient identifier.';
comment on column public.patients.language is 'Language code such as eng or spa, blank means none.';
