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

### 1.3 Non-functional requirements

| ID | Requirement | How I verify it |
|----|-------------|-----------------|
| NFR-01 | A normal request finishes within two seconds under the demonstration workload | Request duration histogram in Grafana, with buckets at 100 ms, 500 ms, 1 s and 2 s |
| NFR-02 | The application stays available during normal operation | Container healthcheck polls the liveness probe |
| NFR-03 | The application recovers on its own after an application or container failure | Docker restart policy and healthcheck. I demonstrate it by killing the container and watching it come back |
| NFR-04 | Passwords are never stored as plain text | BCrypt hashes in the database, which you can see by querying the `app_user` table |
| NFR-05 | Role permissions are enforced on the server, not just hidden in the interface | Integration tests ask for forbidden URLs directly and expect HTTP 403 |
| NFR-06 | The application runs as a container image and behaves the same anywhere | Image published to GHCR and started with `docker compose` |
| NFR-07 | Every change is built, tested and packaged automatically before it can be merged | GitHub Actions pipeline, set as a required status check on `main` |
| NFR-08 | The database schema is versioned and applied automatically | Flyway migrations run at startup and Hibernate validates the result |

Two of these are worth explaining.

NFR-01 is the only number the scenario actually gives me, and it is why the
application records a histogram of request times instead of an average. An
average would hide the slow requests. A histogram with a bucket at two seconds
tells me directly how many requests met the target.

NFR-03 is what makes the deployment part of this project worth anything. If I
have to restart the application by hand then I haven't really delivered it. The
healthcheck has to work without signing in, which is why the liveness endpoint
is the only URL besides the login page that is public.

### 1.4 Task decomposition

I grouped the requirements into seven epics. Each epic is a label in GitHub
Issues, and each task below is one issue with its own Definition of Done,
tracked on the project board in section 3.

I sized the tasks so that one branch and one pull request finish one task. Where
a requirement was too big for that, like patient management, I split it between
reading data and changing it, because those need different tests.

| Epic | Task | Covers |
|------|------|--------|
| **E1 Foundation** | T-01 Project setup, build configuration and container image | — |
| | T-02 Database schema and seed data | NFR-08 |
| **E2 Security** | T-03 Sign in and sign out with hashed passwords | FR-23, FR-28, NFR-04 |
| | T-04 Role based access rules for the three roles | FR-24 – FR-27, NFR-05 |
| **E3 Patients** | T-05 Register and edit a patient | FR-01, FR-04 |
| | T-06 Search and view patients | FR-02, FR-03 |
| | T-07 Deactivate and reactivate a patient | FR-05, FR-06 |
| **E4 Doctors** | T-08 Register and edit a doctor | FR-07, FR-09 |
| | T-09 List doctors, view details, filter by specialization | FR-08, FR-11, FR-12 |
| | T-10 Deactivate a doctor | FR-10 |
| **E5 Appointments** | T-11 Schedule an appointment | FR-13 |
| | T-12 Stop a doctor being double booked | FR-14 |
| | T-13 View, update and cancel appointments | FR-15 – FR-18 |
| **E6 Medical records** | T-14 Doctor's own appointments and patients | FR-19 |
| | T-15 Record and view consultation outcomes | FR-20, FR-21 |
| | T-16 CSV export of a doctor's records | FR-22 |
| **E7 Delivery** | T-17 Continuous integration pipeline | NFR-07 |
| | T-18 Continuous delivery to GHCR with artifacts | NFR-06 |
| | T-19 End to end tests in the pipeline | verifies FR-13, FR-14, FR-22 |
| | T-20 Monitoring and dashboards | NFR-01, NFR-02, NFR-03 |

That is 20 tasks covering 28 functional and 8 non-functional requirements. Each
requirement is built by exactly one task. T-19 shows up against three of them a
second time because it tests them rather than builds them. The four tasks in E7
are the DevOps work that this project is actually marked on.

---

## 2. Version control and branching strategy

### 2.1 Why this choice matters

Version control is not just somewhere to keep the code. The branching strategy
decides when code gets integrated, when it gets reviewed, and whether the CI
pipeline has anything useful to say. If work sits on separate branches for
weeks, the pipeline is testing code that nobody has combined yet. It will keep
reporting success right up to the day everything is merged and breaks.

I looked at three strategies.

### 2.2 Gitflow

Gitflow has two permanent branches, `main` and `develop`, plus three temporary
kinds: `feature/*` off `develop`, `release/*` for stabilising a version, and
`hotfix/*` straight off `main` for production bugs. Code only reaches `main`
through a release branch, so `main` always holds a released version.

**Advantages**

- You can always tell which version is released, because `main` only moves at a release.
- Two versions can be worked on at once. A release branch gets stabilised while new features carry on in `develop`.
- Hotfixes have their own path, so an urgent fix doesn't drag unreleased work into production with it.
- It suits a team big enough that different people are working on different releases.

**Disadvantages**

- Five branch types is a lot of process for one developer with no releases to manage.
- Feature branches are meant to be long-lived, so integration gets put off and conflicts pile up.
- It weakens CI. The pipeline tests `feature/x` and `develop` separately and only tests the combination later.
- Having two permanent branches means deciding which one every change belongs on, and in this project that question has no interesting answer.

### 2.3 Trunk-based development

Everyone commits to one branch, the trunk. If branches are used at all they live
for hours, not days. Unfinished work is hidden behind feature flags instead of
being kept on a branch.

**Advantages**

- Integration happens all the time, so conflicts stay small and you find them immediately.
- The pipeline is always testing the code that will actually ship, because there is only one line of development.
- Almost no overhead. There are no branch types to think about.
- The DevOps literature consistently links it to teams that deliver well.

**Disadvantages**

- In its strict form changes land on the trunk without review, and this project has to demonstrate pull requests.
- Feature flags bring their own complexity and have to be removed afterwards. On a project this size they would cost more than they save.
- An unfinished change on the trunk can leave the application half working, with no branch to keep it off.
- It needs a fast, trustworthy test suite, because there is no branch to catch a mistake on.

