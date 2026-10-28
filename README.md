# 🥩 Vichu Meats – Online Meat Ordering System

Welcome to **Vichu Meats**! 🛒

Vichu Meats is a web-based meat ordering application built using **Java 21 and Spring Boot**. It provides a customer-facing website and a separate admin portal for managing the product catalogue and order requests.

## 🌐 Live Demo

🛍️ **Customer Website:**  
👉 https://vichu-meats.onrender.com/

🔐 **Admin Login Portal:**  
👉 https://vichu-meats.onrender.com/login


> ℹ️ The application is hosted on Render's free instance. After inactivity, the first request may take around 50 seconds or longer to load.

---

## ✨ Features

### 🛒 Customer Features
- 🥩 Browse the available product catalogue.
- 📋 View product details and available options.
- 💰 Check configured prices or contact the owner for a quote.
- 🛍️ Add available products to the cart.
- 📦 Submit order requests through the website.

### 🛡️ Admin Features
- 🔑 Separate admin login portal.
- 📝 Manage product information and catalogue options.
- 💵 Configure product prices.
- 📊 Review customer order requests.
- 📦 Manage product availability and stock settings.
- ⚙️ Configure supported business settings and contact details.

*Available actions depend on the features enabled in the deployed application.*

---

## 💻 Technology Stack

| Technology | Purpose |
|---|---|
| ☕ Java 21 | Programming language |
| 🍃 Spring Boot 3.5.7 | Backend application framework |
| 🎨 Thymeleaf | Server-rendered web pages |
| 🔐 Spring Security | Authentication and access control |
| 🗄️ Spring Data JPA | Database access |
| 🧩 Hibernate | ORM |
| 💾 H2 Database | File-based database |
| 🛠️ Maven | Build and dependency management |
| 🚀 Render | Application hosting |

---

## 🚀 Run the Project Locally

### Prerequisites
- Java Development Kit (JDK) 21
- Visual Studio Code
- Extension Pack for Java (Microsoft)
- Internet access for the initial dependency download

### Step 1: Clone the Repository

Open PowerShell or a terminal and run:

```powershell
git clone https://github.com/prajit242006/vichu-meats.git
cd vichu-meats
```

Alternatively, download the repository as a ZIP from GitHub and extract it.

### Step 2: Open in VS Code

Open the extracted `vichu-meats` project folder in Visual Studio Code. Make sure the folder contains `pom.xml` and `mvnw.cmd`.

Check your Java version:

```powershell
java -version
```

Use Java 21.

### Step 3: Configure the Initial Admin Account

For a fresh local database, open PowerShell in the project directory and run:

```powershell
$env:ADMIN_USERNAME = "vichuowner"
$securePassword = Read-Host "Enter an owner password (at least 12 characters)" -AsSecureString
$env:ADMIN_PASSWORD = [System.Net.NetworkCredential]::new("", $securePassword).Password
```

Choose your own strong password. Do not publish credentials in GitHub or share them in screenshots.

### Step 4: Start the Application

```powershell
.\mvnw.cmd spring-boot:run
```

Wait for Spring Boot to finish starting.

### Step 5: Open the Website

🌐 Customer website: http://localhost:8080/

🔐 Admin portal: http://localhost:8080/admin

Use the credentials configured in Step 3.

### Step 6: Configure the Catalogue

After logging in, review the starter products and configure accurate prices, product options, availability, contact details and other required business settings before accepting real orders.

To stop the application, press `Ctrl + C` in the terminal.

---

## ⚙️ Environment Configuration

| Variable | Purpose |
|---|---|
| `ADMIN_USERNAME` | Username for initial owner setup |
| `ADMIN_PASSWORD` | Password for initial owner setup |
| `DEMO_DATA` | Controls preview catalogue seeding |
| `DATA_DIR` | Database storage directory |
| `UPLOAD_DIR` | Uploaded image storage directory |
| `DB_PASSWORD` | Optional H2 database password |
| `BIND_ADDRESS` | Application network binding address |

The application's default configuration is intended for local development. For hosted deployment, configure the appropriate environment variables in the hosting dashboard.

**Security:** Never commit passwords, private customer data, database files or uploaded customer/business data to GitHub.

---

## 🗄️ Database and Data Storage

Vichu Meats uses a file-based H2 database.

- The local database directory defaults to `./data`.
- Uploaded files default to `./uploads`.
- These runtime folders are excluded from Git using `.gitignore`.
- Back up important data securely and avoid copying a running H2 database as an ordinary file backup.

⚠️ **Hosting note:** Render's free web services do not provide persistent disk storage by default. Data stored only in the container filesystem may be lost when the service restarts or is redeployed. Persistent storage or an appropriate external database is recommended before using the application for real customer orders.

---

## 🧪 Run Tests

To run the project's automated tests:

```powershell
.\mvnw.cmd test
```

Refer to `TEST_REPORT.md` for the test results documented in the repository.

---

## 📁 Project Structure

```text
vichu-meats/
├── src/
│   └── main/
│       ├── java/com/vichumeats/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── repository/
│       │   └── service/
│       └── resources/
│           ├── templates/
│           └── static/
├── .mvn/
├── data/                 # Runtime data; excluded from Git
├── uploads/              # Runtime uploads; excluded from Git
├── pom.xml
├── mvnw
├── mvnw.cmd
├── Dockerfile
├── README.md
└── TEST_REPORT.md
```

---

## 🔒 Security and Production Readiness

Before using this application for a real business:

- 🔐 Use a strong, private admin password.
- 🛡️ Verify that admin-only pages and actions require authentication.
- 💾 Configure persistent storage and regular backups.
- 🔒 Review HTTPS, session security and access controls.
- 📋 Test the full customer order and admin management workflow.
- 💳 Do not assume online payments are supported; the current documented scope does not include an integrated payment gateway.
- 📞 Configure valid contact details before inviting customers to make enquiries.

---

## 📌 Project Scope

Vichu Meats is designed for product browsing, configurable catalogue options and customer order requests. Its documented scope includes manually configured prices, product availability and owner-reviewed order handling.

It does not include an integrated online payment gateway or automatic customer messaging in the documented scope. Verify all features against the currently deployed version before relying on them for business operations.

---

## 👨‍💻 Project Information

**Project Name:** Vichu Meats – Online Meat Ordering System  
**Backend:** Java 21 / Spring Boot  
**Database:** H2  
**Hosting:** Render  
**Source Code:** https://github.com/prajit242006/vichu-meats

🚀 **Explore the live website and its admin portal using the links at the top of this README!**
