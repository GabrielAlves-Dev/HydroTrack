package com.gabriel.hydrotrack.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.gabriel.hydrotrack.viewmodel.LoginViewModel
import com.gabriel.hydrotrack.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel() // Injete LoginViewModel
) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoading by loginViewModel.isLoading.collectAsState() // Coleta o estado de loading do LoginViewModel

    var showEditDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) } // Renomeado e adaptado para exclusão
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading // Desabilita o botão se estiver carregando
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Dados")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // NOVO: Botão Excluir Conta
                OutlinedButton(
                    onClick = { showConfirmDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    enabled = !isLoading // Desabilita o botão se estiver carregando
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Deletar Registro",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Excluir Conta")
                }
            }
            // Se estiver carregando (excluindo conta), mostra o spinner
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {}, // Remove o confirmButton aqui, pois o controle é feito na Row
            title = null,
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Editar Dados", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editedEmail,
                        onValueChange = { editedEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editedPhone,
                        onValueChange = { editedPhone = it },
                        label = { Text("Telefone") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showEditDialog = false }, enabled = !isLoading) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                showEditDialog = false
                                showEditConfirmDialog = true
                            },
                            enabled = !isLoading
                        ) {
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
                }, enabled = !isLoading) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditConfirmDialog = false }, enabled = !isLoading) {
                    Text("Cancelar")
                }
            }
        )
    }

    // NOVO: Diálogo de Confirmação para Excluir Conta
    if (showConfirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            title = { Text("Excluir Conta") },
            text = { Text("Tem certeza que deseja excluir sua conta? Esta ação é irreversível e apagará todos os seus dados.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDeleteDialog = false
                    loginViewModel.deleteAccount(
                        onSuccess = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Conta excluída com sucesso!")
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        },
                        onError = { errorMsg ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Erro ao excluir conta: $errorMsg")
                            }
                        }
                    )
                },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}