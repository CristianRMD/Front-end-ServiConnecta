package com.example.serviconnecta.feature.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.serviconnecta.feature.client.ui.booking.BookingSummaryScreen
import com.example.serviconnecta.feature.client.ui.booking.PaymentMethodScreen
import com.example.serviconnecta.feature.client.ui.booking.SelectDateTimeScreen
import com.example.serviconnecta.feature.client.ui.home.ClientHomeScreen
import com.example.serviconnecta.feature.client.ui.profile.ClientProfileScreen
import com.example.serviconnecta.feature.client.ui.reservations.MyReservationsScreen
import com.example.serviconnecta.feature.client.ui.services.ServiceDetailScreen
import com.example.serviconnecta.feature.client.ui.services.ServicesByCategoryScreen
import com.example.serviconnecta.feature.client.ui.provider.ProviderDetailScreen
import com.example.serviconnecta.feature.client.ui.search.SearchScreen
import com.example.serviconnecta.feature.shared.ui.ChangePasswordScreen
import com.example.serviconnecta.feature.shared.ui.EditProfileScreen

@Composable
fun ClientNavGraph(
    navController: NavHostController,
    userPreferences: com.example.serviconnecta.core.datastore.UserPreferences,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.ClientHome.route
    ) {
        composable(AppDestination.ClientHome.route) {
            ClientHomeScreen(
                onNavigateToSearch = {
                    navController.navigate(AppDestination.ClientSearch.route)
                },
                onNavigateToCategory = { category ->
                    navController.navigate(AppDestination.ClientServicesByCategory.createRoute(category))
                },
                onNavigateToServicesList = {
                    navController.navigate(AppDestination.ClientServicesList.route)
                },
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(AppDestination.ClientServiceDetail.createRoute(serviceId))
                },
                onNavigateToReservations = {
                    navController.navigate(AppDestination.ClientReservations.route)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestination.ClientProfile.route)
                },
                onNavigateToProvider = { providerId ->
                    navController.navigate(AppDestination.ClientProviderDetail.createRoute(providerId))
                }
            )
        }

        composable(AppDestination.ClientSearch.route) {
            SearchScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(AppDestination.ClientServiceDetail.createRoute(serviceId))
                }
            )
        }

        composable(AppDestination.ClientServicesList.route) {
            // Services list placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = AppDestination.ClientServicesByCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: return@composable
            ServicesByCategoryScreen(
                category = category,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(AppDestination.ClientServiceDetail.createRoute(serviceId))
                }
            )
        }

        composable(
            route = AppDestination.ClientProviderDetail.route,
            arguments = listOf(navArgument("providerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId") ?: return@composable
            ProviderDetailScreen(
                providerId = providerId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(AppDestination.ClientServiceDetail.createRoute(serviceId))
                }
            )
        }

        composable(
            route = AppDestination.ClientServiceDetail.route,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: return@composable
            ServiceDetailScreen(
                serviceId = serviceId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToBooking = { sid ->
                    navController.navigate(AppDestination.ClientBookingSummary.createRoute(sid))
                }
            )
        }

        composable(
            route = AppDestination.ClientBookingSummary.route,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: return@composable
            BookingSummaryScreen(
                serviceId = serviceId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToDateTime = { sid ->
                    navController.navigate(AppDestination.ClientSelectDateTime.createRoute(sid))
                }
            )
        }

        composable(
            route = AppDestination.ClientSelectDateTime.route,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: return@composable
            SelectDateTimeScreen(
                serviceId = serviceId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToPayment = { sid, date, time, locationId ->
                    navController.navigate(AppDestination.ClientPaymentMethod.createRoute(sid, date, time, locationId))
                }
            )
        }

        composable(
            route = AppDestination.ClientPaymentMethod.route,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("locationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: return@composable
            val date = backStackEntry.arguments?.getString("date") ?: return@composable
            val time = backStackEntry.arguments?.getString("time") ?: return@composable
            val locationId = backStackEntry.arguments?.getString("locationId") ?: return@composable

            PaymentMethodScreen(
                serviceId = serviceId,
                date = date,
                time = time,
                locationId = locationId,
                onNavigateBack = { navController.navigateUp() },
                onPaymentSuccess = {
                    navController.navigate(AppDestination.ClientReservations.route) {
                        popUpTo(AppDestination.ClientHome.route)
                    }
                }
            )
        }

        composable(AppDestination.ClientReservations.route) {
            MyReservationsScreen(
                onNavigateToHome = {
                    navController.navigate(AppDestination.ClientHome.route) {
                        popUpTo(AppDestination.ClientHome.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestination.ClientProfile.route)
                },
                onNavigateToReview = { bookingId ->
                    navController.navigate(AppDestination.ClientWriteReview.createRoute(bookingId))
                }
            )
        }

        composable(
            route = AppDestination.ClientWriteReview.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) {
            // Review screen placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.ClientProfile.route) {
            ClientProfileScreen(
                userPreferences = userPreferences,
                onNavigateToHome = {
                    navController.navigate(AppDestination.ClientHome.route) {
                        popUpTo(AppDestination.ClientHome.route) { inclusive = true }
                    }
                },
                onNavigateToReservations = {
                    navController.navigate(AppDestination.ClientReservations.route)
                },
                onNavigateToEditProfile = {
                    navController.navigate(AppDestination.ClientEditProfile.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(AppDestination.ClientChangePassword.route)
                },
                onNavigateToMyReservations = {
                    navController.navigate(AppDestination.ClientReservations.route)
                },
                onNavigateToLocations = {
                    navController.navigate(AppDestination.ClientLocations.route)
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate(AppDestination.ClientPrivacyPolicy.route)
                },
                onNavigateToTerms = {
                    navController.navigate(AppDestination.ClientTerms.route)
                },
                onLogout = onLogout
            )
        }

        composable(AppDestination.ClientEditProfile.route) {
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.ClientChangePassword.route) {
            ChangePasswordScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(AppDestination.ClientLocations.route) {
            // Locations screen placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.ClientPrivacyPolicy.route) {
            // Privacy policy placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.ClientTerms.route) {
            // Terms placeholder
            EditProfileScreen(
                userPreferences = userPreferences,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
