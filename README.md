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

