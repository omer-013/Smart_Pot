# ESP8266 Akıllı Saksı

Arduino kodu ile otomatik sulama sistemi.

## Pin Bağlantıları

**Sensörler:**
| Sensör | Pin |
|--------|-----|
| DHT11 | D3 |
| Toprak Nem | A0 |

**Aktüatörler:**
| Aktüatör | Pin |
|----------|-----|
| Su Motoru | D0 |
| Buzzer | D1 |

## Gerekli Kütüphaneler

Arduino IDE'de yükle:
- `DHT11` by Dhruba Saha
- `ESP8266WiFi` (otomatik gelir)

## Kurulum

1. **Arduino IDE'yi aç**
2. **ESP8266 board'unu yükle**
3. **WiFi bilgilerini değiştir:**
   ```cpp
   const char* ssid = "WiFiAdi";
   const char* password = "WiFiSifresi";
   ```
4. **Kodu ESP8266'ya yükle**
5. **Serial Monitor'dan IP adresini al**

## Web Arayüzü

ESP8266'nın IP adresini tarayıcıda aç.
Örnek: `http://192.168.1.100`

## API

- `GET /` - Web sayfası
- `GET /data` - JSON sensör verileri

JSON formatı:
```json
{
  "toprak_nem": 250,
  "sicaklik": 25,
  "nem": 60,
  "sulama_durumu": false
}
```

## Ayarlar

```cpp
#define MOTOR_HIZ 400    // Motor hızı (0-1023)
#define alarm 100        // Alarm eşiği
```

## Sorun Giderme

**WiFi bağlanmıyor:**
- WiFi bilgilerini kontrol et
- Router'a yaklaş (zayıf sinyal sorunu)
- 2.4GHz ağ kullan (5GHz desteklenmez)

**Sensör okumuyor:**
- Pin bağlantılarını kontrol et
- Sensörü değiştir

---
*Not: Tüm bağlantıları kontrol ettikten sonra çalıştır*
