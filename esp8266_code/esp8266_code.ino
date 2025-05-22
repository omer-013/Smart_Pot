#include <DHT11.h>
#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

// WiFi ayarları
const char* ssid = "WiFi_Adi";  // WiFi ağınızın adı
const char* password = "WiFi_Sifresi";  // WiFi şifreniz

// Web sunucusu
ESP8266WebServer server(80);

#define alarm 100
#define MOTOR_HIZ 400  // 0-1023 arası değer (512 = %50 hız)

DHT11 dht(D3);
int su_motor = D0;  // PWM pin olarak D0 kullanılacak
int toprak = A0;
int buzzer = D1;

bool sula = false;

// Global değişkenler - sensör verileri
int toprakNem = 0;
int temperature = 0;
int humidity = 0;
unsigned long lastReadTime = 0;
const unsigned long READ_INTERVAL = 2000; // 2 saniyede bir oku

void setup() {
  Serial.begin(9600);
  Serial.println("ESP başlatılıyor...");
  
  // Pin modlarını ayarla
  pinMode(su_motor, OUTPUT);
  pinMode(buzzer, OUTPUT);
  analogWrite(su_motor, 0);  // Motor başlangıçta kapalı
  noTone(buzzer);
  
  WiFi.begin(ssid, password);
  Serial.print("WiFi'ye bağlanılıyor");
  
  int sayac = 0;
  while (WiFi.status() != WL_CONNECTED && sayac < 20) {
    delay(500);
    Serial.print(".");
    sayac++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nWiFi bağlantısı başarılı!");
    Serial.print("ESP'nin IP Adresi: ");
    Serial.println(WiFi.localIP());
  } else {
    Serial.println("\nWiFi bağlantısı başarısız! IP adresi alınamadı.");
  }
  
  // Sunucu başlatılıyor
  server.on("/", HTTP_GET, handleRoot);
  server.on("/data", HTTP_GET, handleData);
  server.begin();
  Serial.println("HTTP sunucusu başlatıldı");
}

void loop() {
  server.handleClient();
  
  // Belirli aralıklarla sensör oku
  unsigned long currentTime = millis();
  if (currentTime - lastReadTime >= READ_INTERVAL) {
    lastReadTime = currentTime;
    readSensors();
    controlSystem();
  }
}

void readSensors() {
  // Toprak nem sensörünü oku
  toprakNem = analogRead(toprak);
  
  // DHT11 sensörünü oku
  int result = dht.readTemperatureHumidity(temperature, humidity);
  
  if (result != 0) {
    // Hata durumunda
    Serial.print("DHT11 Okuma Hatası: ");
    Serial.println(result);
    
    // Hata durumunda son başarılı değerleri kullan veya varsayılan değerler ata
    if (temperature == 0 && humidity == 0) {
      temperature = 25; // Varsayılan sıcaklık
      humidity = 50;    // Varsayılan nem
    }
  } else {
    // Başarılı okuma
    Serial.println("DHT11 verisi başarıyla okundu");
  }
  
  // Seri port çıktısı
  Serial.print("Toprak nem değeri: ");
  Serial.println(toprakNem);
  Serial.print("Sıcaklık: ");
  Serial.print(temperature);
  Serial.println(" °C");
  Serial.print("Nem: ");
  Serial.print(humidity);
  Serial.println(" %");
  Serial.println("-------------------");
}

void controlSystem() {
  int minToprakNem = Hesapla(temperature, humidity);
  
  // Sulama kontrolü
  if (toprakNem < minToprakNem) {
    sula = true;
    analogWrite(su_motor, MOTOR_HIZ);  // Motoru yarı hızda çalıştır
    Serial.println("Sulama başlatıldı (Yarı hız)");
    delay(900);
    analogWrite(su_motor, 0);
    delay(2500);
  } else if (toprakNem >= minToprakNem) {
    sula = false;
  }
  
  // Alarm kontrolü
  if (toprakNem < alarm) {
    tone(buzzer, 440);
    delay(50);
    noTone(buzzer);
  }
  
  Serial.print("Min toprak nem değeri: ");
  Serial.println(minToprakNem);
  Serial.print("Sulama durumu: ");
  Serial.println(sula ? "AÇIK" : "KAPALI");
}

