# Delivery verification

Executed on Linux using Eclipse Temurin **Java 21.0.12.1**, Apache Maven **3.9.11** and the supplied source. This is a local project, not a deployed public website.

## Executed successfully

- Compiled the Java 21 project.
- Ran the bundled Maven Wrapper: `./mvnw --version`.
- Ran `./mvnw -o test` after initial dependency download: **13 tests, 0 failures, 0 errors, 0 skipped**.
- Started the application with the supplied wrapper and `spring-boot:run`; verified HTTP 200 from the homepage (port 8081 was used to isolate this launch check).
- Started the application on port 8080 and exercised real HTTP requests, including first-run environment-based owner creation and login.
- Requested public pages and all image, stylesheet and script references present on those pages; local assets returned HTTP 200.
- Configured a milk option through the owner form, added two items at INR 10.25, and verified an INR 20.50 request total.
- Submitted a real HTTP order request, repeated the same POST and verified exactly one saved order.
- Submitted a whole-sheep enquiry through HTTP.
- Stopped and restarted the application using a file-based H2 database, then verified the order, sheep enquiry and owner account persisted.
- Verified unauthenticated owner order access redirects to login.

The 13 automated integration tests cover milk sizes; BigDecimal totals and enabled deposits; order snapshots and duplicate tokens; unavailable and unpriced variants; changed prices; invalid quantities and tokens; disabled delivery; sheep storage and duplicate tokens; public pages and assets; CSRF rejection; H2 console disabled; owner template rendering; checkout Bean Validation and confirmation; invalid image rejection; browser cart identifier restoration; and login rate limiting.

## Not executed / limits

- Windows 11 and `mvnw.cmd` could not be executed on this Linux host. The Windows launcher is included and calls the same bundled wrapper JAR. Follow README.md for JDK 21 and PowerShell setup.
- No visual desktop/mobile browser pass, screen-reader test or physical mobile-device test was available. CSS includes responsive breakpoints, keyboard focus indicators, reduced-motion handling, a menu toggle and print rules; these have been checked in source, not visually certified.
- Third-party Maps, WhatsApp and social service behaviour was not tested. No message is sent automatically.
- No real customer orders, real owner business prices, payment processing, delivery fulfilment or public deployment was performed.
- Test databases, credentials, downloaded JDK/Maven installations, compiled output and verification scripts are excluded from the ZIP.

Initial checks found and fixed an admin category option missing its HTML value. An initial Mockito agent-attachment restriction was resolved with a test-only subclass mock-maker setting. The final test run is the passing 13-test run described above.
