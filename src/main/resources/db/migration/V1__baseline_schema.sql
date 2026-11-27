CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(60)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name     VARCHAR(120) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'DOCTOR', 'RECEPTIONIST'))
);

CREATE TABLE patient (
    id             BIGSERIAL PRIMARY KEY,
    first_name     VARCHAR(80)  NOT NULL,
    last_name      VARCHAR(80)  NOT NULL,
    date_of_birth  DATE         NOT NULL,
    email          VARCHAR(120),
    phone          VARCHAR(40),
    address_line   VARCHAR(160),
    city           VARCHAR(80),
    postal_code    VARCHAR(20),
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patient_last_name ON patient (LOWER(last_name));
CREATE INDEX idx_patient_first_name ON patient (LOWER(first_name));

CREATE TABLE doctor (
    id             BIGSERIAL PRIMARY KEY,
    first_name     VARCHAR(80)  NOT NULL,
    last_name      VARCHAR(80)  NOT NULL,
    email          VARCHAR(120),
    phone          VARCHAR(40),
    specialization VARCHAR(80)  NOT NULL,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    user_id        BIGINT       UNIQUE REFERENCES app_user (id),
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doctor_specialization ON doctor (specialization);

CREATE TABLE appointment (
    id            BIGSERIAL PRIMARY KEY,
    patient_id    BIGINT       NOT NULL REFERENCES patient (id),
    doctor_id     BIGINT       NOT NULL REFERENCES doctor (id),
    scheduled_at  TIMESTAMP    NOT NULL,
    reason        VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_appointment_status
        CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED'))
);

-- Cancelled appointments are excluded so the freed slot can be booked again.
CREATE UNIQUE INDEX uq_doctor_slot
    ON appointment (doctor_id, scheduled_at)
    WHERE status <> 'CANCELLED';

CREATE INDEX idx_appointment_doctor ON appointment (doctor_id, scheduled_at);
CREATE INDEX idx_appointment_patient ON appointment (patient_id, scheduled_at);

CREATE TABLE medical_record (
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT       NOT NULL UNIQUE REFERENCES appointment (id),
    patient_id     BIGINT       NOT NULL REFERENCES patient (id),
    doctor_id      BIGINT       NOT NULL REFERENCES doctor (id),
    visit_date     DATE         NOT NULL,
    diagnosis      VARCHAR(255) NOT NULL,
    notes          TEXT,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_record_patient ON medical_record (patient_id, visit_date DESC);
