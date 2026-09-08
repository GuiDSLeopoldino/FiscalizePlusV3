package br.com.fiscalizaplus.network

import br.com.fiscalizaplus.model.DeputadoDetalhesResponse
import br.com.fiscalizaplus.model.DeputadosResponse
import br.com.fiscalizaplus.model.DespesasResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("deputados")
    suspend fun getDeputados(
        @Query("itens") itens: Int = 20,
        @Query("pagina") pagina: Int = 1,
        @Query("ordem") ordem: String = "ASC",
        @Query("ordenarPor") ordenarPor: String = "nome"
    ): Response<DeputadosResponse>

    @GET("deputados")
    suspend fun buscarDeputadosPorNome(
        @Query("nome") nome: String,
        @Query("itens") itens: Int = 30
    ): Response<DeputadosResponse>

    @GET("deputados/{id}")
    suspend fun getDeputadoDetalhes(
        @Path("id") id: Int
    ): Response<DeputadoDetalhesResponse>

    @GET("deputados/{id}/despesas")
    suspend fun getDespesas(
        @Path("id") id: Int,
        @Query("ano") ano: Int,
        @Query("idLegislatura") idLegislatura: Int = 57,
        @Query("mes") mes: Int? = null,
        @Query("itens") itens: Int = 30,
        @Query("pagina") pagina: Int = 1,
        @Query("ordenarPor") ordenarPor: String = "dataDocumento",
        @Query("ordem") ordem: String = "DESC"
    ): Response<DespesasResponse>
}
