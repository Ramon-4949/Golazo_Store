package com.example.golazo_store.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToGestionPublicaciones: () -> Unit,
    onNavigateToAdminPedidos: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val user by viewModel.userState.collectAsState()

    val isAdmin = user?.rol == "Admin"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF07152B)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = bottomNavigation
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Profile Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFF0F4F8), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFFA0B2C6)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user?.nombre ?: "Usuario",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF07152B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user?.correo ?: "correo@golazo.com",
                    fontSize = 16.sp,
                    color = Color(0xFF6B7A90)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { /* TODO Editar Perfil */ },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF07152B)
                    )
                ) {
                    Text("Editar Perfil", fontWeight = FontWeight.SemiBold)
                }
            }

            // Actividad section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "MI ACTIVIDAD",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7A90),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isAdmin) {
                    ProfileMenuItem(
                        icon = Icons.Default.GridView,
                        title = "Gestión de publicaciones",
                        subtitle = "Control de catálogo",
                        iconTint = Color(0xFFFFBA08),
                        onClick = onNavigateToGestionPublicaciones
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.ShoppingBag,
                        title = "Pedidos",
                        subtitle = "Seguimientos de transacciones",
                        iconTint = Color(0xFFFFBA08),
                        onClick = onNavigateToAdminPedidos
                    )
                } else {
                    ProfileMenuItem(
                        icon = Icons.Default.ShoppingBag,
                        title = "Mis Pedidos",
                        subtitle = "Historial y seguimiento",
                        iconTint = Color(0xFFFFBA08),
                        onClick = onNavigateToMisPedidos
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.LocationOn,
                        title = "Mis Direcciones",
                        subtitle = "Gestionar puntos de entrega",
                        iconTint = Color(0xFFFFBA08),
                        onClick = onNavigateToAddresses
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "Métodos de Pago",
                        subtitle = "Tarjetas y formas de pago",
                        iconTint = Color(0xFFFFBA08),
                        onClick = onNavigateToPayments
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "PREFERENCIAS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7A90),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.logout()
                            onNavigateToLogin()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFEBEB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = Color(0xFFE53935)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Cerrar Sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935)
                            )
                            Text(
                                text = "Salir de tu cuenta",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7A90)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF9E6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF07152B)
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7A90)
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ir",
                tint = Color(0xFFA0B2C6)
            )
        }
    }
}


