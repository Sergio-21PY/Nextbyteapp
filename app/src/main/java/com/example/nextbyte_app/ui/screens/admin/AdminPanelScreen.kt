package com.example.nextbyte_app.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.example.nextbyte_app.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val users by userViewModel.allUsers.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    var showRoleChangeDialog by remember { mutableStateOf<Pair<User, UserRole>?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Usuarios") },
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
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    UserManagementCard(user = user, onRoleChange = { newRole ->
                        showRoleChangeDialog = Pair(user, newRole)
                    })
                }
            }
        }

        showRoleChangeDialog?.let { (user, newRole) ->
            RoleChangeConfirmationDialog(
                userName = user.name,
                newRole = newRole.name,
                onConfirm = {
                    userViewModel.updateUserRole(user.uid, newRole)
                    showRoleChangeDialog = null
                },
                onDismiss = { showRoleChangeDialog = null }
            )
        }
    }
}

@Composable
fun UserManagementCard(user: User, onRoleChange: (UserRole) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                Button(onClick = { expanded = true }) {
                    Text(user.role.name)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(text = { Text(role.name) }, onClick = { 
                            onRoleChange(role)
                            expanded = false 
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleChangeConfirmationDialog(userName: String, newRole: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Cambio de Rol") },
        text = { Text("¿Estás seguro de que quieres cambiar el rol de '$userName' a '$newRole'?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}