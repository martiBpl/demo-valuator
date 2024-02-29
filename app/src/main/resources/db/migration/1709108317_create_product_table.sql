create table product(
    id uuid primary key,
    product_id uuid not null,
    name varchar(255) not null,
    price decimal(10, 4) not null
);