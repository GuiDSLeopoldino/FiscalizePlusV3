package br.com.fiscalizaplus.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import br.com.fiscalizaplus.model.Deputado
import br.com.fiscalizaplus.ui.theme.AccentRed
import br.com.fiscalizaplus.ui.theme.Green60
import br.com.fiscalizaplus.viewmodel.DeputadosViewModel
import br.com.fiscalizaplus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    viewModel: DeputadosViewModel,
    deputadoId: Int,
    nomePassado: String,
    partidoPassado: String,
    ufPassado: String,
    fotoPassada: String,
    onNavigateToGastos: (Int, String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.deputadoDetalhes.collectAsState()
    val favoritos by viewModel.favoritos.collectAsState()
    val isFavorito = favoritos.any { it.id == deputadoId }

    LaunchedEffect(deputadoId) {
        viewModel.carregarDetalhes(deputadoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        nomePassado,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.toggleFavorito(
                            Deputado(
                                id = deputadoId,
                                nome = nomePassado,
                                siglaPartido = partidoPassado,
                                siglaUf = ufPassado,
                                idLegislatura = 0,
                                urlFoto = fotoPassada,
                                email = null
                            )
                        )
                    }) {
                        Icon(
                            if (isFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorito) AccentRed else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green60)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = fotoPassada,
                contentDescription = nomePassado,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(Modifier.height(14.dp))
            Text(
                nomePassado,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Green60.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    "$partidoPassado • $ufPassado",
                    color = Green60,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            when (val s = state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(color = Green60, modifier = Modifier.padding(24.dp))
                }
                is UiState.Error -> {
                    Text(
                        "Não foi possível carregar detalhes: ${s.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is UiState.Success -> {
                    val d = s.data
                    val status = d.ultimoStatus
                    val gab = status.gabinete

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Informações",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Green60
                            )
                            Spacer(Modifier.height(12.dp))
                            InfoRow(Icons.Filled.Info, "Nome civil", d.nomeCivil)
                            status.email?.let { InfoRow(Icons.Filled.Email, "E-mail", it) }
                            status.situacao?.let { InfoRow(Icons.Filled.Info, "Situação", it) }
                            d.dataNascimento?.let { InfoRow(Icons.Filled.Cake, "Nascimento", formatarData(it)) }
                            d.municipioNascimento?.let { mun ->
                                InfoRow(Icons.Filled.LocationCity, "Município", mun)
                            }
                        }
                    }

                    if (gab != null && (gab.predio != null || gab.sala != null || gab.telefone != null)) {
                        Spacer(Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Gabinete",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Green60
                                )
                                Spacer(Modifier.height(12.dp))
                                gab.predio?.let { InfoRow(Icons.Filled.MeetingRoom, "Prédio", it) }
                                gab.sala?.let { InfoRow(Icons.Filled.MeetingRoom, "Sala", it) }
                                gab.andar?.let { InfoRow(Icons.Filled.MeetingRoom, "Andar", it) }
                                gab.telefone?.let { InfoRow(Icons.Filled.Phone, "Telefone", it) }
                            }
                        }
                    }
                }
                is UiState.Idle -> {}
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onNavigateToGastos(deputadoId, nomePassado) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green60)
            ) {
                Icon(Icons.Filled.Paid, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text("Ver Gastos", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Green60, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                valor,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatarData(data: String): String {
    // Espera formato yyyy-MM-dd
    return try {
        val partes = data.split("-")
        if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else data
    } catch (e: Exception) {
        data
    }
}
