# Akıllı Sulama Sistemi

ESP8266 ve Android uygulaması kullanarak otomatik bitki sulama projesi.

## Özellikler

- Toprak nemini ölçer ve otomatik sular
- Sıcaklık ve nem takibi
- Android uygulaması ile canlı izleme
- Web arayüzü
- Sesli uyarı sistemi

## Sistem

```
Android App ←→ ESP8266 ←→ Sensörler
```

## Donanım

- ESP8266 NodeMCU
- DHT11 (sıcaklık/nem sensörü)
- Toprak nem sensörü
- Su pompası
- Buzzer

## Yazılım

- Arduino C++ (ESP8266)
- Kotlin + Jetpack Compose (Android)

## Kurulum

1. **ESP8266**: `esp8266/` klasöründeki kodu yükle
2. **Android**: `android/` klasöründeki uygulamayı derle
3. WiFi ayarlarını güncelle
4. IP adresini kontrol et

## Yapılandırma

ESP8266'da WiFi bilgilerini değiştir:
```cpp
const char* ssid = "WiFi_Adi";
const char* password = "WiFi_Sifresi";
```

Android uygulamada IP adresini güncelle:
```kotlin
.baseUrl("http://ESP8266_IP/")
```

## Kullanım

1. ESP8266'yı çalıştır
2. Serial Monitor'den IP adresini al
3. Android uygulamayı başlat
4. Sensör verilerini izle

---
*IoT dersi projesi*
