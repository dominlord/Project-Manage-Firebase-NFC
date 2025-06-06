# Student Management App with NFC and Firebase

An Android application to manage student data using NFC cards and Firebase.

## Main Features

### 🔐 Student Login

Students can log in to access 4 main feature pages:

1. **Home Page**:
   - Scan QR codes to display student information.
   - Look up student scores.

2. **News Page**:
   - View recent news.
   - Read news posted by administrators.

3. **Feedback Page**:
   - Send feedback to Admin via Firebase.
   - Receive feedback from Admin.

4. **Personal Page**:
   - Fill in and view personal information.
   - Send personal information to Admin.
   - Log out of account.

---

### 🛠️ Administrator Login

Administrators have a special account with 4 main pages:

1. **Home Page**:
   - Scan QR codes to view all student information.
   - Post, delete, and search news.
   - Manage student scores.

2. **History Page**:
   - Track history of user information edits.
   - View admin-edit history of student data.
   - Track user attendance.
   - Log out.

3. **Information Page**:
   - View list of students who submitted information.
   - View student-submitted data.
   - Generate QR code from student info.
   - Scan NFC card to:
     - Read data
     - Create data
     - Delete data  
     _(If the device does not support NFC, these features will be disabled)_

4. **Support Page**:
   - Display list of students who sent feedback.
   - View and respond to messages.

## Technologies Used
- **Java**
- **Firebase Firestore** 
- **Firebase Authentication** 
- **NFC (Near Field Communication)** 
- **QR Code Scanner** 
- **Android Studio** 

## Author
- **Hoang Bao Thinh**
