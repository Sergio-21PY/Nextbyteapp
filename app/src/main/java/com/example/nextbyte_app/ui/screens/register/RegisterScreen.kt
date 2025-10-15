package com.example.nextbyte_app.ui.screens.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextbyte_app.ui.screens.BdFake


@Composable
fun RegisterScreen(
    //Usamos onBack para indicar que la acción es de regreso.
    onBack: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF6A0DAD),
                        Color(0xFF4B0082)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        //MODIFICAR COLORES DE LOS CAMPOS DE TXT Y BOTON
        TextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = {newPassword -> password = newPassword},
            label = {Text("Contraseña")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = number,
            onValueChange = {newNumber -> number = newNumber},
            label = {Text("Numero telefonico")},
            modifier = Modifier
                .fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                if (email.isNotEmpty() && password.isNotEmpty() && number.isNotEmpty()) {

                    BdFake.registeredEmail = email
                    BdFake.registeredPassword = password
                    BdFake.registeredNumber = number

                    /*El toast es un mensaje de retroalimentacion para el usuario,
                    * donde se le notificara si su accion ha terminado o fallo sin
                    * interrumpir la funcionalidad.*/
                    // Notificación de éxito
                    Toast.makeText(
                        context,
                        "¡Registro exitoso para: ${BdFake.registeredEmail}!",
                        Toast.LENGTH_LONG
                    ).show()

                   onBack() //Volvemos al login para iniciar sesion


                } else {
                    // Notificación de error si algún campo está vacío
                    Toast.makeText(
                        context,
                        "Debes llenar todos los campos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9400D3)
            )
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(8.dp))

        //El botón de Volver ejecuta la acción onBack.
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Volver")
        }
    }
}