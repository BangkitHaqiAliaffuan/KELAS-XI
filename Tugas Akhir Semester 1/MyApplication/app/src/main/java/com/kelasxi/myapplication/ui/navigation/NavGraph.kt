package com.kelasxi.myapplication.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelasxi.myapplication.ui.auth.LoginScreen
import com.kelasxi.myapplication.ui.auth.RegisterScreen
import com.kelasxi.myapplication.ui.home.HomeScreen
import com.kelasxi.myapplication.ui.marketplace.MarketplaceScreen
import com.kelasxi.myapplication.ui.marketplace.ProductDetailScreen
import com.kelasxi.myapplication.ui.onboarding.OnboardingScreen
import com.kelasxi.myapplication.ui.onboarding.SplashScreen
import com.kelasxi.myapplication.ui.profile.MyOrdersScreen
import com.kelasxi.myapplication.ui.profile.ProfileScreen
import com.kelasxi.myapplication.ui.profile.WishlistScreen
import com.kelasxi.myapplication.viewmodel.HomeViewModel
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel
import com.kelasxi.myapplication.viewmodel.AuthViewModel

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Marketplace", Screen.Marketplace.route, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    BottomNavItem("Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun TrashCareNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val homeViewModel: HomeViewModel = viewModel()
    val marketplaceViewModel: MarketplaceViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // Navigate to Login only after logout() fully completes (coroutine finished)
    LaunchedEffect(Unit) {
        authViewModel.logoutEvent.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val screensWithBottomNav = listOf(Screen.Home.route, Screen.Marketplace.route, Screen.Profile.route)
    val showBottomBar = currentDestination?.route in screensWithBottomNav

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = androidx.compose.ui.unit.Dp(8f)
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.15f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "navIconScale"
                        )

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.scale(scale)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    authViewModel = authViewModel
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    authViewModel = authViewModel
                )
            }
            composable(Screen.Splash.route) {
                SplashScreen(onSplashDone = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onGetStarted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(viewModel = homeViewModel)
            }
            composable(Screen.Marketplace.route) {
                MarketplaceScreen(
                    viewModel = marketplaceViewModel,
                    onProductClick = { product ->
                        marketplaceViewModel.selectProduct(product)
                        navController.navigate(Screen.ProductDetail.createRoute(product.id))
                    }
                )
            }
            composable(Screen.ProductDetail.route) {
                ProductDetailScreen(
                    viewModel = marketplaceViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = { authViewModel.logout() },
                    onMyOrders = { navController.navigate(Screen.MyOrders.route) },
                    onWishlist = { navController.navigate(Screen.Wishlist.route) }
                )
            }
            composable(Screen.MyOrders.route) {
                MyOrdersScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    onBack = { navController.popBackStack() },
                    onProductClick = { product ->
                        marketplaceViewModel.selectProduct(product)
                        navController.navigate(Screen.ProductDetail.createRoute(product.id))
                    }
                )
            }
        }
    }
}
