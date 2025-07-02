# 🌤️ SkySnap

**SkySnap** is a dynamic, modern weather application for Android built with Kotlin and powered by a RESTful weather API. It provides real-time weather updates with rich UI elements, smooth animations, and live location-based weather cards tailored for different conditions like sunny, rainy, cloudy, and snowy.

## ✨ Features

- 📍 **Auto Location Detection**: Fetches the user’s live location using GPS.
- 🌡️ **Real-Time Weather Data**: Integrates with WeatherAPI to provide current temperature, condition, humidity, wind, and more.
- 🎨 **Dynamic UI Styling**: Backgrounds and cards adapt based on weather conditions (sunny, rainy, cloudy, snowy).
- 🕐 **Hourly Forecast Display**: Visually styled cards with weather icons and color-coded layouts for clarity.
- ☀️ **Feels Like & Sunset Info**: Pulled dynamically from API—nothing hardcoded.

## 🛠️ Tech Stack

- **Kotlin** – Primary language for Android development.
- **Android Studio** – IDE for building and testing.
- **Retrofit** – HTTP client for API integration.
- **Figma** – UI/UX design.
- **MVVM Architecture** – Clean separation of UI and logic.

## 📁 Project Structure

```
SkySnap/
├── MainActivity.kt
├── HourlyAdapter.kt
├── WeatherApiService.kt
├── WeatherResponse.kt
├── WeatherStyleProvider.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   └── item_hourly.xml
│   └── drawable/
│       ├── card_sunny.xml
│       ├── cloud.png
│       └── sunny.png
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- JDK 21+
- An API key from [weatherapi.com](https://www.weatherapi.com)

### Installation

1. Clone the repo:
   ```bash
   git clone https://github.com/Ssk7722/SkySnap.git
   ```
2. Open in Android Studio.
3. Insert your Weather API key in `WeatherApiService.kt`.
4. Run the app on an emulator or device.

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## 📄 License

This project is licensed under the MIT License.

---

> Designed & developed with care to bring weather to your fingertips.

