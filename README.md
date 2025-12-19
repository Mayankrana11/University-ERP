# UNIVERSITY ERP – HOW TO RUN GUIDE

---

## 1. Prerequisites

- **Java JDK 17** or higher  
- **MySQL Server** (running on `localhost:3306`)  
- All required external libraries are already included in the `/lib` folder  

---

## 2. CRITICAL: Database Configuration

Before running the application, you **must** ensure the database credentials match your local MySQL setup.

### Steps:
1. Navigate to:
   ```
   src/edu/univ/erp/data/DatabaseConnection.java
   ```
2. Open the file and update the following lines with **your MySQL credentials**:

   ```java
   private static final String USER = "root";        // Change if needed
   private static final String PASS = "YOUR_PASSWORD"; // ⚠️ CRITICAL: change this
   ```

---

## 3. Database Setup (Required)

Run the following SQL scripts in **this exact order** using MySQL Workbench or any MySQL client:

1. `sql/auth_db_setup.sql`  
   → Creates the authentication database  

2. `sql/erp_db_setup.sql`  
   → Creates the ERP database  

3. `sql/seed_data.sql`  
   → Inserts test users and initial data  

---

## 4. How to Compile & Run

Open a terminal in the **main project folder**.

---

### A. Windows (Command Prompt / PowerShell)

#### Step 1: Compile
```powershell
javac -cp "lib/*" -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

#### Step 2: Run
```powershell
java -cp "lib/*;out" edu.univ.erp.App
```

---

### B. macOS / Linux

#### Step 1: Compile
```bash
javac -d out -cp "lib/*:src" src/edu/univ/erp/App.java
```

#### Step 2: Run
```bash
java -cp "lib/*:out" edu.univ.erp.App
```

---

## 5. Default Login Credentials

> **Note:** Credentials are provided for testing and evaluation purposes.

### Test Accounts

| Role        | Username      | Password     | Notes |
|------------|---------------|--------------|-------|
| Admin      | admin1        | admin123     | Full system access |
| Instructor | inst1         | inst123      | Teaches Data Structures |
| Instructor | MAYANK RANA   | MAYANK123    | Teaches Discrete Maths |
| Student    | student1     | student123   | Enrolled student |
| Student    | student2     | student2123  | Enrolled student |
| Student    | student3     | student3123  | Enrolled student |
| Student    | Mukul        | password     | Enrolled student |

---

## 🔐 Important Notes

- Ensure MySQL service is **running** before launching the application  
- Incorrect database credentials will prevent the application from starting  
- All passwords are **hashed internally** using BCrypt  

---

## ✅ You’re Ready to Go!

Once the database is set up and credentials are configured, the ERP system should launch successfully with the login screen.

For detailed system design, workflows, and testing:
- Refer to `Resources/University ERP System report.pdf`
- Watch `Resources/Demo.mp4`
