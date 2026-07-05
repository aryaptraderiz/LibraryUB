<div align="center">

# 📚 LibraryUB
### Modern Library Management System

A desktop-based library management system built with **Java**, **Object-Oriented Programming (OOP)** principles, and **Google Firebase Firestore** for real-time cloud database storage.

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Firebase](https://img.shields.io/badge/Firebase-Firestore-yellow?style=for-the-badge&logo=firebase)
![OOP](https://img.shields.io/badge/OOP-Java-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

</div>

---

# 📖 About

LibraryUB is a desktop application developed as an Object-Oriented Programming (OOP) final project.

The system simplifies library management by allowing librarians to manage books, members, and borrowing transactions while utilizing Firebase Firestore as cloud storage.

The application emphasizes clean OOP architecture including:

- Inheritance
- Abstraction
- Encapsulation
- Polymorphism
- Interface
- Singleton Pattern
- Aggregation
- Exception Handling

---

# ✨ Features

### 👤 User Management

- Login authentication
- Admin & Student roles
- Password verification
- Notification system

### 📚 Book Management

- Add new books
- Edit book information
- Delete books
- Book categories
- Book status
- Stock management

### 🔍 Search

- Search books by keyword
- Search users
- Search borrowing records

### 📑 Borrowing System

- Borrow books
- Return books
- Due date management
- Fine calculation
- Borrowing history

### ☁ Firebase Integration

- Cloud database using Firestore
- Automatic data synchronization
- Persistent storage

---

# 🏗️ Built With

- Java
- Java Swing
- Firebase Firestore
- Maven
- Object-Oriented Programming

---

# 📂 Project Structure

```
LibraryUB
│
├── model/
│ ├── Buku
│ ├── Pengguna
│ ├── Mahasiswa
│ ├── Admin
│ └── Peminjaman
│
├── service/
│ ├── DataStore
│ ├── FirebaseClient
│ └── SearchService
│
├── util/
│ ├── JsonObject
│ ├── FirebaseException
│ └── Validator
│
├── ui/
│ ├── Login
│ ├── Dashboard
│ ├── BookForm
│ └── BorrowForm
│
└── Main.java
```

---

# 🧠 OOP Concepts Used

| Concept | Implementation |
|----------|---------------|
| Encapsulation | Private attributes with getters/setters |
| Inheritance | Admin and Mahasiswa extend Pengguna |
| Abstraction | Abstract class Pengguna |
| Polymorphism | Overridden methods |
| Interface | Searchable, Notifikasi |
| Aggregation | Peminjaman references Buku & Mahasiswa |
| Singleton | FirestoreClient & DataStore |
| Exception Handling | FirebaseException |

---

# 🔥 Firebase Database

The project uses **Google Firebase Firestore** to store:

- Users
- Books
- Borrowing Transactions

This enables cloud-based data storage instead of relying on local files.

---

# 🚀 Getting Started

## Clone Repository

```bash
git clone https://github.com/aryaptraderiz/LibraryUB.git
```

## Open Project

Open using

- IntelliJ IDEA
- NetBeans
- Eclipse

---

## Configure Firebase

1. Create a Firebase Project.
2. Enable Firestore Database.
3. Download your Firebase credentials.
4. Place the configuration file into the project.
5. Run the application.

---

# 🖥️ Screenshots

> Add your application screenshots here.

Example:

```
images/
│
├── login.png
├── dashboard.png
├── books.png
├── borrowing.png
└── users.png
```

---

# 📊 UML Design

The project is designed using UML including:

- Use Case Diagram
- Class Diagram
- Sequence Diagram
- Activity Diagram

---

# 📚 Learning Objectives

This project was developed to practice:

- Advanced Java Programming
- Object-Oriented Programming
- Software Design
- Cloud Database Integration
- Desktop Application Development

---

# 👨‍💻 Authors

**Arya Putra Aderiz**

Informatics Student

Universitas Bakrie

GitHub:
https://github.com/aryaptraderiz

---

# ⭐ Future Improvements

- QR Code Book Borrowing
- Barcode Scanner
- Email Notifications
- Report Export (PDF)
- Dark Mode
- Multi-user Authentication
- Dashboard Analytics

---

# 📄 License

This project is created for educational purposes as a university final project.

Feel free to fork and learn from it.

---

<div align="center">

### ⭐ If you like this project, don't forget to leave a star!

Made with ❤️ using Java & Firebase

</div>
