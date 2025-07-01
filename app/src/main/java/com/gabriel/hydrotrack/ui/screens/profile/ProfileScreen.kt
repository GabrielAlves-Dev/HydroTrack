package com.gabriel.hydrotrack.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gabriel.hydrotrack.R
import com.gabriel.hydrotrack.navigation.Screen
import com.gabriel.hydrotrack.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditConfirmDialog by remember { mutableStateOf(false) }

    var editedName by remember(userProfile) { mutableStateOf(userProfile.name) }
    var editedEmail by remember(userProfile) { mutableStateOf(userProfile.email) }
    var editedPhone by remember(userProfile) { mutableStateOf(userProfile.phone) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil do Usuário") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Profile.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_placeholder),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Text("Nome: ${userProfile.name}")
                        Text("Email: ${userProfile.email}")
                        Text("Telefone: ${userProfile.phone}")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        editedName = userProfile.name
                        editedEmail = userProfile.email
                        editedPhone = userProfile.phone
                        showEditDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Dados")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(id = R.drawable.ic_logout), contentDescription = "Sair", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sair da Conta")
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {},
            title = null,
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Editar Dados", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editedEmail,
                        onValueChange = { editedEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editedPhone,
                        onValueChange = { editedPhone = it },
                        label = { Text("Telefone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            showEditDialog = false
                            showEditConfirmDialog = true
                        }) {
                            Text("Salvar")
                        }
                    }
                }
            }
        )
    }

    if (showEditConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showEditConfirmDialog = false },
            title = { Text("Confirmar Alteração") },
            text = { Text("Deseja realmente salvar as alterações?") },
            confirmButton = {
                TextButton(onClick = {
                    showEditConfirmDialog = false
                    profileViewModel.updateUserProfile(
                        name = editedName,
                        email = editedEmail,
                        phone = editedPhone
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar("Dados atualizados com sucesso!")
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sair da Conta") },
            text = { Text("Tem certeza que deseja sair?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    profileViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }) {
                    Text("Sair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}