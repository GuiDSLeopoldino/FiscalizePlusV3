package br.com.fiscalizaplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiscalizaplus.model.Despesa
import br.com.fiscalizaplus.ui.theme.AccentRed
import br.com.fiscalizaplus.ui.theme.Green60
import br.com.fiscalizaplus.viewmodel.DeputadosViewModel
import br.com.fiscalizaplus.viewmodel.UiState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    viewModel: DeputadosViewModel,
    deputadoId: Int,
    deputadoNome: String,
    onBack: () -> Unit
) {
    val state by viewModel.despesas.collectAsState()
    val anoAtual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    var anoSelecionado by remember { mutableIntStateOf(anoAtual) }
    var mesSelecionado by remember { mutableStateOf<Int?>(null) }
    val anos = (anoAtual - 3..anoAtual).toList().reversed()
    val meses = (1..12).toList()

    LaunchedEffect(deputadoId, anoSelecionado, mesSelecionado) {
        viewModel.carregarDespesas(deputadoId, anoSelecionado, mesSelecionado)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gastos de $deputadoNome",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green60)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Year selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Ano:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onBackground
                )
                anos.forEach { ano ->
                    FilterChip(
                        selected = anoSelecionado == ano,
                        onClick = { anoSelecionado = ano },
                        label = { Text(ano.toString()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green60,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Month chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = mesSelecionado == null,
                    onClick = { mesSelecionado = null },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Green60,
                        selectedLabelColor = Color.White
                    )
                )
                meses.forEach { mes ->
                    FilterChip(
                        selected = mesSelecionado == mes,
                        onClick = { mesSelecionado = mes },
                        label = { Text(nomeMes(mes)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green60,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            when (val s = state) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Green60)
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Erro ao carregar gastos: ${s.message}",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(24.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                is UiState.Success -> {
                    val total = s.data.sumOf { it.valorDocumento }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Green60),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                "Total do período",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                formatarMoeda(total),
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "${s.data.size} lançamento(s)",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (s.data.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "Nenhum gasto encontrado para o período.",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(s.data) { despesa ->
                                DespesaCard(despesa)
                            }
                        }
                    }
                }
                is UiState.Idle -> {}
            }
        }
    }
}

@Composable
private fun DespesaCard(despesa: Despesa) {
    val corValor = when {
        despesa.valorDocumento > 5000 -> AccentRed
        despesa.valorDocumento < 500 -> Green60
        else -> MaterialTheme.colorScheme.onSurface
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.ReceiptLong,
                contentDescription = null,
                tint = corValor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    despesa.tipoDespesa,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                despesa.nomeFornecedor?.let {
                    Text(
                        it,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
                despesa.dataDocumento?.let {
                    Text(
                        formatarDataDespesa(it),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                formatarMoeda(despesa.valorDocumento),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = corValor
            )
        }
    }
}

private fun formatarMoeda(valor: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(valor)
}

private fun nomeMes(mes: Int): String {
    val nomes = listOf(
        "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
        "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    )
    return if (mes in 1..12) nomes[mes - 1] else mes.toString()
}

private fun formatarDataDespesa(data: String): String {
    // Espera yyyy-MM-dd (com possível hora)
    return try {
        val soData = data.take(10)
        val partes = soData.split("-")
        if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else data
    } catch (e: Exception) {
        data
    }
}
