README.md

# Paddy Disease Prediction App

This mobile application predicts **Blast and False Smut disease** in paddy plants using a **TensorFlow Lite (TFLite) model** converted from an ANN model originally built in R. The app supports **both light and dark themes** and follows the **MVVM architecture** with **Koin for dependency injection**.

## 🚀 Features
- 📌 **Predicts Blast & False Smut disease in paddy crops**
- 🧠 **Integrated TensorFlow Lite model** for on-device predictions (No API calls required)
- 🌤️ Fetches **weather data** from an API & XLSX sheets for predictions
- 🏗️ **MVVM Architecture** for clean and maintainable code
- ⚙️ **Koin for Dependency Injection**
- 🌙🎨 Supports **both Light and Dark themes**

## 📌 Tech Stack
- **Language**: Kotlin
- **Framework**: Android Jetpack (Compose UI)
- **Machine Learning**: TensorFlow Lite
- **Architecture**: MVVM
- **Dependency Injection**: Koin
- **Data Handling**: Weather API 
- **Networking**: Ktor (for fetching weather data)

## 📸 Screenshots

### 🌙🌞 Dark & Light Mode

| Light Mode | Dark Mode |
|------------|-----------|
|<img src="https://github.com/user-attachments/assets/fa5d1f2e-19eb-4fec-9a33-7db4d6fbf42a" height="500" width="240"/>|<img src="https://github.com/user-attachments/assets/1c00dcb7-e8ef-41bb-9b6b-46e6741310dc" height="500" width="240"/>|
|<img src="https://github.com/user-attachments/assets/5d6806d0-647c-4de7-b738-6fac8363518e"  height="500" width="240"/>|<img src="https://github.com/user-attachments/assets/61319c07-d6bd-4e97-8b67-72d8a0c521b9" height="500" width="240"/>|
|<img src="https://github.com/user-attachments/assets/7ca179a9-29cb-47d4-b0da-e7670280040b" height="500" width="240"/>|<img src="https://github.com/user-attachments/assets/b6b66898-34ab-4655-b596-efae353616a1" height="500" width="240"/>|


## 🔧 Setup Instructions
1. Clone the repository:
   ```sh
   git clone https://github.com/Kaviselvan-SJ/Disease-Prediction.git
   cd Disease-Prediction
   ```
2. Open the project in **Android Studio**
3. Sync Gradle & build the project
4. Run the app on an emulator or a real device

## 📸 Screenshots
(Add your app screenshots here)

## 🛠️ Folder Structure
```
Disease-Prediction/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/ (Contains TensorFlow Lite model)
│   │   │   ├── java/com/kavi/diseaseprediction/
│   │   │   │   ├── core/ (Core utilities)
│   │   │   │   |  ├── data/networking
│   │   │   │   |  ├── domain/util
│   │   │   │   |  ├── presentation/util
│   │   │   │   ├── di/ (Koin dependency injection modules)
│   │   │   │   ├── ui/theme/ (Light and Dark theme files)
│   │   │   │   ├── weather/
│   │   │   │   │   ├── data/ (Data layer, repositories)
│   │   │   │   │   ├── mappers/ 
│   │   │   │   │   ├── networking/ (API calls using Ktor)
│   │   │   │   │   │   ├── dto/ 
│   │   │   │   │   ├── domain/ (Business logic and use cases)
│   │   │   │   │   ├── presentation/ (UI and ViewModel layer)
│   │   │   │   │   │   ├── models/ (Data models)
│   │   │   │   │   │   ├── weather_List/ (Weather-related components)
│   │   │   │   │   │   │   ├── components/
│   │   │   │   ├── MainActivity.kt (Main UI entry point)
│   │   │   │   ├── PredictionApp.kt (Application class for setup)
│   │   │   │   ├── TFLiteModelInterpreter.kt (Handles TFLite model execution)
```

## 🛠️ Future Improvements
- ✅ Enhance model accuracy with more training data
- ✅ UI/UX improvements
- ✅ Extend the model to detect more paddy diseases
- ✅ Implement image recognition for disease detection

---

⭐ Feel free to contribute or report issues!  
