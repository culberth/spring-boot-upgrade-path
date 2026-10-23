# Upgrade path: every change, hop by hop

This lists each pull request that moved `catalog-service` from Spring Boot 3.0 to 4.2, in the order they were merged. Every pull request description explains what changed, why, what broke without it, and how it was verified.

**Change types** (also used as PR labels):

| Label | Meaning |
|---|---|
| `carry-forward` | Brings the previous epic's finished code into the new epic |
| `prepare` | Works on the *old* version; done first so the bump doesn't break |
| `version-bump` | Changes the Spring Boot version, plus the smallest change needed to stay green |
| `breaking-change` | Required: build, tests, or runtime fail without it |
| `deprecation` | Clears a deprecation before it turns into a removal |
| `behavior-change` | Adapts to a changed default or behavior |
| `new-feature` | Optional: adopts a capability new in that version |

Every epic ends with `mvn verify` passing and **no deprecation warnings** with `-Xlint:deprecation`.

---

## 3.0 baseline: [`epic/3.0`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.0)

| PR | Type | Change |
|---|---|---|
| [#1](https://github.com/culberth/spring-boot-upgrade-path/pull/1) | new-feature | Add `catalog-service` on Spring Boot 3.0.13, written the way a typical 3.0 codebase looked |

## 3.0 → 3.1: [`epic/3.1`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.1), [compare](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.0...epic/3.1)

| PR | Type | Change | Lesson |
|---|---|---|---|
| [#2](https://github.com/culberth/spring-boot-upgrade-path/pull/2) | carry-forward | Bring in the 3.0 code | |
| [#3](https://github.com/culberth/spring-boot-upgrade-path/pull/3) | prepare | Remove `mockito-inline` | The 3.1 BOM stops managing it, so the POM won't resolve after the bump |
| [#4](https://github.com/culberth/spring-boot-upgrade-path/pull/4) | prepare, deprecation | Security lambda DSL with `authorizeHttpRequests` | `authorizeRequests()` is already deprecated on 3.0. Also fixes a hidden bug: anonymous error responses returned 401 instead of the real status |
| [#5](https://github.com/culberth/spring-boot-upgrade-path/pull/5) | version-bump | 3.0.13 → **3.1.12** | A one-line bump because the prepare work came first |
| [#6](https://github.com/culberth/spring-boot-upgrade-path/pull/6) | behavior-change | Drop `maven.compiler.source/target` | The parent now sets only `maven.compiler.release` |

## 3.1 → 3.2: [`epic/3.2`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.2), [compare](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.1...epic/3.2)

| PR | Type | Change | Lesson |
|---|---|---|---|
| [#7](https://github.com/culberth/spring-boot-upgrade-path/pull/7) | carry-forward | Bring in the 3.1 code | |
| [#8](https://github.com/culberth/spring-boot-upgrade-path/pull/8) | version-bump | 3.1.12 → **3.2.12** | The tests pass, but the Docker image fails to start |
| [#9](https://github.com/culberth/spring-boot-upgrade-path/pull/9) | breaking-change | `JarLauncher` moved to `org.springframework.boot.loader.launch` | Test the packaged artifact, not just the tests. Don't fall back to the `CLASSIC` loader |
| [#10](https://github.com/culberth/spring-boot-upgrade-path/pull/10) | new-feature | Availability endpoint built on `RestClient` | Start the `RestTemplate` exit here. This client needs nothing in 4.2 |

## 3.2 → 3.3: [`epic/3.3`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.3), [compare](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.2...epic/3.3)

| PR | Type | Change | Lesson |
|---|---|---|---|
| [#11](https://github.com/culberth/spring-boot-upgrade-path/pull/11) | carry-forward | Bring in the 3.2 code | |
| [#12](https://github.com/culberth/spring-boot-upgrade-path/pull/12) | version-bump | 3.2.12 → **3.3.13** | The H2 tests pass, but production on PostgreSQL fails: `Unsupported Database` |
| [#13](https://github.com/culberth/spring-boot-upgrade-path/pull/13) | breaking-change | Add `flyway-database-postgresql` | Flyway 10 split database support into modules. Tests on H2 can't catch this |
| [#14](https://github.com/culberth/spring-boot-upgrade-path/pull/14) | deprecation | `@RestControllerEndpoint` → `@Endpoint` + `@ReadOperation` | |
| [#15](https://github.com/culberth/spring-boot-upgrade-path/pull/15) | deprecation | `-Djarmode=layertools` → `-Djarmode=tools` | Removed in 4.1, and nothing in the Maven build exercises the Dockerfile |

## 3.3 → 3.4: [`epic/3.4`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.4), [compare](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.3...epic/3.4)

| PR | Type | Change | Lesson |
|---|---|---|---|
| [#16](https://github.com/culberth/spring-boot-upgrade-path/pull/16) | carry-forward | Bring in the 3.3 code | |
| [#17](https://github.com/culberth/spring-boot-upgrade-path/pull/17) | prepare, behavior-change | `@Valid` on nested `@ConfigurationProperties` | Without it, invalid config starts **silently** on 3.4 |
| [#18](https://github.com/culberth/spring-boot-upgrade-path/pull/18) | version-bump | 3.3.13 → **3.4.13** | Six deprecation warnings marked for removal, and the actuator keys are flagged |
| [#19](https://github.com/culberth/spring-boot-upgrade-path/pull/19) | deprecation | `@MockBean` → `@MockitoBean` | Can't be declared on `@Configuration` classes |
| [#20](https://github.com/culberth/spring-boot-upgrade-path/pull/20) | deprecation | Actuator `enabled` → `access` | The migrator can't map these keys for you. It also misses custom endpoints |
| [#21](https://github.com/culberth/spring-boot-upgrade-path/pull/21) | deprecation | `RestTemplateBuilder.setConnectTimeout` → `connectTimeout` | Not in the release notes. `-Xlint:deprecation` found it |
| [#22](https://github.com/culberth/spring-boot-upgrade-path/pull/22) | behavior-change | Graceful shutdown is now the default | Tune `timeout-per-shutdown-phase` against your orchestrator's grace period |

## 3.4 → 3.5: [`epic/3.5`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/3.5), [compare](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.4...epic/3.5)

| PR | Type | Change | Lesson |
|---|---|---|---|
| [#23](https://github.com/culberth/spring-boot-upgrade-path/pull/23) | carry-forward | Bring in the 3.4 code | |
| [#24](https://github.com/culberth/spring-boot-upgrade-path/pull/24) | prepare, breaking-change | `@Async("taskExecutor")` → `applicationTaskExecutor` | Compiles and starts fine, then **fails the request with a 500** |
| [#25](https://github.com/culberth/spring-boot-upgrade-path/pull/25) | version-bump | 3.4.13 → **3.5.16** | The last 3.x line and the required launch point for 4.0 |
| [#26](https://github.com/culberth/spring-boot-upgrade-path/pull/26) | deprecation | `spring.mvc.converters.*` → `spring.http.converters.*` | Removed properties are silently ignored. Run the migrator |
| [#27](https://github.com/culberth/spring-boot-upgrade-path/pull/27) | prepare, deprecation | Executable jar launch script → systemd unit | Change the deployment on 3.5 so 4.0 doesn't change the framework and the deployment at once |

## 3.5 → 4.0: [`epic/4.0`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/4.0), [compare](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/3.5...epic/4.0)

| PR | Type | Change | Lesson |
|---|---|---|---|
| [#28](https://github.com/culberth/spring-boot-upgrade-path/pull/28) | carry-forward | Bring in the 3.5 code | |
| [#29](https://github.com/culberth/spring-boot-upgrade-path/pull/29) | version-bump, breaking-change | 3.5.16 → **4.0.9** with the *classic* starters, package relocations, a temporary Jackson 2 setup, and `@AutoConfigureTestRestTemplate` | The guide's low-risk sequence: compile on the old classpath first |
| [#30](https://github.com/culberth/spring-boot-upgrade-path/pull/30) | breaking-change | Jackson 3 | `write-dates-as-timestamps` fails startup. The golden-file test proves the JSON didn't change |
| [#31](https://github.com/culberth/spring-boot-upgrade-path/pull/31) | breaking-change | Modular starters | With only `flyway-core`, **Flyway silently doesn't run** and the tests still pass. The jar drops from 173 to 103 dependencies |
| [#32](https://github.com/culberth/spring-boot-upgrade-path/pull/32) | deprecation | JUnit 4 → JUnit Jupiter | |
| [#33](https://github.com/culberth/spring-boot-upgrade-path/pull/33) | deprecation | `HttpStatus.UNPROCESSABLE_ENTITY` → `UNPROCESSABLE_CONTENT` | |

## 4.0 → 4.1: [`epic/4.1`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/4.1), [compare](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/4.0...epic/4.1)

| PR | Type | Change | Lesson |
|---|---|---|---|
| [#34](https://github.com/culberth/spring-boot-upgrade-path/pull/34) | carry-forward | Bring in the 4.0 code | |
| [#35](https://github.com/culberth/spring-boot-upgrade-path/pull/35) | version-bump | 4.0.9 → **4.1.2** | One line: every 4.1 removal was already handled on 4.0 |
| [#36](https://github.com/culberth/spring-boot-upgrade-path/pull/36) | deprecation | `RestTemplateBuilder.rootUri` → `baseUri` | Not in the guide. The compiler found it |

## 4.1 → 4.2 (RC1): [`epic/4.2`](https://github.com/culberth/spring-boot-upgrade-path/tree/epic/4.2), [compare](https://github.com/culberth/spring-boot-upgrade-path/compare/epic/4.1...epic/4.2)

> 4.2 isn't GA yet. This epic targets **4.2.0-RC1**, which is on Maven Central. Recheck it against the final release notes.

| PR | Type | Change | Lesson |
|---|---|---|---|
| [#37](https://github.com/culberth/spring-boot-upgrade-path/pull/37) | carry-forward | Bring in the 4.1 code | |
| [#38](https://github.com/culberth/spring-boot-upgrade-path/pull/38) | version-bump | 4.1.2 → **4.2.0-RC1** | `RestTemplate` and `TestRestTemplate` are deprecated for removal |
| [#39](https://github.com/culberth/spring-boot-upgrade-path/pull/39) | deprecation | `IsbnLookupClient`: `RestTemplate` → `RestClient` | A per-client request factory replaces the one `MockRestServiceServer` installs, so the test moved to a real in-process server |
| [#40](https://github.com/culberth/spring-boot-upgrade-path/pull/40) | deprecation | `TestRestTemplate` → `RestTestClient` | |

---

## Things only a runtime check caught

The test suite passed for each of these. They showed up only when the packaged app was run the way production runs it:

| Hop | Problem | Caught by |
|---|---|---|
| 3.2 | Docker image: `ClassNotFoundException: org.springframework.boot.loader.JarLauncher` | `docker run` |
| 3.3 | `FlywayException: Unsupported Database: PostgreSQL 16` | `prod` profile against PostgreSQL |
| 4.0 | Flyway not running at all with `flyway-core` alone | Reading the startup log for the migration line |

## Things that fail silently

| Hop | Problem | Caught by |
|---|---|---|
| 3.4 | Nested config validation no longer applied without `@Valid` | `CatalogPropertiesTests.failsFastWhenNestedBaseUrlIsMissing` |
| 3.5 | `@Async("taskExecutor")` fails at first call, not at startup | `CatalogApplicationTests` exercising the async path |
| 3.5 | Renamed properties are ignored after removal | `spring-boot-properties-migrator` |
| 4.0 | Jackson 3 could change the JSON format | Golden-file `BookResponseJsonTests` |
