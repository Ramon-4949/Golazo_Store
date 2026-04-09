package com.example.golazo_store.presentation.create.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.golazo_store.domain.model.Camiseta
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CreateListScreen(
    viewModel: CreateListViewModel = hiltViewModel(),
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CreateListBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToCreate = onNavigateToCreate,
        onNavigateToEdit = onNavigateToEdit,
        onBack = onBack,
        bottomNavigation = bottomNavigation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListBodyScreen(
    state: CreateListUiState,
    onEvent: (CreateListEvent) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<Camiseta?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión de publicaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface // Matches Figma light creamy background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary, // Darker yellow/orange from Figma
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Camiseta",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        bottomBar = bottomNavigation
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (state.camisetas.isEmpty()) {
                Text(
                    text = "No hay publicaciones registradas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp), // Padding for FAB
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.camisetas, key = { it.id }) { camiseta ->
                        CreateListCardItem(
                            camiseta = camiseta,
                            onEditClick = { onNavigateToEdit(camiseta.id) },
                            onDeleteClick = { showDeleteDialog = camiseta }
                        )
                    }
                }
            }

            if (showDeleteDialog != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = null },
                    title = {
                        Text("Eliminar publicación", fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text("¿Estás seguro de que deseas eliminar '${showDeleteDialog?.nombre}'? Esta acción no se puede deshacer.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog?.let { onEvent(CreateListEvent.OnDeleteClick(it)) }
                                showDeleteDialog = null
                            }
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = null }) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

@Composable
fun CreateListCardItem(
    camiseta: Camiseta,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Currency formatter
    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "DO"))
    formatter.maximumFractionDigits = 0

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Box
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (camiseta.imagenUrl.isNotBlank()) {
                    AsyncImage(
                        model = camiseta.imagenUrl,
                        contentDescription = camiseta.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback visual
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = camiseta.nombre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatter.format(camiseta.precio),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Stock: ${camiseta.stockTotal} ud.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onEditClick() }
                )
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDeleteClick() }
                )
            }
        }
    }
}




