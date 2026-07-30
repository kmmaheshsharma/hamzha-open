-- Supabase/Postgres schema for appointment storage
-- This table mirrors the Open Dental appointment structure for API sync

create table if not exists public.appointments (
  apt_num bigint primary key,
  pat_num bigint not null references public.patients(pat_num) on delete cascade,
  apt_status text default '',
  pattern text default '',
  confirmed bigint default 0,
  time_locked text default '',
  op bigint default 0,
  note text default '',
  prov_num bigint default 0,
  prov_abbr text default '',
  prov_hyg bigint default 0,
  apt_date_time timestamp,
  next_apt_num bigint default 0,
  unsched_status bigint default 0,
  is_new_patient text default '',
  proc_descript text default '',
  assistant bigint default 0,
  clinic_num bigint default 0,
  is_hygiene text default '',
  date_t_stamp timestamp,
  date_time_arrived timestamp,
  date_time_seated timestamp,
  date_time_dismissed timestamp,
  ins_plan1 bigint default 0,
  ins_plan2 bigint default 0,
  date_time_asked_to_arrive timestamp,
  color_override text default '',
  appointment_type_num bigint default 0,
  sec_user_num_entry bigint default 0,
  sec_date_t_entry timestamp,
  priority text default '',
  pattern_secondary text default '',
  item_order_planned bigint default 0,
  is_mirrored text default '',
  e_service_log_type text default '',
  created_at timestamp with time zone default now(),
  updated_at timestamp with time zone default now()
);

create index if not exists appointments_pat_num_idx on public.appointments (pat_num);
create index if not exists appointments_apt_date_time_idx on public.appointments (apt_date_time);
create index if not exists appointments_apt_status_idx on public.appointments (apt_status);
create index if not exists appointments_prov_num_idx on public.appointments (prov_num);
create index if not exists appointments_clinic_num_idx on public.appointments (clinic_num);

comment on table public.appointments is 'Appointment records synced from Open Dental API.';
comment on column public.appointments.apt_num is 'Open Dental appointment identifier.';
comment on column public.appointments.pat_num is 'FK to patients table.';
comment on column public.appointments.apt_status is 'Appointment status code (e.g. Scheduled, Complete, Broken, etc.).';
comment on column public.appointments.pattern is 'Time pattern for the appointment slot.';
comment on column public.appointments.note is 'Appointment note text.';