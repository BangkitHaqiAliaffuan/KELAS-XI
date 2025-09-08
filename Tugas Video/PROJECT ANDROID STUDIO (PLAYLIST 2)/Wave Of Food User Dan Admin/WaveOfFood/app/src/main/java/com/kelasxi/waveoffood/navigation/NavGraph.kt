package com.kelasxi.waveoffood.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kelasxi.waveoffood.ui.screens.auth.LoginScreen
import com.kelasxi.waveoffood.ui.screens.auth.RegisterScreen
import com.kelasxi.waveoffood.ui.screens.cart.CartScreen
import com.kelasxi.waveoffood.ui.screens.cart.CheckoutScreen
import com.kelasxi.waveoffood.ui.screens.cart.OrderSuccessScreen
import com.kelasxi.waveoffood.ui.screens.home.HomeScreen
import com.kelasxi.waveoffood.ui.screens.onboarding.OnboardingScreen
import com.kelasxi.waveoffood.ui.screens.splash.SplashScreen
import com.kelasxi.waveoffood.ui.viewmodels.CartViewModel
import com.kelasxi.waveoffood.navigation.PlaceholderScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    // Shared CartViewModel across all screens
    val cartViewModel: CartViewModel = viewModel()
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(
            route = Screen.Splash.route
        ) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding Screen
        composable(
            route = Screen.Onboarding.route
        ) {
            OnboardingScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Login Screen
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        
        // Register Screen
        composable(
            route = Screen.Register.route
        ) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Home Screen
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(
                onNavigateToRestaurant = { restaurantId ->
                    navController.navigate(Screen.RestaurantDetail.createRoute(restaurantId))
                },
                onNavigateToFood = { foodId ->
                    navController.navigate(Screen.FoodDetail.createRoute(foodId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                cartViewModel = cartViewModel
            )
        }
        
        // Restaurant Detail Screen (placeholder)
        composable(
            route = Screen.RestaurantDetail.route
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            // TODO: Implement RestaurantDetailScreen
            PlaceholderScreen(
                title = "Restaurant Detail",
                subtitle = "Restaurant ID: $restaurantId",
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Food Detail Screen (placeholder)
        composable(
            route = Screen.FoodDetail.route
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            // TODO: Implement FoodDetailScreen
            PlaceholderScreen(
                title = "Food Detail",
                subtitle = "Food ID: $foodId",
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Profile Screen (placeholder)
        composable(
            route = Screen.Profile.route
        ) {
            // TODO: Implement ProfileScreen
            PlaceholderScreen(
                title = "Profile",
                subtitle = "User profile and settings",
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Cart Screen
        composable(
            route = Screen.Cart.route
        ) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { cartSummary ->
                    navController.navigate(Screen.Checkout.route)
                },
                cartViewModel = cartViewModel
            )
        }
        
        // Checkout Screen
        composable(
            route = Screen.Checkout.route
        ) {
            val cartSummary = cartViewModel.getCartSummary()
            CheckoutScreen(
                cartSummary = cartSummary,
                onNavigateBack = { navController.popBackStack() },
                onOrderPlaced = { orderId ->
                    navController.navigate("order_success/$orderId") {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                cartViewModel = cartViewModel
            )
        }
        
        // Order Success Screen
        composable(
            route = "order_success/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderSuccessScreen(
                orderId = orderId,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.OrderSuccess.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Forgot Password Screen (placeholder)
        composable(
            route = Screen.ForgotPassword.route
        ) {
            // TODO: Implement ForgotPasswordScreen
            PlaceholderScreen(
                title = "Forgot Password",
                subtitle = "Reset your password",
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
