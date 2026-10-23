# Spring Boot upgrade path: 3.0 → 4.2, by example

This repository shows, one change at a time, what it takes to move a realistic Spring Boot application from **3.0** to **4.2**.
The same sample application, `catalog-service`, exists on every Spring Boot version. Each step it took to get from one version to the next is its own branch and pull request.

This `main` branch only holds the support documents. The code lives on the `epic/*` branches.

## Branching strategy

```
main                         support documents only
 ├── epic/3.0                sample app on Spring Boot 3.0.x
 │    └── dev/3.0/NN-...     one branch per change, merged by pull request
 ├── epic/3.1                sample app on Spring Boot 3.1.x
 │    ├── dev/3.1/00-carry-forward-3.0
 │    ├── dev/3.1/01-...
 │    └── ...
 ├── epic/3.2 … epic/4.2
```

| Branch type | Branched from | Contains |
|---|---|---|
| `main` | — | Documentation only: this README, the upgrade guide, and the Java version table. |
| `epic/<version>` | `main` | The whole sample app on that Spring Boot version. Changes arrive only through pull requests from `dev/<version>/*` branches. |
| `dev/<version>/NN-<change>` | `epic/<version>` | A single change needed to upgrade to `<version>`. `NN` is the order the changes were applied. |

Every epic starts with a `00-carry-forward-<previous>` pull request that merges the previous epic's finished code. The pull requests after it apply the upgrade one change at a time, and each one explains what changed and why.

Within an epic, the order is:

1. **Carry forward** the previous version's code.
2. **Prepare.** Make changes that already work on the old version and remove the risk from the bump (for example, clearing deprecations).
3. **Bump** the Spring Boot version, plus the smallest change needed to compile and pass the tests again.
4. **Follow up.** Clear the new deprecations, adopt changed defaults, and optionally adopt new features.

## How to compare

- **A whole hop:** compare two epics, for example `epic/3.4...epic/3.5`.
- **A single change:** open that change's pull request, filtered by the `boot-<version>` label.
- **Everything from 3.0 to 4.2:** compare `epic/3.0...epic/4.2`.

```bash
git diff epic/3.4 epic/3.5
```

## The sample application

`catalog-service` is a small book catalog REST API. It uses the parts of Spring Boot where most of the upgrade work happens:

| Area | What the app uses |
|---|---|
| Web | Spring MVC REST controllers, Bean Validation, `ProblemDetail` error responses |
| Data | Spring Data JPA, Flyway, H2 (default) and PostgreSQL (`prod` profile) |
| Security | Spring Security with HTTP Basic and a `SecurityFilterChain` |
| Actuator | Health, info, metrics, Prometheus, and a custom `catalogstats` endpoint |
| HTTP clients | `RestTemplate` for ISBN lookup (and `RestClient` from 3.2 on) |
| JSON | Jackson customization and a golden-file JSON contract test |
| Async | `@Async` event listener |
| Config | Validated, nested `@ConfigurationProperties` records |
| Packaging | Layered Docker image and an executable jar |
| Tests | `@WebMvcTest`, `@DataJpaTest`, `@RestClientTest`, `@SpringBootTest`, and one legacy JUnit 4 test |

To run it from any epic branch (Java 17+ and Maven required):

```bash
mvn spring-boot:run
```

## Documents

- [Spring Boot 3.0 to 4.2 upgrade guide (docx)](docs/spring-boot-3-to-4.2-upgrade-guide.docx): what changed in every minor release.
- [Minimum Java version and release type by Spring Boot version](docs/spring-boot-java-versions.md)
