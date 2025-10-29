package com.example.nextbyte_app.ui.screens.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    var confirmPassword by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    val context = LocalContext.current

    // --- Lógica de validación para el email ---
    val isEmailError = email.isNotEmpty() && "@" !in email

    // -- Logica de validacion de confirmacion de contraseña --

    val isPasswordMismatch = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword

    // --- Definición de colores para los TextField ---
    val customTextFieldColors = TextFieldDefaults.colors(
        // Colores de texto, etiqueta y cursor
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White.copy(alpha = 0.8f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = Color.White,

        // Color del contenedor (fondo)
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,

        // Color del indicador (línea inferior)
        focusedIndicatorColor = Color.White,
        unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),

        // Colores de error
        errorTextColor = Color.White,
        errorLabelColor = Color(0xFFF48FB1), // Un rosa claro para el error
        errorIndicatorColor = Color(0xFFF48FB1),
        errorSupportingTextColor = Color(0xFFF48FB1)
    )

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
            )
            .padding(horizontal = 24.dp), // Padding para que no pegue a los bordes
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        //MODIFICAR COLORES DE LOS CAMPOS DE TXT Y BOTON
        TextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors, // Colores aplicados
            isError = isEmailError, // Estado de error
            supportingText = { // Mensaje de error
                if (isEmailError) {
                    Text(text = "El correo debe contener un '@'")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = {newPassword -> password = newPassword},
            label = {Text("Contraseña")},
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = isPasswordMismatch // Para que ambos se marquen en rojo
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { newConfirmPassword -> confirmPassword = newConfirmPassword },
            label = { Text("Repetir contraseña") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

            // Lógica de error para este campo
            isError = isPasswordMismatch,
            supportingText = {
                if (isPasswordMismatch) {
                    Text(text = "Las contraseñas no coinciden")
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = number,
            onValueChange = {newNumber -> number = newNumber},
            label = {Text("Numero telefonico")},
            modifier = Modifier
                .fillMaxWidth(),
            colors = customTextFieldColors, // Colores aplicados
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // 1. Validar campos vacíos
                if (email.isEmpty() || password.isEmpty() || number.isEmpty()) {

                    /*El toast es un mensaje de retroalimentacion para el usuario,
                    * donde se le notificara si su accion ha terminado o fallo sin
                    * interrumpir la funcionalidad.*/ //
                    // Notificación de error si algún campo está vacío
                    Toast.makeText(
                        context,
                        "Debes llenar todos los campos.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                // 2. Validar formato de email
                if (isEmailError) {
                    Toast.makeText(
                        context,
                        "El formato del correo no es válido.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                // 3. Si todo está bien, registrar
                BdFake.registeredEmail = email
                BdFake.registeredPassword = password
                BdFake.registeredNumber = number

                // Notificación de éxito
                Toast.makeText(
                    context,
                    "¡Registro exitoso para: ${BdFake.registeredEmail}!",
                    Toast.LENGTH_LONG
                ).show()

                onBack() //Volvemos al login para iniciar sesion
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
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Texto actualizado para mayor claridad
            Text("Volver al inicio de sesión", color = Color.White.copy(alpha = 0.8f))
        }
    }
}