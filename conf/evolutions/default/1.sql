# --- !Ups

create table players (
  id varchar not null,
  display_name varchar(100) not null,
  email varchar not null,
  photo_url varchar(300) not null,
  PRIMARY KEY(id)
);

create table matches (
  id bigint generated by default as identity(start with 1) not null,
  start_date timestamp not null,
  end_date timestamp not null,
  game_name varchar(70) not null,
  player_id varchar not null REFERENCES players(id),
  PRIMARY KEY(id)
);

# --- !Downs

drop table "players" if exists;
drop table "matches" if exists;
