create table restaurant
(
    id          serial primary key,
    "name"      varchar not null,
    description varchar
);

create table restaurant_location
(
    id            serial primary key,
    restaurant_id int not null references restaurant (id),
    address       varchar,
    latitude      real,
    longitude     real,
    phone         varchar
);

create table location_photo
(
    id          serial primary key,
    location_id int     not null references restaurant_location (id),
    photo_url   varchar not null
);

create table location_working_hour
(
    id          serial primary key,
    location_id int     not null references restaurant_location (id),
    day_of_week varchar not null,
    open_at     time    not null,
    close_at    time    not null
);

create table "table"
(
    id              serial primary key,
    location_id     int not null references restaurant_location (id),
    number_of_seats int not null
);

create table "user"
(
    id         serial primary key,
    email      varchar not null,
    "password" varchar not null,
    first_name varchar,
    last_name  varchar
);

create table reservation
(
    id            serial primary key,
    table_id      int       not null references "table" (id),
    "start"       timestamp not null,
    "end"         timestamp not null,
    reservator_id int       not null references "user" (id)
);