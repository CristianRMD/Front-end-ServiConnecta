package com.example.serviconnecta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.serviconnecta.core.datastore.AuthPreferences
import com.example.serviconnecta.core.datastore.UserPreferences
import com.example.serviconnecta.core.datastore.authDataStore
import com.example.serviconnecta.core.datastore.userDataStore
import com.example.serviconnecta.core.network.AuthInterceptor
import com.example.serviconnecta.core.network.NetworkMonitor
import com.example.serviconnecta.core.network.NoInternetDialog
import com.example.serviconnecta.core.network.RetrofitProvider
import com.example.serviconnecta.feature.auth.data.AuthRepositoryImpl
import com.example.serviconnecta.feature.auth.data.IdentityVerificationRepository
import com.example.serviconnecta.feature.auth.data.UserProfileRepository
import com.example.serviconnecta.feature.auth.data.remote.AuthApiService
import com.example.serviconnecta.feature.auth.data.remote.IdentityVerificationApiService
import com.example.serviconnecta.feature.auth.data.remote.UserApiService
import com.example.serviconnecta.feature.auth.domain.usecase.*
import com.example.serviconnecta.feature.auth.ui.identity.IdentityVerificationViewModel
import com.example.serviconnecta.feature.auth.ui.login.LoginViewModel
import com.example.serviconnecta.feature.auth.ui.register.RegistrationViewModel
import com.example.serviconnecta.feature.navigation.MainNavGraph
import com.example.serviconnecta.ui.theme.ServiconnectaTheme
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DataStore
        val authPreferences = AuthPreferences(authDataStore)
        val userPreferences = UserPreferences(userDataStore)

        // --- Monitor de red global ---
        networkMonitor = NetworkMonitor(applicationContext)

        // Retrofit sin auth (para login/register)
        val retrofit = RetrofitProvider.createRetrofit()

        // Interceptor que lee el accessToken de DataStore
        val authInterceptor = AuthInterceptor {
            // Bloqueamos brevemente para leer el token actual
            runBlocking {
                authPreferences.accessTokenFlow.firstOrNull()
            }
        }

        // Retrofit con auth (para endpoints que requieren Authorization)
        val retrofitAuthed = RetrofitProvider.createAuthRetrofit(authInterceptor)

        // APIs
        val authApi = retrofit.create(AuthApiService::class.java)
        val userApi = retrofitAuthed.create(UserApiService::class.java)
        val identityApi = retrofitAuthed.create(IdentityVerificationApiService::class.java)

        // Repos
        val authRepository = AuthRepositoryImpl(authApi, authPreferences, userPreferences)
        val userProfileRepository = UserProfileRepository(userApi)
        val identityVerificationRepository = IdentityVerificationRepository(identityApi)

        // Use cases
        val loginUseCase = LoginUseCase(authRepository)
        val registerUseCase = RegisterUseCase(authRepository)
        val requestPhoneVerificationUseCase = RequestPhoneVerificationUseCase(authRepository)
        val confirmPhoneVerificationUseCase = ConfirmPhoneVerificationUseCase(authRepository)
        val updatePersonalInfoUseCase = UpdatePersonalInfoUseCase(userProfileRepository)
        val getIdentityOptionsUseCase = GetIdentityOptionsUseCase(identityVerificationRepository)
        val submitIdentityDocumentUseCase = SubmitIdentityDocumentUseCase(identityVerificationRepository)

        // ViewModel factories...

        val loginVmFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return LoginViewModel(loginUseCase) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        val registrationVmFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return RegistrationViewModel(
                        registerUseCase = registerUseCase,
                        requestPhoneVerificationUseCase = requestPhoneVerificationUseCase,
                        confirmPhoneVerificationUseCase = confirmPhoneVerificationUseCase,
                        updatePersonalInfoUseCase = updatePersonalInfoUseCase
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        val identityVmFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(IdentityVerificationViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return IdentityVerificationViewModel(
                        getIdentityOptionsUseCase = getIdentityOptionsUseCase,
                        submitIdentityDocumentUseCase = submitIdentityDocumentUseCase
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        val loginViewModel =
            ViewModelProvider(this, loginVmFactory)[LoginViewModel::class.java]

        val registrationViewModel =
            ViewModelProvider(this, registrationVmFactory)[RegistrationViewModel::class.java]

        val identityVerificationViewModel =
            ViewModelProvider(this, identityVmFactory)[IdentityVerificationViewModel::class.java]

        setContent {
            ServiconnectaTheme {
                // Observamos el estado de conexión de red
                val isOnline by networkMonitor.isOnline.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                    MainNavGraph(
                        loginViewModel = loginViewModel,
                        registrationViewModel = registrationViewModel,
                        identityVerificationViewModel = identityVerificationViewModel,
                        accountTypeFlow = authPreferences.accountTypeFlow,
                        userPreferences = userPreferences,
                        onClearSession = {
                            authPreferences.clearTokens()
                            userPreferences.clearUserData()
                        }
                    )

                    // Si NO hay internet, mostramos el diálogo bloqueante
                    if (!isOnline) {
                        NoInternetDialog()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberamos el callback de red
        if (this::networkMonitor.isInitialized) {
            networkMonitor.unregister()
        }
    }
}