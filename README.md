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
- **Data Handling**: Weather API & XLSX file parsing
- **Networking**: Ktor (for fetching weather data)

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
