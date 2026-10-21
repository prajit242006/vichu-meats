# Vichu Meats

Local-first Java 21 / Spring Boot website with Thymeleaf, Spring Security, Spring Data JPA and a persistent file-based H2 database. Open one folder in VS Code. No Python, Node, React, external database installation or separate Maven installation is needed.

## Windows 11 setup — step by step

1. Install **JDK 21**, not just a JRE. One source is [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21). Select Windows x64 JDK and enable the installer's JAVA_HOME and PATH options. Restart VS Code after installing. In a new PowerShell terminal run `java -version`; it should say 21. If another Java appears, correct JAVA_HOME and PATH first.
2. Install **VS Code**, then its **Extension Pack for Java** by Microsoft.
3. Right-click the ZIP → Extract All. Open the extracted **vichu-meats** folder in VS Code (File → Open Folder). This is the folder containing `pom.xml` and `mvnw.cmd`. Do not run inside the ZIP.
4. Open Terminal → New Terminal, using PowerShell. Prepare your first owner account in that terminal:

   ```powershell
   $env:ADMIN_USERNAME = "vichuowner"
   $securePassword = Read-Host "Choose an owner password (at least 12 characters)" -AsSecureString
   $env:ADMIN_PASSWORD = [System.Net.NetworkCredential]::new("", $securePassword).Password
   ```

   The username must be 3–50 letters, numbers, dots, underscores or hyphens. Choose your own password. There is no default password. Never put credentials in Git, screenshots or source files.
5. Run:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   The first run downloads Maven and Java dependencies, so it requires internet and can take several minutes. The wrapper JAR is included. Later runs reuse cached downloads. If Windows says Java is missing, return to step 1. If a firewall/proxy blocks Maven Central, allow the official Maven download domain using your network administrator's instructions.
6. Wait for the application to start. Open **http://localhost:8080**.
7. Open **http://localhost:8080/admin** and log in with the credentials you chose. The initial owner is created only when the admin table is empty. Later starts do not reset the stored password. If you start without credentials, the public site still works, but no owner can sign in; stop it and repeat steps 4–5.
8. Open **Products**, edit the starter products and add real prices to each option. Blank price means **Contact for price**; no zero-price checkout is allowed. Milk starts with 100 ml and 200 ml options. Add chicken weights and egg packs as individual options. Use complete option prices: a “500 g” chicken option has the price for 500 g, not an automatic per-kg conversion. Clear the preview label only after owner review. Configure contact details and any delivery areas/payment preferences under Settings.
9. Stop with **Ctrl+C** in the terminal. Restart from the same project folder using the command in step 5. Your products and orders remain in `data/`. After the first successful setup, you can remove the password from your terminal environment:

   ```powershell
   Remove-Item Env:ADMIN_PASSWORD -ErrorAction SilentlyContinue
   Remove-Item Env:ADMIN_USERNAME -ErrorAction SilentlyContinue
   ```

10. Back up safely: stop the application first, then copy the entire **data** and **uploads** folders to private storage. Keep both together. Restore them while the application is stopped, with the same DB_PASSWORD if configured. These contain customer information. Do not upload them to GitHub. A running H2 database should not be copied as a normal file backup.

## Preview data and real use

`DEMO_DATA` defaults to `true`. On an empty database it creates three visibly labelled preview catalogue products, with **no invented prices**, no contact numbers, no opening hours and no enabled payment or delivery options. All generated orders containing a preview product are marked preview, including in historical records.

For real use, set this before starting:

```powershell
$env:DEMO_DATA = "false"
```

This prevents new preview seeding. It **does not erase or automatically hide existing products**. Review and clear their preview flags (or uncheck visibility), set accurate prices and replace illustrations. Completed value excludes preview orders. Completed counts are separate from requests; no dashboard total represents collected payments.

## Optional environment configuration

| Variable | Default | Purpose |
|---|---|---|
| ADMIN_USERNAME / ADMIN_PASSWORD | none | First empty-database owner setup only |
| DEMO_DATA | true | Seed a labelled preview catalogue if empty |
| DATA_DIR | ./data | H2 database directory (outside target) |
| UPLOAD_DIR | ./uploads | Persistent image directory |
| DB_PASSWORD | empty | Optional local H2 password; set before first database creation and retain for all subsequent starts/backups |
| BIND_ADDRESS | 127.0.0.1 | Local-only binding; do not change for local preview |

There is no password recovery by email. Keep owner credentials safely. For a disposable preview only, stopping and deleting `data/` resets everything, **including all orders**. Never do this to a live business database.

## Checks

```powershell
.\mvnw.cmd test
```

See TEST_REPORT.md for checks actually performed for this delivery. Tests use an isolated in-memory database and test-only customer details. They do not modify the normal `data/` folder.

## Project layout

- `src/main/java/com/vichumeats/{config,controller,dto,entity,repository,service}`
- `src/main/resources/templates` — public and owner Thymeleaf pages
- `src/main/resources/static/{css,js,images}` — all local assets
- `.mvn/wrapper`, `mvnw`, `mvnw.cmd` — Maven wrapper
- `data/`, `uploads/` — runtime folders, deliberately excluded from Git and ZIP

## Current scope

A single local process, order **requests**, boolean availability, integer counts of configurable sized options, no online payments or automatic messages. Currency and deposits use backend BigDecimal. Sheep pricing is a manually entered estimated quote with an explicit owner-confirmed basis. Cart IDs/quantities can survive browser restarts through localStorage; checkout personal fields do not. Server session checkout and confirmation expire; references are shown at submission and visible to the owner. Public order lookup is intentionally absent.

Default artwork is original **illustration placeholder** artwork, not real farm photographs. A phone or WhatsApp number must be configured before remote product-price enquiries are useful. All first-run products require price enquiry until configured.

Public deployment is a separate task. Before it, review current supported dependency/security updates, HTTPS, secure cookies, backups, record retention, account recovery, network-specific rate limiting and a persistent host disk. The included throttle is per process and resets on restart; it is appropriate for a local prototype, not a substitute for deployment protections. This project has not been published.
