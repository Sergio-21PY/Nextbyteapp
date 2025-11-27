package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastOrdersScreen(
    navController: NavController,
    pastOrdersViewModel: PastOrdersViewModel = viewModel()
) {
    // FORZAMOS LA VISTA DE ADMINISTRADOR PARA QUE LA BÚSQUEDA SEA VISIBLE
    val isAdmin = true
    val currentUserEmail = if (isAdmin) "admin@admin.cl" else "user@user.cl"

    LaunchedEffect(Unit) {
        pastOrdersViewModel.loadOrders(currentUserEmail, isAdmin)
    }

    val orders = pastOrdersViewModel.filteredOrders
    val searchQuery = pastOrdersViewModel.searchQuery

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAdmin) "Todos los Pedidos" else "Mis Pedidos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isAdmin) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = pastOrdersViewModel::onSearchQueryChange,
                    onSearch = pastOrdersViewModel::performSearch
                )
            }

            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // CORRECCIÓN: Mensaje de error más específico
                    Text(if (searchQuery.isNotBlank()) "ID de pedido no encontrado." else "No hay pedidos para mostrar.")
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(orders, key = { it.orderId }) {
                        OrderCard(order = it, isAdmin = isAdmin)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar por ID o cliente") },
            modifier = Modifier.weight(1f),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSearch) {
            Text("Buscar")
        }
    }
}

@Composable
private fun OrderCard(order: Order, isAdmin: Boolean) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pedido #${order.orderId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            if (isAdmin) {
                Text("Cliente: ${order.userName} (${order.userEmail})")
            }
            Text("Fecha: ${order.orderDate}")
            Text("Total: ${formatPrice(order.totalAmount)}")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Artículos:", fontWeight = FontWeight.Bold)
            order.items.forEach {
                Text("  - ${it.product.name} (x${it.quantity})")
            }
        }
    }
}

private fun formatPrice(price: Int): String {
    return "$${price.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1.")}"
}
