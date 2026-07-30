-- Supabase/Postgres schema for document storage
-- This table mirrors the Open Dental document structure for API sync

create table if not exists public.documents (
  doc_num bigint primary key,
  pat_num bigint not null references public.patients(pat_num) on delete cascade,
  description text default '',
  note text default '',
  date_created timestamp,
  doc_category bigint default 0,
  file_name text default '',
  img_type text default 'Document',
  tooth_numbers text default '',
  date_t_stamp timestamp,
  prov_num bigint default 0,
  print_heading text default 'false',
  created_at timestamp with time zone default now(),
  updated_at timestamp with time zone default now()
);

create index if not exists documents_pat_num_idx on public.documents (pat_num);
create index if not exists documents_date_created_idx on public.documents (date_created);
create index if not exists documents_doc_category_idx on public.documents (doc_category);

comment on table public.documents is 'Document records synced from Open Dental API.';
comment on column public.documents.doc_num is 'Open Dental document identifier.';
comment on column public.documents.pat_num is 'FK to patients table.';
comment on column public.documents.note is 'Stores _rawBase64_ or _download_ prefixes for deferred file loading.';
comment on column public.documents.img_type is 'One of: Document, Radiograph, Photo, File, Attachment.';
comment on column public.documents.print_heading is 'true or false, controls print header display.';