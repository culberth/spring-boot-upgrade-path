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

## The hops

| Hop | Epic | Spring Boot | Pull requests | Compare | Headline |
|---|---|---|---|---|---|
| baseline | [`epic/3.0`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.0) | 3.0.13 | [#1](https://github.com/culberth/spring-boot-upgrade-path/pull/1) | — | The sample app, written the way 3.0 codebases were |
| 3.0 → 3.1 | [`epic/3.1`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.1) | 3.1.12 | [#2–#6](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Aboot-3.1) | [diff](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.0...epic/3.1) | Mockito 5, Security lambda DSL |
| 3.1 → 3.2 | [`epic/3.2`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.2) | 3.2.12 | [#7–#10](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Aboot-3.2) | [diff](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.1...epic/3.2) | New jar loader breaks the Docker image; `RestClient` |
| 3.2 → 3.3 | [`epic/3.3`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.3) | 3.3.13 | [#11–#15](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Aboot-3.3) | [diff](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.2...epic/3.3) | Flyway 10 modules, `@Endpoint`, tools jar mode |
| 3.3 → 3.4 | [`epic/3.4`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.4) | 3.4.13 | [#16–#22](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Aboot-3.4) | [diff](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.3...epic/3.4) | `@MockitoBean`, actuator access model, nested `@Valid` |
| 3.4 → 3.5 | [`epic/3.5`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.5) | 3.5.16 | [#23–#27](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Aboot-3.5) | [diff](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.4...epic/3.5) | `applicationTaskExecutor`, 4.0 prep |
| 3.5 → 4.0 | [`epic/4.0`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/4.0) | 4.0.9 | [#28–#33](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Aboot-4.0) | [diff](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.5...epic/4.0) | Modular Boot, Jackson 3, JUnit 6, test auto-config |
| 4.0 → 4.1 | [`epic/4.1`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/4.1) | 4.1.2 | [#34–#36](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Aboot-4.1) | [diff](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/4.0...epic/4.1) | One-line bump; `baseUri` |
| 4.1 → 4.2 | [`epic/4.2`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/4.2) | 4.2.0-RC1 | [#37–#40](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Aboot-4.2) | [diff](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/4.1...epic/4.2) | `RestTemplate` → `RestClient`, `RestTestClient` |

**[docs/upgrade-path.md](docs/upgrade-path.md)** lists every pull request with its type and the lesson it teaches. It also covers the problems only a runtime check caught, and the ones that fail silently.

> 4.2 isn't GA yet. `epic/4.2` targets 4.2.0-RC1, which shipped after the upgrade guide (written against M2). Recheck it against the final 4.2.0 release notes.

## How to compare

- **A whole hop:** compare two epics, for example [`epic/3.4...epic/3.5`](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.4...epic/3.5).
- **A single change:** open that change's pull request, or filter pull requests by the `boot-<version>` label. Labels such as `breaking-change`, `deprecation` and `prepare` filter by kind of change.
- **Everything from 3.0 to 4.2:** compare [`epic/3.0...epic/4.2`](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.0...epic/4.2).

```bash
git diff origin/epic/3.4 origin/epic/3.5
```

## Dependency reports

Every epic has `docs/dependencies.md`, generated by `scripts/dependency-report.ps1` (PRs [#41–#49](https://github.com/culberth/spring-boot-upgrade-path/pulls?q=is%3Apr+label%3Adocumentation)). It lists:
- every library in the jar with its group, artifact and version (from `mvn dependency:list`)
- the libraries the application's own code uses directly (from `jdeps -summary`)
- the JDK modules the application needs, as a ready-to-run `jlink --add-modules` line (from `jdeps --print-module-deps`)

All epics were generated with the same script and JDK, so comparing the file shows only dependency changes:

```bash
git diff origin/epic/3.5 origin/epic/4.0 -- docs/dependencies.md
```

| Epic | Libraries in the jar | Used directly by app code | JDK modules |
|---|---|---|---|
| 3.0 | 82 | 14 | 15 |
| 3.1 | 82 | 14 | 15 |
| 3.2 | 82 | 14 | 15 |
| 3.3 | 84 | 14 | 14 |
| 3.4 | 83 | 14 | 14 |
| 3.5 | 82 | 14 | 14 |
| 4.0 | 103 | 17 | 14 |
| 4.1 | 100 | 17 | 14 |
| 4.2 | 100 | 17 | 14 |

The jump at 4.0 comes from Spring Boot's modularization: the app now depends directly on modules such as `spring-boot-health`, `spring-boot-jackson` and `spring-boot-security`.

## How each hop was verified

Every pull request was checked locally before merging, and CI (GitHub Actions, Java 17) builds every pull request and epic:

1. `mvn verify` with `-Xlint:deprecation`. Each epic ends with **no deprecation warnings**.
2. `spring-boot-properties-migrator`, added temporarily, to find renamed or removed properties. It's never committed.
3. On packaging and database hops, the runtime artifact: `docker build` plus `docker run` against `/actuator/health`, and the `prod` profile against a real PostgreSQL 16.

## The sample application

`catalog-service` is a small book catalog REST API. It uses the parts of Spring Boot where most of the upgrade work happens:

| Area | What the app uses |
|---|---|
| Web | Spring MVC REST controllers, Bean Validation, `ProblemDetail` error responses |
| Data | Spring Data JPA, Flyway, H2 (default) and PostgreSQL (`prod` profile) |
| Security | Spring Security with HTTP Basic and a `SecurityFilterChain` |
| Actuator | Health, info, metrics, Prometheus, and a custom `catalogstats` endpoint |
| HTTP clients | `RestTemplate` for ISBN lookup until 4.2, then `RestClient`; `RestClient` for warehouse stock from 3.2 on |
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

- [Upgrade path, PR by PR](docs/upgrade-path.md): every change in this repository, grouped by hop.
- [Which version to target](docs/version-recommendation.md): support dates, a comparison of the sample app on every epic (versions, size, startup, memory), and why 4.1 beats stopping at 3.5.
- [Spring Boot 3.0 to 4.2 upgrade guide (docx)](docs/spring-boot-3-to-4.2-upgrade-guide.docx): what changed in every minor release. The pull requests quote it.
- [Minimum Java version and release type by Spring Boot version](docs/spring-boot-java-versions.md)
