# Which Spring Boot version should we target?

*Research as of October 23, 2026. Support dates change; recheck them before deciding.*

## Recommendation

**Target Spring Boot 4.1 (currently 4.1.2) now, then take the small hop to 4.2 once it reaches GA.**

Spring Boot 3.5 is called long-term support, but that only means something if you pay for it:

- **3.5's free (OSS) support ended June 30, 2026.** The last public release is 3.5.16 (June 25, 2026). Security and bug fixes since then are only available through a commercial Tanzu Spring subscription, which extends 3.5 to June 2032.
- **Without a subscription, 3.5 is already unsupported.** It's in the same position as 3.0–3.4.
- **4.0's free support ends December 31, 2026**, about ten weeks from now, so it isn't a good place to stop either.
- **4.1 is the newest GA release and has the longest free support of any release you can ship today:** OSS until July 31, 2027, and commercial support until July 31, 2028.
- **4.2 isn't GA yet** (4.2.0-RC1, and no GA date has been announced). The 4.1 → 4.2 hop in this repository was small: three pull requests, mostly the `RestTemplate` → `RestClient` migration. Take it once 4.2 ships.

Stopping at 3.5 doesn't avoid the expensive work; it postpones it. In this repository the 3.5 → 4.0 hop is the largest of all (modular starters, Jackson 3, test auto-configuration, JUnit 6), while 4.0 → 4.1 was a one-line version bump. Once you're on 4.x, staying current is cheap.

### When 3.5 is still the right choice

| Situation | Target | Why |
|---|---|---|
| No commercial Spring subscription | **4.1**, then 4.2 | 3.5 and 4.0 get no more (or soon no more) free fixes |
| Subscription, app actively developed | **4.1** | Commercial support also covers 4.1 (to July 2028), and you stay on the line new features and libraries target |
| Subscription, app in maintenance mode or retiring before 2032 | **3.5** is defensible | You pay for patches instead of paying for the 4.0 migration |
| A dependency doesn't support Boot 4 yet (Undertow, a library needing Jackson 2, ...) | 3.5 on a subscription **as a stopgap**, while removing the blocker | Undertow is gone in 4.0. Jackson 2 survives in 4.x only through the deprecated `spring-boot-jackson2` module |
| Need Java 26 or newer | **4.1** (4.0 works too, until its OSS support ends) | 3.5 supports Java 17–25. 4.0.9 and 4.1 support Java 17–27 |

## Support status of every version

