<div align="center">
  <img src="https://img.icons8.com/color/96/000000/android-os.png" alt="Android Logo" width="80"/>
  <h1>AutoHub 🚗🔧</h1>
  <p><strong>A comprehensive Android platform connecting vehicle owners, service providers, and mechanics.</strong></p>

  <p>
    <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
    <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
    <img src="https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase" />
  </p>
</div>

---

AutoHub is a comprehensive Android application designed to bridge the gap between vehicle owners, automotive service providers (Washing Centers, Mechanics), and administrators. It provides a unified platform for booking services, requesting emergency mechanic assistance, tracking expenses, and managing automotive service businesses.

## ✨ Key Features

### 👤 For Vehicle Owners (Users)
*   **Service Booking:** Book car washing services with options for Normal, Doorstep, or Pickup/Return.
*   **Emergency Assistance:** Find and request help from the nearest available mechanics in case of a breakdown.
*   **Expense Tracking:** Track and visualize vehicle-related expenses using interactive charts.
*   **OBD Integration & Geofencing:** Fetch On-Board Diagnostics (OBD) data and manage geofences for your vehicles.
*   **Document Management:** Securely upload and verify essential documents like Aadhar, PAN, Driving License, and RC details.

### 🏢 For Service Owners (Washing Centers)
*   **Business Management:** Register and manage washing center details and services offered.
*   **Appointment Handling:** Accept, manage, and track incoming service requests and appointments.
*   **Slot Management:** Update available time slots dynamically for different service types (Normal, Doorstep, Pickup).
*   **Financial Tracking:** Monitor business expenses and revenue history.

### 🛠️ For Mechanics
*   **Availability Toggle:** Easily switch status to show availability for emergency requests.
*   **Request Management:** Receive, view, and respond to emergency assistance requests from nearby users.
*   **Service History:** Keep track of completed services and past emergency responses.

### 🛡️ For Administrators
*   **Verification System:** Review and verify registered washing centers and service providers to ensure platform quality and trust.

## 🛠️ Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Platform** | <img src="https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Android"/> | Native Android Application |
| **Language** | <img src="https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java"/> | Java Development Kit (Min SDK 24, Target SDK 34) |
| **Backend & DB**| <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black" alt="Firebase"/> | Authentication, Firestore, Realtime DB, Cloud Storage |
| **Payments** | <img src="https://img.shields.io/badge/Razorpay-02042B?style=flat-square&logo=razorpay&logoColor=white" alt="Razorpay"/> | Secure payment gateway integration |
| **Location** | <img src="https://img.shields.io/badge/Google_Maps-4285F4?style=flat-square&logo=googlemaps&logoColor=white" alt="Google Maps"/> | Play Services Location for geofencing & tracking |
| **UI/UX** | <img src="https://img.shields.io/badge/Material_Design-757575?style=flat-square&logo=materialdesign&logoColor=white" alt="Material"/> | Lottie Animations, Glide (Images), MPAndroidChart |

## 🚀 Getting Started

### Prerequisites
*   Android Studio (Latest version recommended)
*   Java Development Kit (JDK) 8 or higher
*   Firebase Project with Authentication, Firestore, Realtime Database, and Storage enabled.

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sanskaraut/Autohub4thSem.git
    ```
2.  **Open the project:**
    Open Android Studio and select `Open an existing Android Studio project`. Navigate to the cloned directory and select the `EDAI-APP` folder.
3.  **Firebase Configuration:**
    *   Add your `google-services.json` file (obtained from your Firebase Console) into the `app/` directory.
4.  **Razorpay Configuration (Optional):**
    *   If testing payments, ensure your Razorpay API keys are correctly set in the corresponding Activities/Fragments or local properties.
5.  **Sync and Build:**
    *   Allow Gradle to sync the project dependencies.
    *   Click on **Run** to build and deploy the app to your emulator or physical device.

## 📱 Screenshots & Visuals
*(Consider adding screenshots of the app's key screens here, such as the Dashboard, Map view for Mechanics, and Expense Charts).*

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the issues page if you want to contribute.

## 📝 License
This project is licensed under the MIT License - see the LICENSE file for details.
