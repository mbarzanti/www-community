# 🔐 VulnApp – Vulnerable Mobile App for Security Testing

**VulnApp** is a deliberately insecure mobile application built with Flutter. It is designed for **educational purposes**, helping developers, pentesters, and students understand common mobile vulnerabilities by demonstrating them in a real, working app.

> ⚠️ **Disclaimer:** This app is intentionally insecure and should never be used in production. Use only in controlled environments.

---

## 📲 Features

- Simulates real-world mobile security flaws
- Built with modern Flutter (Dart)
- Clean UI with vulnerability showcase dashboard
- Modular structure for easy extension

---

## 🧪 Simulated Vulnerabilities

### 🔐 1. Hardcoded Credentials
🔖 **OWASP M10: Extraneous Functionality**  
The login screen uses hardcoded username (`admin`) and password (`1234`) in the app code.

📛 **Why it’s bad:** Attackers can reverse engineer the APK to extract credentials and gain access.

---

### 💾 2. Insecure Local Storage
🔖 **OWASP M2: Insecure Data Storage**  
Sensitive data like tokens and passwords are saved in plaintext using `SharedPreferences`.

📛 **Why it’s bad:** Data can be easily accessed by rooted devices or forensic tools.

---

### 🪵 3. Logging Sensitive Information
🔖 **OWASP M2: Insecure Data Storage**  
Credentials entered by the user are printed to the device logs.

📛 **Why it’s bad:** Logs can be viewed via `adb logcat`, exposing sensitive data to anyone with USB debugging.

---

### 🌐 4. Insecure Network Communication
🔖 **OWASP M3: Insecure Communication**  
The app uses HTTP instead of HTTPS for API requests.

📛 **Why it’s bad:** Traffic can be intercepted or modified using Man-in-the-Middle (MITM) attacks.

---

### 🔓 5. Broken Authentication Flow
🔖 **OWASP M5: Insufficient Authentication/Authorization**  
Users can access the protected profile screen without validating the token.

📛 **Why it’s bad:** No proper session or access control allows unauthorized access to user data.



## 📊 OWASP Mobile Top 10 Coverage

| Vulnerability                   | OWASP Category                           | Present In           |
|--------------------------------|------------------------------------------|----------------------|
| Hardcoded Credentials          | M10: Extraneous Functionality            | `LoginScreen`        |
| Insecure Local Storage         | M2: Insecure Data Storage                | `InsecureStorage`    |
| Logging Sensitive Info         | M2: Insecure Data Storage                | `LoginScreen`        |
| Insecure Network Communication | M3: Insecure Communication               | `InsecureNetwork`    |
| Broken Authentication Flow     | M5: Insufficient Authentication          | `ProfileScreen`      |

---

## 🚀 Getting Started

```bash
git clone https://github.com/your-username/VulnApp.git
cd VulnApp
flutter pub get
flutter run