Source: [endoflife.date](https://endoflife.date/spring-boot), matching the [Spring support policy](https://spring.io/blog/2025/02/13/support-policy-updates).

| Version | Released | Free OSS support until | Commercial support until | Latest | Status on Oct 23, 2026 |
|---|---|---|---|---|---|
| 3.0 | Nov 2022 | Dec 31, 2023 | Dec 31, 2024 | 3.0.13 | Unsupported |
| 3.1 | May 2023 | Jun 30, 2024 | Jun 30, 2025 | 3.1.12 | Unsupported |
| 3.2 | Nov 2023 | Dec 31, 2024 | Dec 31, 2025 | 3.2.12 | Unsupported |
| 3.3 | May 2024 | Jun 30, 2025 | Jun 30, 2026 | 3.3.13 | Unsupported |
| 3.4 | Nov 2024 | Dec 31, 2025 | Dec 31, 2026 | 3.4.13 | Commercial only, ends in about 10 weeks |
| **3.5** | May 2025 | **Jun 30, 2026** | **Jun 30, 2032** | 3.5.16 | **Commercial only** |
| 4.0 | Nov 2025 | Dec 31, 2026 | Dec 31, 2027 | 4.0.9 | OSS, ends in about 10 weeks |
| **4.1** | Jun 2026 | **Jul 31, 2027** | **Jul 31, 2028** | 4.1.2 | **OSS (newest GA)** |
| 4.2 | not GA | (about 13 months after GA) | (12 more months) | 4.2.0-RC1 | Release candidate |

How it works: every minor release gets 13 months of OSS support, rounded to the end of the month, plus 12 months of commercial support. The **last minor of each major line** (2.7, 3.5) gets five extra years of commercial support. That's what "long-term support" means for Spring Boot, and why the 3.5 commercial date is 2032. There's no free LTS.

Spring Cloud follows the same pattern: release train 2025.0 pairs with Boot 3.5 and 2025.1 with Boot 4.0, and a train's OSS support ends about three months after its Boot version's ([Spring Cloud supported versions](https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions)).

## Comparing the sample app on each epic

The numbers below come from the `catalog-service` sample on each `epic/*` branch of this repository. Versions are from each epic's `docs/dependencies.md` report (for example, [the one on `epic/4.1`](https://github.com/culberth/spring-boot-upgrade-path/blob/epic/4.1/docs/dependencies.md)).

### Platform

| Epic | Spring Framework | Spring Security | Hibernate | Jackson | Tomcat | Java supported |
|---|---|---|---|---|---|---|
| 3.0 | 6.0.14 | 6.0.8 | 6.1.7 | 2.14.3 | 10.1.16 | 17–21 |
| 3.1 | 6.0.21 | 6.1.9 | 6.2.25 | 2.15.4 | 10.1.24 | 17–21 |
| 3.2 | 6.1.15 | 6.2.8 | 6.4.10 | 2.15.4 | 10.1.33 | 17–23 |
| 3.3 | 6.1.21 | 6.3.10 | 6.5.3 | 2.17.3 | 10.1.42 | 17–23 |
| 3.4 | 6.2.15 | 6.4.13 | 6.6.39 | 2.18.5 | 10.1.50 | 17–24 |
| 3.5 | 6.2.19 | 6.5.11 | 6.6.53 | 2.21.4 | 10.1.55 | **17–25** |
| 4.0 | 7.0.10 | 7.0.8 | 7.2.25 | **3.1.7** | **11.0.27** | **17–27** |
| 4.1 | 7.0.10 | 7.1.2 | 7.4.13 | 3.1.7 | 11.0.27 | 17–27 |
| 4.2 (RC1) | 7.1.0-RC1 | 7.2.0-RC1 | 7.4.13 | 3.1.7 | 11.0.27 | 17–27 |

The Java ranges are for the latest patch of each line, from the official system requirements ([3.0](https://docs.spring.io/spring-boot/docs/3.0.13/reference/html/getting-started.html), [3.1](https://docs.spring.io/spring-boot/docs/3.1.12/reference/html/getting-started.html), [3.2](https://docs.spring.io/spring-boot/docs/3.2.12/reference/html/getting-started.html), [3.3](https://docs.spring.io/spring-boot/3.3/system-requirements.html), [3.4](https://docs.spring.io/spring-boot/3.4/system-requirements.html), [3.5](https://docs.spring.io/spring-boot/3.5/system-requirements.html), [4.0](https://docs.spring.io/spring-boot/4.0/system-requirements.html), [4.1](https://docs.spring.io/spring-boot/system-requirements.html)). 4.2 isn't GA, so its range is assumed to match 4.1. Later patches of a still-supported line can raise the upper bound: 4.0.9 supports Java 27, for example. The minimum is Java 17 throughout.

### Code and packaging

| Epic | Main code (lines) | Test code (lines) | Tests | Libraries in jar | Jar size | JDK modules needed |
|---|---|---|---|---|---|---|
| 3.0 | 378 | 270 | 16 | 82 | 51.3 MB | 15 |
| 3.1 | 379 | 275 | 17 | 82 | 54.8 MB | 15 |
| 3.2 | 425 | 342 | 19 | 82 | 56.2 MB | 15 |
| 3.3 | 425 | 342 | 19 | 84 | 59.1 MB | 14 |
| 3.4 | 426 | 342 | 19 | 83 | 64.5 MB | 14 |
| 3.5 | 426 | 342 | 19 | 82 | 65.4 MB | 14 |
| 4.0 | 428 | 347 | 20 | 103 | 69.5 MB | 14 |
| 4.1 | 428 | 347 | 20 | 100 | 64.5 MB | 14 |
| 4.2 | 430 | 406 | 21 | 100 | 64.8 MB | 14 |

The application code barely changed across nine versions: 378 → 430 lines, and the growth is mostly the availability feature added on 3.2. Almost all upgrade work was configuration, tests and build files. The library count rises at 4.0 because Spring Boot split into one module per technology, but the jar stays about the same size.

### Runtime

Each epic's jar was started three times in the same container (`eclipse-temurin:17-jre`, 2 CPUs, 1 GB memory, H2 database). The table shows medians. After 20 inserts and 1,000 warm-up requests, 500 `GET /api/books` requests were timed.

| Epic | Startup (Spring) | Memory after warm-up | Median request |
|---|---|---|---|
| 3.0 | 8.13 s | 239 MiB | 0.92 ms |
| 3.1 | 8.41 s | 253 MiB | 0.95 ms |
| 3.2 | **6.42 s** | 264 MiB | 0.95 ms |
| 3.3 | 6.13 s | 260 MiB | 0.95 ms |
| 3.4 | 6.14 s | 259 MiB | 0.99 ms |
| 3.5 | 6.16 s | 264 MiB | 0.97 ms |
| 4.0 | 6.80 s | **288 MiB** | 0.93 ms |
| 4.1 | 6.64 s | 285 MiB | 0.94 ms |
| 4.2 | 6.14 s | 286 MiB | 0.95 ms |

What the numbers show:

- **Startup improved by about 24% at 3.2** (8.1 s → 6.4 s) and has stayed around 6–7 s since. 3.2 rewrote the nested-jar loader, which is the change that broke the Dockerfile in [#9](https://github.com/culberth/spring-boot-upgrade-path/pull/9).
- **4.0 starts about 0.6 s slower than 3.5 and uses about 25 MiB (10%) more memory.** 4.2 is back to 3.5's startup time, but the memory increase remains. For a small app this is noise; for large fleets, measure your own app.
- **Request latency is flat** (0.92–0.99 ms, within measurement noise). For this kind of CRUD workload, performance is not a reason to choose any version over another.

These numbers come from one small app on one machine (Docker Desktop on Windows). Treat them as the direction of change, not as benchmarks.

## What each version offers

New capabilities per release, from the [upgrade guide](spring-boot-3-to-4.2-upgrade-guide.docx) and the Spring Boot release notes. **Bold** marks items with a clear payoff for a typical REST service like this one.

| Version | Notable capabilities | Used in this repository |
|---|---|---|
| 3.1 | **Testcontainers `@ServiceConnection`**, Docker Compose support for local development, SSL bundles, Spring Authorization Server starter | — |
| 3.2 | **Virtual threads** (Java 21), **`RestClient`**, `JdbcClient`, faster nested-jar loader, CRaC, SSL hot reload | `RestClient` ([#10](https://github.com/culberth/spring-boot-upgrade-path/pull/10)); faster startup measured above |
| 3.3 | Class Data Sharing support, `@Observed`/`@SpanTag` tracing, SBOM actuator endpoint, `tools` jar mode | `tools` jar mode ([#15](https://github.com/culberth/spring-boot-upgrade-path/pull/15)) |
| 3.4 | **Structured JSON logging**, `MockMvcTester`, actuator access model, **graceful shutdown by default**, `@MockitoBean`, `ClientHttpRequestFactoryBuilder` | access model, `@MockitoBean`, graceful shutdown ([#19](https://github.com/culberth/spring-boot-upgrade-path/pull/19)–[#22](https://github.com/culberth/spring-boot-upgrade-path/pull/22)) |
| 3.5 | `@ServletRegistration`/`@FilterRegistration`, `env:` config import, SSL for service connections, **Java 25 support** (from 3.5.5), commercial support to 2032 | — |
| 4.0 | **Modular Spring Boot** (only what you use), **HTTP service clients** (`@HttpExchange` + `@ImportHttpServices`), **API versioning**, `@Retryable`/`@ConcurrencyLimit` built into Spring Framework, JSpecify null safety, Jackson 3, Jakarta EE 11, `RestTestClient`, OpenTelemetry starter | modular starters ([#31](https://github.com/culberth/spring-boot-upgrade-path/pull/31)), Jackson 3 ([#30](https://github.com/culberth/spring-boot-upgrade-path/pull/30)), `RestTestClient` ([#40](https://github.com/culberth/spring-boot-upgrade-path/pull/40)) |
| 4.1 | **gRPC** server and client support, **SSRF protection** (`InetAddressFilter`) for HTTP clients, `@Async` trace propagation, OpenTelemetry improvements, lazy JDBC connection fetch, Spock support restored | — |
| 4.2 (RC) | AMQP 1.0, OpenTelemetry semantic conventions, configurable MDC keys, LDAPS via SSL bundles, buildpack image caches; **`RestTemplate` deprecated** | `RestClient` everywhere ([#39](https://github.com/culberth/spring-boot-upgrade-path/pull/39)) |

## Upgrade cost, measured in this repository

| Hop | Pull requests (excluding carry-forward, dependency reports and `.gitattributes`) | Breaking changes | Effort |
|---|---|---|---|
| 3.0 → 3.1 | 4 | 0 | Small |
| 3.1 → 3.2 | 3 | 1 (Docker launcher) | Small |
| 3.2 → 3.3 | 4 | 1 (Flyway PostgreSQL module) | Small |
| 3.3 → 3.4 | 6 | 0 (one silent behavior change) | Medium |
| 3.4 → 3.5 | 4 | 1 (`@Async` executor name) | Small |
| **3.5 → 4.0** | **5** | **3** (Jackson 3, modular starters, test auto-config) | **Large** |
| 4.0 → 4.1 | 2 | 0 | Trivial (one-line bump) |
| 4.1 → 4.2 | 3 | 0 (deprecations only) | Small |

From 3.5, going to 4.1 costs the 4.0 hop plus a trivial 4.1 hop. Going to 3.5 costs nothing today, but every year on 3.5 adds subscription cost, and the 4.0 hop still has to happen before 2032. Meanwhile the libraries you depend on increasingly target Boot 4.

## Sources

- [Spring Boot support dates (endoflife.date)](https://endoflife.date/spring-boot)
- [Spring support policy updates, February 2025](https://spring.io/blog/2025/02/13/support-policy-updates)
- [Spring Enterprise long-term support](https://enterprise.spring.io/lts-releases)
- [Spring Boot 3.5 system requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html)
- [Spring Boot 4.1 system requirements](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring Boot 4.2.0-M1 announcement](https://spring.io/blog/2026/08/20/spring-boot-4-2-0-M1-available-now/)
- [Spring Cloud supported versions](https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions)
- [Spring Boot 3.0 to 4.2 upgrade guide](spring-boot-3-to-4.2-upgrade-guide.docx) and the per-hop pull requests in [upgrade-path.md](upgrade-path.md)
