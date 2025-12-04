package com.example.nextbyte_app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.Order
import com.example.nextbyte_app.data.OrderStatus
import com.example.nextbyte_app.viewmodels.ManageOrdersViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageOrdersScreen(
    navController: NavController,
    manageOrdersViewModel: ManageOrdersViewModel = viewModel()
) {
    val orders by manageOrdersViewModel.allOrders.collectAsState()
    val isLoading by manageOrdersViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Pedidos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No hay pedidos para gestionar.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    AdminOrderCard(order = order, onStatusChange = {
                        manageOrdersViewModel.updateOrderStatus(order.orderId, it)
                    })
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, onStatusChange: (OrderStatus) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Pedido #${order.orderId.take(6)}...", fontWeight = FontWeight.Bold)
                Text(formatter.format(order.createdAt.toDate()), style = MaterialTheme.typography.bodySmall)
            }
            Text("Usuario ID: ${order.userId.take(8)}...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            order.items.forEach {
                Text("• ${it.quantity} x ${it.name}")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Total: $${String.format("%.2f", order.totalPrice)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Estado: ${order.status}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Cambiar estado")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        OrderStatus.entries.forEach { status ->
                            DropdownMenuItem(text = { Text(status.name) }, onClick = { 
                                onStatusChange(status)
                                expanded = false
                            })
                        }
                    }
                }
            }
        }
    }
}