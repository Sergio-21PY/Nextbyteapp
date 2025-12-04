package com.example.nextbyte_app.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.Order
import com.example.nextbyte_app.data.OrderStatus
import com.example.nextbyte_app.viewmodels.MyOrdersViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    myOrdersViewModel: MyOrdersViewModel = viewModel()
) {
    val orders by myOrdersViewModel.orders.collectAsState()
    val isLoading by myOrdersViewModel.isLoading.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { myOrdersViewModel.loadUserOrders(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos") },
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
                Text("Aún no has realizado ningún pedido.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders, key = { it.orderId }) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    val formatter = remember { SimpleDateFormat("dd 'de' MMMM, yyyy", Locale.getDefault()) }
    val (icon, color) = getStatusAppearance(order.status)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pedido #${order.orderId.take(6).uppercase()}", fontWeight = FontWeight.Bold)
                Text(formatter.format(order.createdAt.toDate()), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            order.items.forEach {
                Text("• ${it.quantity} x ${it.name}")
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = "Estado", tint = color, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatOrderStatus(order.status),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Text(
                    text = "$${String.format("%.2f", order.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatOrderStatus(status: OrderStatus): String {
    return status.name.replace('_', ' ').lowercase().replaceFirstChar { 
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
    }
}

@Composable
private fun getStatusAppearance(status: OrderStatus): Pair<ImageVector, Color> {
    return when (status) {
        OrderStatus.PROCESANDO -> Icons.Default.Refresh to MaterialTheme.colorScheme.secondary
        OrderStatus.EN_CAMINO -> Icons.Default.LocalShipping to MaterialTheme.colorScheme.primary
        OrderStatus.ENTREGADO -> Icons.Default.CheckCircle to Color(0xFF388E3C) // Verde oscuro
        OrderStatus.CANCELADO -> Icons.Default.Error to MaterialTheme.colorScheme.error
    }
}