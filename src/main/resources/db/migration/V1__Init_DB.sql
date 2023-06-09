-- Cities
create table cities (
id bigint not null auto_increment unique,
city varchar(255) not null,
region varchar(255) not null,
primary key (id)
);

-- Users
create table users (
user_id bigint not null unique,
first_symbol char(1) not null,
primary key (user_id)
);

-- Users_Cities
create table users_cities (
user_id bigint not null,
id bigint not null
);

alter table users_cities
 add constraint users_cities_fk_cities foreign key (id) references cities (id);

alter table users_cities
 add constraint users_cities_fk_users foreign key (user_id) references users (user_id);

-- Index on City
create index index_city on cities (city);

-- Unique on City and Region
alter table cities add constraint city_unique unique (city, region);