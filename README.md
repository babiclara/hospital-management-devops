# Hospital Management - Applied DevOps project

Graduate study, Software Engineering, 3rd semester. Author: Lara Babić.

This is a small administrative web application for a hospital. I built it so
that I could demonstrate a working DevOps pipeline around it. Every change goes
from a written requirement to an issue, then a branch, then a pull request, and
from there it is built, tested, packaged, published and monitored automatically.

The application itself is kept simple on purpose. The pipeline is the part this
project is really about.

**Technology:** Java 25, Spring Boot 4.1.2, Thymeleaf, Spring Data JPA,
PostgreSQL 17, Flyway, Maven.

---

## 1. Requirements analysis

### 1.1 How I derived the requirements

The project scenario is written as prose about how hospital staff work. I went
through it sentence by sentence and wrote down every obligation it places on the
system as one requirement. Sentences about how well the system has to behave,
rather than what it has to do, went into the non-functional list instead.

I followed three rules while doing this.

**One requirement says one thing.** The scenario says staff should be able to
search for a patient and view their information. I made that two requirements,
because search and view are separate screens and either one can break on its own.

**Every requirement names a role.** The scenario restricts what each role can
do, so if I write a requirement without saying who performs it, I can't check it
against the access rules later.

**Every requirement has to be observable.** If I couldn't think of a way to show
it working in the running application, I either reworded it until I could, or
moved it to the non-functional list.

Requirements marked Must are needed for the application to match the scenario.
Should means it makes the demonstration better but I could drop it.

### 1.2 Functional requirements

#### Patient management

| ID | Requirement | Role | Priority |
|----|-------------|------|----------|
| FR-01 | Register a new patient with first name, last name, date of birth, contact details and address | Receptionist | Must |
| FR-02 | Search for patients by name | Receptionist, Doctor, Administrator | Must |
| FR-03 | View a single patient's full details | Receptionist, Doctor, Administrator | Must |
| FR-04 | Edit a patient's details, for example when they change their phone number | Receptionist | Must |
| FR-05 | Deactivate a patient record without deleting it, so previous appointments stay intact | Receptionist | Must |
| FR-06 | Reactivate a patient who was deactivated earlier | Receptionist | Should |

#### Doctor management

| ID | Requirement | Role | Priority |
|----|-------------|------|----------|
| FR-07 | Register a doctor with name, contact details and medical specialization | Administrator | Must |
| FR-08 | View the list of doctors and a single doctor's details | Administrator | Must |
| FR-09 | Update a doctor's details | Administrator | Must |
| FR-10 | Deactivate a doctor who is no longer available | Administrator | Must |
| FR-11 | See which doctors are currently available to take appointments | Receptionist | Must |
| FR-12 | Filter available doctors by specialization | Receptionist | Should |

#### Appointment management

| ID | Requirement | Role | Priority |
|----|-------------|------|----------|
| FR-13 | Schedule an appointment by picking a patient and a doctor and entering date, time and reason for the visit | Receptionist | Must |
| FR-14 | Refuse an appointment that would give one doctor two appointments at the same date and time | Receptionist | Must |
| FR-15 | View all existing appointments | Receptionist, Administrator | Must |
| FR-16 | Update an existing appointment | Receptionist | Must |
| FR-17 | Cancel an appointment, which frees the doctor's slot again | Receptionist | Must |
| FR-18 | Mark an appointment as scheduled, completed or cancelled | Receptionist, Doctor | Must |

#### Medical records

| ID | Requirement | Role | Priority |
|----|-------------|------|----------|
| FR-19 | See the patients who have appointments with the signed-in doctor | Doctor | Must |
| FR-20 | Record what happened at a consultation: date of visit, diagnosis and notes | Doctor | Must |
| FR-21 | View a patient's previous medical records | Doctor | Must |
| FR-22 | Export the signed-in doctor's medical records as a CSV report | Doctor | Must |

#### Authentication and access control

| ID | Requirement | Role | Priority |
|----|-------------|------|----------|
| FR-23 | Make users sign in before they reach any protected page | All | Must |
| FR-24 | Tell the receptionist, doctor and administrator roles apart | All | Must |
| FR-25 | Let a receptionist manage patients and appointments, but not medical notes | Receptionist | Must |
| FR-26 | Let an administrator manage doctors and user accounts | Administrator | Must |
| FR-27 | Limit a doctor to the appointments and medical information they are allowed to see | Doctor | Must |
| FR-28 | Let users sign out | All | Must |

