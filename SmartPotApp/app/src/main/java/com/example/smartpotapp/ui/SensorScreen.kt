package com.example.smartpotapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpotapp.viewmodel.SensorViewModel
import kotlinx.coroutines.delay

@Composable
fun SensorScreen(
    viewModel: SensorViewModel = viewModel()
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var lastUpdateTime by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.fetchSensorData()
            lastUpdateTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date())
            delay(5000) // Her 5 saniyede bir güncelle
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Akıllı Saksı Monitörü",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Mobil uygulama, Kotlin ve Jetpack Compose ile geliştirilmiştir. " +
                  "Gerçek zamanlı sensör verileri izlenebilir, sulama durumu kontrol edilebilir " +
                  "ve kritik değerlerde renk kodlu uyarılar sağlanır.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading && sensorData == null) {
            CircularProgressIndicator()
        }

        error?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        sensorData?.let { data ->
            SensorDataCard(data)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Son güncelleme: $lastUpdateTime (5 saniyede bir otomatik güncellenir)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SensorDataCard(data: com.example.smartpotapp.api.SensorData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Toprak nemi gösterimi
            val toprakNemColor = when {
                data.toprak_nem < 30 -> Color.Red  // Çok kuru - kritik
                data.toprak_nem < 60 -> Color(0xFFFFA500) // Turuncu - uyarı
                else -> Color.Green  // İyi durum
            }
            
            SensorDataRow(
                label = "Toprak Nemi",
                value = "%${data.toprak_nem}",
                color = toprakNemColor,
                description = when {
                    data.toprak_nem < 30 -> "Kritik seviye! Sulama gerekli"
                    data.toprak_nem < 60 -> "Düşük seviye"
                    else -> "İyi durumda"
                }
            )
            
            // Sıcaklık gösterimi
            val sicaklikColor = when {
                data.sicaklik > 30 -> Color.Red  // Çok sıcak - kritik
                data.sicaklik > 25 -> Color(0xFFFFA500)  // Turuncu - uyarı
                data.sicaklik < 10 -> Color(0xFF87CEEB)  // Açık mavi - soğuk
                else -> Color.Green  // İyi durum
            }
            
            SensorDataRow(
                label = "Sıcaklık",
                value = "${data.sicaklik}°C",
                color = sicaklikColor,
                description = when {
                    data.sicaklik > 30 -> "Yüksek sıcaklık! Dikkat"
                    data.sicaklik > 25 -> "Yüksek sıcaklık"
                    data.sicaklik < 10 -> "Düşük sıcaklık"
                    else -> "İdeal sıcaklık"
                }
            )
            
            // Nem gösterimi
            val nemColor = when {
                data.nem < 30 -> Color.Red  // Çok kuru - kritik
                data.nem < 40 -> Color(0xFFFFA500)  // Turuncu - uyarı
                data.nem > 80 -> Color(0xFF87CEEB)  // Açık mavi - çok nemli
                else -> Color.Green  // İyi durum
            }
            
            SensorDataRow(
                label = "Nem",
                value = "%${data.nem}",
                color = nemColor,
                description = when {
                    data.nem < 30 -> "Düşük nem! Dikkat"
                    data.nem < 40 -> "Düşük nem"
                    data.nem > 80 -> "Yüksek nem"
                    else -> "İdeal nem"
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Sulama durumu
            val sulamaDurumuColor = if (data.sulama_durumu) Color(0xFF4169E1) else Color.Gray
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sulama Durumu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = sulamaDurumuColor
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = if (data.sulama_durumu) "AKTİF" else "PASİF",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            if (!data.sulama_durumu && data.toprak_nem < 30) {
                Text(
                    text = "Toprak nemi düşük! Sulama önerilir.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SensorDataRow(label: String, value: String, color: Color, description: String = "") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (description.isNotEmpty()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
} 