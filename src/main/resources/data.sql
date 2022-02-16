insert into guest(id, name) values(null, 'Roger Federer');
insert into guest(id, name) values(null, 'Rafael Nadal');
insert into guest(id, name) values(10, 'Rafael Nadal Test');

insert into tennis_court(id, name) values(null, 'Roland Garros - Court Philippe-Chatrier');

insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (1, '2022-12-20T20:00:00.0', '2025-02-20T21:00:00.0', 1);
insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (2, '2020-12-20T20:00:00.0', '2020-05-20T21:00:00.0', 1);
insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (3, '2024-12-20T20:00:00.0', '2025-05-20T21:00:00.0', 1);

insert
    into reservation(id, guest_id, value, reservation_status, schedule_id, refund_value)
    values (1, 1, 10, 'READY_TO_PLAY', 1, 0);

--    NOT_SHOW_UP - RESERVATION
insert
    into reservation(id, guest_id, value, reservation_status, schedule_id, refund_value)
    values (2, 1, 10, 'READY_TO_PLAY', 2, 0);