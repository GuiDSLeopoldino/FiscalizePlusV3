package br.com.fiscalizaplus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiscalizaplus.model.Deputado
import br.com.fiscalizaplus.model.DeputadoDetalhes
import br.com.fiscalizaplus.model.Despesa
import br.com.fiscalizaplus.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Idle : UiState<Nothing>()
}

class DeputadosViewModel : ViewModel() {

    private val _deputados = MutableStateFlow<UiState<List<Deputado>>>(UiState.Idle)
    val deputados: StateFlow<UiState<List<Deputado>>> = _deputados.asStateFlow()

    private val _deputadoDetalhes = MutableStateFlow<UiState<DeputadoDetalhes>>(UiState.Idle)
    val deputadoDetalhes: StateFlow<UiState<DeputadoDetalhes>> = _deputadoDetalhes.asStateFlow()

    private val _despesas = MutableStateFlow<UiState<List<Despesa>>>(UiState.Idle)
    val despesas: StateFlow<UiState<List<Despesa>>> = _despesas.asStateFlow()

    private val _favoritos = MutableStateFlow<List<Deputado>>(emptyList())
    val favoritos: StateFlow<List<Deputado>> = _favoritos.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun carregarDeputados(pagina: Int = 1) {
        viewModelScope.launch {
            _deputados.value = UiState.Loading
            try {
                val response = RetrofitInstance.api.getDeputados(itens = 20, pagina = pagina)
                if (response.isSuccessful) {
                    val dados = response.body()?.dados ?: emptyList()
                    _deputados.value = UiState.Success(dados)
                } else {
                    _deputados.value = UiState.Error("Erro ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                _deputados.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun buscarDeputados(nome: String) {
        _searchQuery.value = nome
        if (nome.isBlank()) {
            carregarDeputados()
            return
        }
        viewModelScope.launch {
            _deputados.value = UiState.Loading
            try {
                val response = RetrofitInstance.api.buscarDeputadosPorNome(nome)
                if (response.isSuccessful) {
                    val dados = response.body()?.dados ?: emptyList()
                    _deputados.value = UiState.Success(dados)
                } else {
                    _deputados.value = UiState.Error("Erro ${response.code()}")
                }
            } catch (e: Exception) {
                _deputados.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun carregarDetalhes(id: Int) {
        viewModelScope.launch {
            _deputadoDetalhes.value = UiState.Loading
            try {
                val response = RetrofitInstance.api.getDeputadoDetalhes(id)
                if (response.isSuccessful) {
                    val dados = response.body()?.dados
                    if (dados != null) {
                        _deputadoDetalhes.value = UiState.Success(dados)
                    } else {
                        _deputadoDetalhes.value = UiState.Error("Dados não encontrados")
                    }
                } else {
                    _deputadoDetalhes.value = UiState.Error("Erro ${response.code()}")
                }
            } catch (e: Exception) {
                _deputadoDetalhes.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun carregarDespesas(id: Int, ano: Int, mes: Int? = null) {
        viewModelScope.launch {
            _despesas.value = UiState.Loading
            try {
                val response = RetrofitInstance.api.getDespesas(id, ano, mes = mes)
                if (response.isSuccessful) {
                    val dados = response.body()?.dados ?: emptyList()
                    _despesas.value = UiState.Success(dados)
                } else {
                    _despesas.value = UiState.Error("Erro ${response.code()}")
                }
            } catch (e: Exception) {
                _despesas.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun toggleFavorito(deputado: Deputado) {
        val lista = _favoritos.value.toMutableList()
        if (lista.any { it.id == deputado.id }) {
            lista.removeAll { it.id == deputado.id }
        } else {
            lista.add(deputado)
        }
        _favoritos.value = lista
    }

    fun isFavorito(id: Int): Boolean = _favoritos.value.any { it.id == id }
}
