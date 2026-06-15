# UrbanFlora 🌿

UrbanFlora is an AI-powered mobile application designed to help users explore and identify plants in urban environments. Using on-device machine learning, it identifies plant species and tracks your discoveries on an interactive map.

## Features ✨

*   **AI Plant Identification:** High-accuracy plant recognition using TensorFlow Lite and Google ML Kit.
*   **Interactive Discovery Map:** Visualize your plant finds on a map powered by OpenStreetMap (OSMDroid).
*   **Gamified Progress:** Earn XP and unlock badges (Explorer, Rare Finder) as you discover more species.
*   **Personal Library:** Keep a local record of all your discoveries with photos, scientific names, and location data.
*   **Modern UI:** Clean and intuitive interface built with Jetpack Compose and Material 3.
*   **Privacy-Focused:** All data and AI processing happen locally on your device.

## Tech Stack 🛠

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Database:** [Room](https://developer.android.com/training/data-storage/room) (Local Persistence)
*   **AI/ML:** [TensorFlow Lite](https://www.tensorflow.org/lite) & [Google ML Kit](https://developers.google.com/ml-kit)
*   **Maps:** [OSMDroid](https://github.com/osmdroid/osmdroid) (OpenStreetMap)
*   **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
*   **Dependency Management:** Version Catalogs (.toml)


## Setup & Installation 🚀

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Sumeyye-Senturk/UrbanFlora.git
    ```
2.  **Open in Android Studio:**
    Recommended version: Android Studio Ladybug (2024.2.1) or newer.
3.  **Model Assets:**
    Ensure the `flowers_model.tflite` and `labels.txt` files are present in `app/src/main/assets/`.
4.  **Build:**
    Run `./gradlew assembleDebug` to generate a debug APK or use the Run button in Android Studio.

---

# UrbanFlora (Türkçe) 🌿

UrbanFlora, şehir ortamlarındaki bitkileri keşfetmenize ve tanımlamanıza yardımcı olan yapay zeka destekli bir mobil uygulamadır. Cihaz içi makine öğrenimi kullanarak bitki türlerini tanımlar ve keşiflerinizi harita üzerinde işaretler.

## Özellikler ✨

*   **Yapay Zeka Bitki Tanımlama:** TensorFlow Lite ve Google ML Kit kullanarak yüksek doğrulukla bitki teşhisi.
*   **İnteraktif Keşif Haritası:** Keşfettiğiniz bitkileri OpenStreetMap destekli harita üzerinde görüntüleyin.
*   **Oyunlaştırılmış İlerleme:** Yeni türler keşfettikçe XP kazanın ve başarımların (Gezgin, Nadir Bulucu vb.) kilidini açın.
*   **Kişisel Kitaplık:** Keşiflerinizi fotoğrafları, bilimsel adları ve konum bilgileriyle birlikte kaydedin.
*   **Modern Arayüz:** Jetpack Compose ve Material 3 ile oluşturulmuş temiz ve modern tasarım.

