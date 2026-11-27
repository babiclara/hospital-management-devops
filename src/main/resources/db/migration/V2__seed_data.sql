INSERT INTO app_user (username, password_hash, full_name, role) VALUES
    ('admin',        '$2y$10$QXUL5q9ARIcUEQyli3/zA.OpQFct1kXaGye70bUSo9CWxJB4YBHIO', 'Ana Adminović',   'ADMIN'),
    ('receptionist', '$2y$10$/e6MiGiY7ZyV0AF3VLnDP.DlD6nk0uBMHMO15n6.3lSUHrms/BNKu', 'Petra Recepcija', 'RECEPTIONIST'),
    ('dr.horvat',    '$2y$10$m1eLTCgQB4/vNRGldmryPehG2j2.KGVMVC4sUqh7My5yzhdyvKvXe', 'Ivan Horvat',     'DOCTOR'),
    ('dr.kovac',     '$2y$10$2QKopFXe1G9KHbIXlFLe6.7zUzdwtkTSCxvW69LCAJJikfl/ubYwK', 'Maja Kovač',      'DOCTOR');

INSERT INTO doctor (first_name, last_name, email, phone, specialization, user_id) VALUES
    ('Ivan', 'Horvat', 'ivan.horvat@hospital.example', '+385 1 2345 001', 'Cardiology',
        (SELECT id FROM app_user WHERE username = 'dr.horvat')),
    ('Maja', 'Kovač',  'maja.kovac@hospital.example',  '+385 1 2345 002', 'Dermatology',
        (SELECT id FROM app_user WHERE username = 'dr.kovac')),
    ('Luka', 'Novak',  'luka.novak@hospital.example',  '+385 1 2345 003', 'Neurology', NULL);

INSERT INTO patient (first_name, last_name, date_of_birth, email, phone, address_line, city, postal_code) VALUES
    ('Marko',    'Marić',   DATE '1985-03-14', 'marko.maric@example.com',    '+385 91 111 2233', 'Ilica 112',          'Zagreb', '10000'),
    ('Ivana',    'Jurić',   DATE '1992-11-02', 'ivana.juric@example.com',    '+385 95 222 3344', 'Vukovarska 28',      'Zagreb', '10000'),
    ('Tomislav', 'Babić',   DATE '1978-07-23', 'tomislav.babic@example.com', '+385 98 333 4455', 'Trg bana Jelačića 1','Zagreb', '10000'),
    ('Petra',    'Vuković', DATE '2001-01-30', 'petra.vukovic@example.com',  '+385 92 444 5566', 'Savska 66',          'Zagreb', '10000');

INSERT INTO appointment (patient_id, doctor_id, scheduled_at, reason, status) VALUES
    ((SELECT id FROM patient WHERE last_name = 'Marić'),
     (SELECT id FROM doctor  WHERE last_name = 'Horvat'),
     CURRENT_DATE + INTERVAL '1 day' + TIME '09:00', 'Routine cardiology check-up', 'SCHEDULED'),
    ((SELECT id FROM patient WHERE last_name = 'Jurić'),
     (SELECT id FROM doctor  WHERE last_name = 'Horvat'),
     CURRENT_DATE + INTERVAL '1 day' + TIME '10:00', 'Chest pain follow-up', 'SCHEDULED'),
    ((SELECT id FROM patient WHERE last_name = 'Babić'),
     (SELECT id FROM doctor  WHERE last_name = 'Kovač'),
     CURRENT_DATE - INTERVAL '7 day' + TIME '11:30', 'Skin rash examination', 'COMPLETED');

INSERT INTO medical_record (appointment_id, patient_id, doctor_id, visit_date, diagnosis, notes)
SELECT a.id, a.patient_id, a.doctor_id, DATE(a.scheduled_at),
       'Contact dermatitis',
       'Mild reaction on both forearms. Topical corticosteroid prescribed for ten days.'
FROM appointment a
WHERE a.status = 'COMPLETED';
