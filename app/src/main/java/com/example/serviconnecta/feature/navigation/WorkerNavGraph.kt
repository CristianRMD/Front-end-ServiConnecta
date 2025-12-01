package com.example.serviconnecta.feature.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.serviconnecta.feature.shared.ui.ChangePasswordScreen
import com.example.serviconnecta.feature.shared.ui.EditProfileScreen
import com.example.serviconnecta.feature.worker.ui.home.WorkerHomeScreen
import com.example.serviconnecta.feature.worker.ui.profile.WorkerProfileScreen
import com.example.serviconnecta.feature.worker.ui.requests.RequestsScreen
import com.example.serviconnecta.feature.worker.ui.services.AddServiceScreen
import com.example.serviconnecta.feature.worker.ui.services.WorkerServicesScreen

@Composable
fun WorkerNavGraph(
    navController: NavHostController,
    userPreferences: com.example.serviconnecta.core.datastore.UserPreferences,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.WorkerHome.route
    ) {
        composable(AppDestination.WorkerHome.route) {
            WorkerHomeScreen(
                userPreferences = userPreferences,
                onNavigateToServices = {
                    navController.navigate(AppDestination.WorkerServices.route)
                },
                onNavigateToRequests = {
                    navController.navigate(AppDestination.WorkerRequests.route)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestination.WorkerProfile.route)
                }
            )
        }

        composable(AppDestination.WorkerServices.route) {
            WorkerServicesScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddService = {
                    navController.navigate(AppDestination.WorkerAddService.route)
                },
                onNavigateToServiceDetail = { serviceId ->
                    // Navigate to service detail
                },
                onNavigateToRequests = {
                    navController.navigate(AppDestination.WorkerRequests.route)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestination.WorkerProfile.route)
                }
            )
        }

        composable(AppDestination.WorkerAddService.route) {
            AddServiceScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.WorkerRequests.route) {
            RequestsScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToProfile = {
                    navController.navigate(AppDestination.WorkerProfile.route)
                }
            )
        }

        composable(AppDestination.WorkerProfile.route) {
            WorkerProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToServices = {
                    navController.navigate(AppDestination.WorkerServices.route)
                },
                onNavigateToEditProfile = {
                    navController.navigate(AppDestination.WorkerEditProfile.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(AppDestination.WorkerChangePassword.route)
                },
                onNavigateToReviews = {
                    navController.navigate(AppDestination.WorkerMyReviews.route)
                },
                onNavigateToAreas = {
                    navController.navigate(AppDestination.WorkerMyAreas.route)
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate(AppDestination.WorkerPrivacyPolicy.route)
                },
                onNavigateToTerms = {
                    navController.navigate(AppDestination.WorkerTerms.route)
                },
                onLogout = onLogout
            )
        }

        composable(AppDestination.WorkerEditProfile.route) {
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.WorkerChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.WorkerMyReviews.route) {
            // Reviews screen placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.WorkerMyAreas.route) {
            // Areas screen placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.WorkerPrivacyPolicy.route) {
            // Privacy policy screen placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.WorkerTerms.route) {
            // Terms screen placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
