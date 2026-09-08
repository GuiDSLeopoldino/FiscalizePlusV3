package br.com.fiscalizaplus.model

import com.google.gson.annotations.SerializedName

data class DeputadosResponse(
    @SerializedName("dados") val dados: List<Deputado>,
    @SerializedName("links") val links: List<Link>?
)

data class Deputado(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String,
    @SerializedName("siglaPartido") val siglaPartido: String,
    @SerializedName("siglaUf") val siglaUf: String,
    @SerializedName("idLegislatura") val idLegislatura: Int,
    @SerializedName("urlFoto") val urlFoto: String,
    @SerializedName("email") val email: String?
)

data class DeputadoDetalhesResponse(
    @SerializedName("dados") val dados: DeputadoDetalhes
)

data class DeputadoDetalhes(
    @SerializedName("id") val id: Int,
    @SerializedName("nomeCivil") val nomeCivil: String,
    @SerializedName("ultimoStatus") val ultimoStatus: UltimoStatus,
    @SerializedName("cpf") val cpf: String?,
    @SerializedName("sexo") val sexo: String?,
    @SerializedName("urlWebsite") val urlWebsite: String?,
    @SerializedName("redeSocial") val redeSocial: List<String>?,
    @SerializedName("dataNascimento") val dataNascimento: String?,
    @SerializedName("municipioNascimento") val municipioNascimento: String?
)

data class UltimoStatus(
    @SerializedName("nome") val nome: String,
    @SerializedName("siglaPartido") val siglaPartido: String,
    @SerializedName("siglaUf") val siglaUf: String,
    @SerializedName("urlFoto") val urlFoto: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("situacao") val situacao: String?,
    @SerializedName("descricaoStatus") val descricaoStatus: String?,
    @SerializedName("nomeEleitoral") val nomeEleitoral: String?,
    @SerializedName("gabinete") val gabinete: Gabinete?
)

data class Gabinete(
    @SerializedName("nome") val nome: String?,
    @SerializedName("predio") val predio: String?,
    @SerializedName("sala") val sala: String?,
    @SerializedName("andar") val andar: String?,
    @SerializedName("telefone") val telefone: String?,
    @SerializedName("email") val email: String?
)

data class DespesasResponse(
    @SerializedName("dados") val dados: List<Despesa>
)

data class Despesa(
    @SerializedName("ano") val ano: Int,
    @SerializedName("mes") val mes: Int,
    @SerializedName("tipoDespesa") val tipoDespesa: String,
    @SerializedName("dataDocumento") val dataDocumento: String?,
    @SerializedName("nomeFornecedor") val nomeFornecedor: String?,
    @SerializedName("cnpjCpfFornecedor") val cnpjCpfFornecedor: String?,
    @SerializedName("valorDocumento") val valorDocumento: Double,
    @SerializedName("urlDocumento") val urlDocumento: String?
)

data class Link(
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)
