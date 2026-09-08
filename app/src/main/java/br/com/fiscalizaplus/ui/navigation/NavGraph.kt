package br.com.fiscalizaplus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.fiscalizaplus.ui.screens.DeputadosScreen
import br.com.fiscalizaplus.ui.screens.DetalhesScreen
import br.com.fiscalizaplus.ui.screens.GastosScreen
import br.com.fiscalizaplus.ui.screens.HomeScreen
import br.com.fiscalizaplus.ui.screens.SplashScreen
import br.com.fiscalizaplus.viewmodel.DeputadosViewModel

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val DEPUTADOS = "deputados"
    const val DETALHES = "detalhes/{id}/{nome}/{partido}/{uf}/{foto}"
    const val GASTOS = "gastos/{id}/{nome}"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel: DeputadosViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(onNavigateToHome = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToDeputados = { navController.navigate(Routes.DEPUTADOS) }
            )
        }
        composable(Routes.DEPUTADOS) {
            DeputadosScreen(
                viewModel = viewModel,
                onNavigateToDetalhes = { id, nome, partido, uf, foto ->
                    val encodedFoto = java.net.URLEncoder.encode(foto, "UTF-8")
                    val encodedNome = java.net.URLEncoder.encode(nome, "UTF-8")
                    navController.navigate("detalhes/$id/$encodedNome/$partido/$uf/$encodedFoto")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.DETALHES,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("nome") { type = NavType.StringType },
                navArgument("partido") { type = NavType.StringType },
                navArgument("uf") { type = NavType.StringType },
                navArgument("foto") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val nome = backStackEntry.arguments?.getString("nome") ?: ""
            val partido = backStackEntry.arguments?.getString("partido") ?: ""
            val uf = backStackEntry.arguments?.getString("uf") ?: ""
            val foto = backStackEntry.arguments?.getString("foto") ?: ""
            DetalhesScreen(
                viewModel = viewModel,
                deputadoId = id,
                nomePassado = java.net.URLDecoder.decode(nome, "UTF-8"),
                partidoPassado = partido,
                ufPassado = uf,
                fotoPassada = java.net.URLDecoder.decode(foto, "UTF-8"),
                onNavigateToGastos = { depId, depNome ->
                    val encoded = java.net.URLEncoder.encode(depNome, "UTF-8")
                    navController.navigate("gastos/$depId/$encoded")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.GASTOS,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("nome") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val nome = backStackEntry.arguments?.getString("nome") ?: ""
            GastosScreen(
                viewModel = viewModel,
                deputadoId = id,
                deputadoNome = java.net.URLDecoder.decode(nome, "UTF-8"),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
