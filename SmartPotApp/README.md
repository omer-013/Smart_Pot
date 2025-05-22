# Android Akıllı Saksı Uygulaması

ESP8266'dan sensör verilerini görüntüleyen Android uygulaması.

## Teknoloji

- Kotlin
- Jetpack Compose
- Retrofit (HTTP istekleri)
- MVVM mimarisi

## Kurulum

1. **Android Studio'yu aç**
2. **Projeyi import et**
3. **ESP8266 IP adresini güncelle:**
   ```kotlin
   // SensorRepository.kt dosyasında
   .baseUrl("http://192.168.1.100/") // Kendi IP'nizi yazın
   ```
4. **Uygulamayı derle ve çalıştır**

## Özellikler

- 5 saniyede bir otomatik güncelleme
- Renk kodlu uyarılar:
  - 🟢 İyi durum
  - 🟠 Uyarı
  - 🔴 Kritik
- Gerçek zamanlı veri gösterimi

## Gereksinimler

- İnternet izni
- ESP8266 ile aynı WiFi ağı

## Dosya Yapısı

```
app/src/main/java/com/example/smartpotapp/
├── MainActivity.kt          # Ana aktivite
├── ui/SensorScreen.kt      # Ekran tasarımı
├── api/SensorApi.kt        # API interface
├── repository/             # Veri katmanı
└── viewmodel/              # ViewModel
```

## Kullanım

1. ESP8266'nın çalıştığından emin ol
2. Uygulamayı aç
3. Sensör verilerini görüntüle
4. Renk kodlarına dikkat et

## Sorun Giderme

**Veri gelmiyor:**
- ESP8266 IP adresini kontrol et
- Aynı WiFi ağında ol
- ESP8266'nın çalıştığını kontrol et

**Uygulama çöküyor:**
- İnternet izni var mı kontrol et
- Logcat'te hata mesajlarına bak
