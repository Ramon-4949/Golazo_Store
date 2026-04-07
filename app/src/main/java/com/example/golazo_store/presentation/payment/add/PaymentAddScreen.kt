package com.example.golazo_store.presentation.payment.add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.golazo_store.ui.theme.primaryDark

@Composable
fun PaymentAddScreen(
    viewModel: PaymentAddViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            Toast.makeText(context, "Tarjeta agregada exitosamente", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    PaymentAddBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentAddBodyScreen(
    state: PaymentAddUiState,
    onEvent: (PaymentAddEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agregar Métodos de Pago",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF07152B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ATRÁS",
                            tint = Color(0xFF07152B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF9F9FB)
                )
            )
        },
        containerColor = Color(0xFFF9F9FB)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Card UI
                VisualCreditCard(state)

                // Nombre Titular
                PaymentTextField(
                    label = "Nombre en la tarjeta",
                    value = state.nombreTitular,
                    placeholder = "Ej. Juan Pérez",
                    error = state.titularError,
                    leadingIcon = Icons.Default.PersonOutline,
                    onValueChange = { onEvent(PaymentAddEvent.OnNombreTitularChange(it)) }
                )

                // Número de tarjeta
                PaymentTextField(
                    label = "Número de tarjeta",
                    value = state.numeroTarjeta,
                    placeholder = "0000 0000 0000 0000",
                    error = state.numeroTarjetaError,
                    leadingIcon = Icons.Outlined.CreditCard,
                    keyboardType = KeyboardType.Number,
                    onValueChange = { onEvent(PaymentAddEvent.OnNumeroTarjetaChange(it)) }
                )

                // Expiración y CVV
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PaymentTextField(
                        label = "Expiración",
                        value = state.expiracionMMYY,
                        placeholder = "MM/AA",
                        error = state.expiracionError,
                        leadingIcon = Icons.Default.DateRange,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                        visualTransformation = ExpirationVisualTransformation(),
                        onValueChange = {
                            onEvent(PaymentAddEvent.OnExpiracionChange(it))
                        }
                    )
                    PaymentTextField(
                        label = "CVV",
                        value = state.cvv,
                        placeholder = "123",
                        error = state.cvvError,
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                        onValueChange = { onEvent(PaymentAddEvent.OnCvvChange(it)) }
                    )
                }

                // Checkbox Principal
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = state.esPrincipal,
                        onCheckedChange = { onEvent(PaymentAddEvent.OnEsPrincipalChange(it)) },
                        colors = CheckboxDefaults.colors(checkedColor = primaryDark)
                    )
                    Text(
                        text = "Guardar esta tarjeta como principal",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7A90)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = { onEvent(PaymentAddEvent.OnSave) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = "Guardar",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar Tarjeta",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun VisualCreditCard(state: PaymentAddUiState) {
    val displayNum = if (state.numeroTarjeta.length >= 4) {
        "•••• •••• •••• ${state.numeroTarjeta.takeLast(4)}"
    } else {
        "•••• •••• •••• ••••"
    }
    
    val displayHolder = state.nombreTitular.ifBlank { "NOMBRE DEL TITULAR" }.uppercase()
    val displayExp = if (state.expiracionMMYY.length >= 3) {
        "${state.expiracionMMYY.take(2)}/${state.expiracionMMYY.drop(2)}"
    } else if (state.expiracionMMYY.isNotEmpty()) {
        state.expiracionMMYY
    } else {
        "MM/AA"
    }
    val displayCvv = if (state.cvv.isNotEmpty()) "***" else "***"
    
    val cardType = when (state.numeroTarjeta.firstOrNull()) {
        '4' -> "VISA"
        '5' -> "MASTERCARD"
        '3' -> "AMEX"
        else -> "VISA"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1E243A), Color(0xFF111424))
                )
            )
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GOLAZO STORE PLATINUM",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = cardType,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // NFC Icon approximate
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = null,
                tint = primaryDark,
                modifier = Modifier.size(32.dp).offset(y = (-8).dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = displayNum,
                color = Color.White,
                fontSize = 20.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "TITULAR DE LA TARJETA",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 8.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = displayHolder,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "VENCE",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 8.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = displayExp,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CVV",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 8.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = displayCvv,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentTextField(
    label: String,
    value: String,
    placeholder: String,
    error: String? = null,
    leadingIcon: ImageVector,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF07152B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = primaryDark,
                cursorColor = primaryDark,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            singleLine = true
        )
    }
}

class ExpirationVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        for (i in text.indices) {
            out += text[i]
            if (i == 1) out += "/"
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                return offset + 1
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                return offset - 1
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
