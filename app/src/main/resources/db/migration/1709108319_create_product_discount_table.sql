create table product_discount(
    id uuid primary key,
    product_id uuid not null,
    strategy JSONB not null,
    active_to timestamp not null
);