package com.example.nextbyte_app.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// 1. Modelo de datos para cada tienda
data class StoreLocation(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

// 2. Lista de nuestras tiendas físicas
private val physicalStores = listOf(
    StoreLocation("NextByte: Sucursal San Joaquín", "Av. Vicuña Mackenna 491, San Joaquín, Santiago", -33.4975, -70.6135),
    StoreLocation("NextByte: Sucursal Viña del Mar", "Av. Álvarez 2366, Viña del Mar, Valparaíso", -33.0245, -71.5518),
    StoreLocation("NextByte: Sucursal Antonio Varas", "Antonio Varas 666, Providencia, Santiago", -33.4304, -70.6083),
    StoreLocation("NextByte: Sucursal Apoquindo", "Av. Apoquindo 4950, Las Condes, Santiago", -33.4116, -70.5752)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicalStoresScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nuestras Tiendas Físicas") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(physicalStores) { store ->
                StoreCard(store = store)
            }
        }
    }
}

@Composable
private fun StoreCard(store: StoreLocation) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = store.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = store.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    openMap(context, store.latitude, store.longitude, store.name)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    Icons.Default.LocationOn, 
                    contentDescription = "Ver en Mapa",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Ver en Mapa")
            }
        }
    }
}

// CORREGIDO: Se añade el parámetro de zoom (z=18) para una vista más precisa.
private fun openMap(context: Context, latitude: Double, longitude: Double, label: String) {
    // El formato geo:lat,lng?z=18&q=... centra el mapa, aplica zoom y pone un marcador.
    val encodedLabel = Uri.encode(label)
    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?z=18&q=$latitude,$longitude($encodedLabel)")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

    // Intentamos abrir Google Maps específicamente
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        // Si Google Maps no está, cualquier otra app de mapas puede responder
        val genericMapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        context.startActivity(genericMapIntent)
    }
}
