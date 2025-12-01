package com.example.serviconnecta.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.serviconnecta.feature.auth.ui.identity.IdentityVerificationViewModel
import com.example.serviconnecta.feature.auth.ui.login.LoginViewModel
import com.example.serviconnecta.feature.auth.ui.navigation.AuthNavGraph
import com.example.serviconnecta.feature.auth.ui.register.RegistrationViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun MainNavGraph(
    loginViewModel: LoginViewModel,
    registrationViewModel: RegistrationViewModel,
    identityVerificationViewModel: IdentityVerificationViewModel,
    accountTypeFlow: Flow<String?>,
    userPreferences: com.example.serviconnecta.core.datastore.UserPreferences,
    onClearSession: suspend () -> Unit
) {
    val rootNavController = rememberNavController()
    var currentAccountType by remember { mutableStateOf<String?>(null) }

    // Observar el account_type guardado
    LaunchedEffect(Unit) {
        accountTypeFlow.collect { accountType ->
            currentAccountType = accountType
        }
    }

    NavHost(
        navController = rootNavController,
        startDestination = "auth"
    ) {
        composable("auth") {
            val authNavController = rememberNavController()
            AuthNavGraphWrapper(
                navController = authNavController,
                loginViewModel = loginViewModel,
                registrationViewModel = registrationViewModel,
                identityVerificationViewModel = identityVerificationViewModel,
                onLoginSuccess = { accountType ->
                    currentAccountType = accountType
                    when (accountType) {
                        "CONECTA_PRO" -> rootNavController.navigate("worker") {
                            popUpTo("auth") { inclusive = true }
                        }
                        "CLIENTE" -> rootNavController.navigate("client") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("worker") {
            val workerNavController = rememberNavController()
            WorkerNavGraph(
                navController = workerNavController,
                userPreferences = userPreferences,
                onLogout = {
                    kotlinx.coroutines.runBlocking {
                        onClearSession()
                    }
                    currentAccountType = null
                    rootNavController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("client") {
            val clientNavController = rememberNavController()
            ClientNavGraph(
                navController = clientNavController,
                userPreferences = userPreferences,
                onLogout = {
                    kotlinx.coroutines.runBlocking {
                        onClearSession()
                    }
                    currentAccountType = null
                    rootNavController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }

    // Auto-navigate si ya hay un account_type guardado al iniciar
    LaunchedEffect(currentAccountType) {
        if (currentAccountType != null) {
            when (currentAccountType) {
                "CONECTA_PRO" -> {
                    if (rootNavController.currentDestination?.route != "worker") {
                        rootNavController.navigate("worker") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
                "CLIENTE" -> {
                    if (rootNavController.currentDestination?.route != "client") {
                        rootNavController.navigate("client") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthNavGraphWrapper(
    navController: androidx.navigation.NavHostController,
    loginViewModel: LoginViewModel,
    registrationViewModel: RegistrationViewModel,
    identityVerificationViewModel: IdentityVerificationViewModel,
    onLoginSuccess: (String) -> Unit
) {
    // Wrapper para interceptar el login success y obtener el account_type
    LaunchedEffect(loginViewModel.uiState) {
        loginViewModel.uiState.collect { state ->
            if (state.loginSuccess && state.accountType != null) {
                onLoginSuccess(state.accountType)
            }
        }
    }

    AuthNavGraph(
        navController = navController,
        loginViewModel = loginViewModel,
        registrationViewModel = registrationViewModel,
        identityVerificationViewModel = identityVerificationViewModel
    )
}
