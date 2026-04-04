package com.example.golazo_store.presentation.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.Golazo_Store.R
import com.example.golazo_store.presentation.components.AuthButton
import com.example.golazo_store.presentation.components.AuthTextField
import com.example.golazo_store.presentation.components.AuthYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Golazo Store Logo",
                modifier = Modifier.size(180.dp).padding(top = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Únete a la mejor comunidad de fútbol y obtén beneficios exclusivos.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name
                AuthTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.onEvent(RegisterEvent.NombreChanged(it)) },
                    label = "Nombre completo",
                    placeholder = "Juan Pérez",
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) }
                )

                // Email
                AuthTextField(
                    value = state.correo,
                    onValueChange = { viewModel.onEvent(RegisterEvent.CorreoChanged(it)) },
                    label = "Correo electrónico",
                    placeholder = "ejemplo@correo.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) }
                )

                // Password
                AuthTextField(
                    value = state.contrasena,
                    onValueChange = { viewModel.onEvent(RegisterEvent.ContrasenaChanged(it)) },
                    label = "Contraseña",
                    placeholder = "••••••••",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                        }
                    }
                )

                // Confirm Password
                AuthTextField(
                    value = state.confirmContrasena,
                    onValueChange = { viewModel.onEvent(RegisterEvent.ConfirmContrasenaChanged(it)) },
                    label = "Confirmar contraseña",
                    placeholder = "••••••••",
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        // The Figma doesn't explicitly show a visibility toggle for the confirm password, 
                        // but it's good UX to add it, or we can omit it if strictly following Figma. Let's omit to strictly follow Figma.
                        // Actually Figma shows lock with circular arrow, we used Refresh icon which is similar.
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = AuthYellow)
            } else {
                AuthButton(
                    text = "Crear cuenta",
                    onClick = { viewModel.onEvent(RegisterEvent.RegisterClicked) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 16.dp, top = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "¿Ya tienes una cuenta? ", color = Color.Gray)
                Text(
                    text = "Inicia sesión aquí",
                    color = AuthYellow,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }
        }
    }
}


