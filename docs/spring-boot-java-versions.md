# Minimum Java Version by Spring Boot Release

Source: *Spring Boot 3.0 to 4.2 upgrade guide* (prepared September 26, 2026), "Version map" section. Release types come from the Spring support policy (see below).

| Spring Boot | Release type | Minimum Java | Notes |
|-------------|--------------|--------------|-------|
| 3.0 | Major | 17 | Jakarta EE 10 baseline |
| 3.1 | Minor | 17 | |
| 3.2 | Minor | 17 | Java 21 needed only to use virtual threads |
| 3.3 | Minor | 17 | |
| 3.4 | Minor | 17 | |
| 3.5 | Minor, **long-term support** (last 3.x minor) | 17 | Enterprise support until mid-2032 |
| 4.0 | Major | 17 | Java 25 supported |
| 4.1 | Minor | 17 | jOOQ users need Java 21 (jOOQ 3.20+ requires it) |
| 4.2 (M2) | Minor (milestone, not GA) | 17* | |

\* The 4.2 milestones haven't announced any change to the Java baseline. Check this against the final 4.2 release notes.

## Support policy

The open-source Spring Boot project has no "LTS" label. Its support policy works like this:

- **Every release:** 13 months of free (OSS) support, rounded to the end of the month. A commercial subscription adds 12 more months.
- **Last minor release of each major line (2.7, 3.5):** five more years of commercial support, for about seven years in total. This is Spring's long-term support release. A February 2025 policy update applied it to 3.5, putting its enterprise end of life in mid-2032.
- **4.x:** no long-term support release yet. That will be the last 4.x minor, whenever it ships.

References:
- [Spring support policy updates (Feb 2025)](https://spring.io/blog/2025/02/13/support-policy-updates)
- [Spring Enterprise LTS releases](https://enterprise.spring.io/lts-releases)