// Ana sayfa için HTML
void handleRoot() {
  String html = "<!DOCTYPE html>";
  html += "<html><head>";
  html += "<meta charset='UTF-8'>";
  html += "<meta name='viewport' content='width=device-width, initial-scale=1.0'>";
  html += "<title>Akıllı Sulama Sistemi</title>";
  html += "<style>";
  html += "body { font-family: Arial, sans-serif; margin: 20px; background-color: #f0f0f0; }";
  html += ".container { max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }";
  html += "h1 { color: #333; text-align: center; }";
  html += ".data { margin: 10px 0; padding: 10px; background-color: #f9f9f9; border-radius: 5px; }";
  html += ".label { font-weight: bold; color: #555; }";
  html += ".value { color: #2196F3; }";
  html += ".status { font-weight: bold; }";
  html += ".status.on { color: #4CAF50; }";
  html += ".status.off { color: #F44336; }";
  html += ".refresh { text-align: center; margin-top: 20px; }";
  html += ".button { background-color: #2196F3; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; text-decoration: none; display: inline-block; }";
  html += ".button:hover { background-color: #1976D2; }";
  html += "</style>";
  html += "</head><body>";
  html += "<div class='container'>";
  html += "<h1>Akıllı Sulama Sistemi</h1>";
  html += "<div id='sensorData'>Yükleniyor...</div>";
  html += "<div class='refresh'>";
  html += "<button class='button' onclick='refreshData()'>Verileri Yenile</button>";
  html += "</div>";
  html += "</div>";
  html += "<script>";
  html += "function refreshData() {";
  html += "  fetch('/data')";
  html += "    .then(response => response.json())";
  html += "    .then(data => {";
  html += "      let html = '';";
  html += "      html += '<div class=\"data\"><span class=\"label\">Toprak Nemi:</span> <span class=\"value\">' + data.toprak_nem + '</span></div>';";
  html += "      html += '<div class=\"data\"><span class=\"label\">Sıcaklık:</span> <span class=\"value\">' + data.sicaklik + ' °C</span></div>';";
  html += "      html += '<div class=\"data\"><span class=\"label\">Nem:</span> <span class=\"value\">' + data.nem + ' %</span></div>';";
  html += "      html += '<div class=\"data\"><span class=\"label\">Sulama Durumu:</span> <span class=\"status ' + (data.sulama_durumu ? 'on' : 'off') + '\">' + (data.sulama_durumu ? 'AÇIK' : 'KAPALI') + '</span></div>';";
  html += "      document.getElementById('sensorData').innerHTML = html;";
  html += "    })";
  html += "    .catch(error => {";
  html += "      document.getElementById('sensorData').innerHTML = '<div class=\"data\">Veri yüklenirken hata oluştu</div>';";
  html += "    });";
  html += "}";
  html += "refreshData();";
  html += "setInterval(refreshData, 5000);"; // Her 5 saniyede bir otomatik yenile
  html += "</script>";
  html += "</body></html>";
  
  server.send(200, "text/html", html);
}

// Sensör verilerini JSON formatında döndür
void handleData() {
  String json = "{";
  json += "\"toprak_nem\":" + String(toprakNem) + ",";
  json += "\"sicaklik\":" + String(temperature) + ",";
  json += "\"nem\":" + String(humidity) + ",";
  json += "\"sulama_durumu\":" + String(sula ? "true" : "false");
  json += "}";
  
  server.send(200, "application/json", json);
}

int Hesapla(float temperature, float humidity) {
  float tempEffect = (temperature - 20.0) * 0.5;  // 20°C referans
  float humidityEffect = (humidity - 50.0) * -0.3;  // 50% nem referans
  float threshold = 180 + tempEffect + humidityEffect;
  
  //if (threshold < 100) threshold = 100;
  //if (threshold > 500) threshold = 500;
  
  return (int)threshold;
}
