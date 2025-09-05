package com.kelasxi.waveoffood.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kelasxi.waveoffood.ui.screens.auth.LoginScreen
import com.kelasxi.waveoffood.ui.screens.auth.RegisterScreen
import com.kelasxi.waveoffood.ui.screens.home.HomeScreen
import com.kelasxi.waveoffood.ui.screens.onboarding.OnboardingScreen
import com.kelasxi.waveoffood.ui.screens.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
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
                }
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
        
        // Cart Screen (placeholder)
        composable(
            route = Screen.Cart.route
        ) {
            // TODO: Implement CartScreen
            PlaceholderScreen(
                title = "Cart",
                subtitle = "Your selected items",
                onBackClick = { navController.popBackStack() }
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
