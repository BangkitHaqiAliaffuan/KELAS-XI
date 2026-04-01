package com.kelasxi.myapplication.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelasxi.myapplication.R
import com.kelasxi.myapplication.ui.auth.LoginScreen
import com.kelasxi.myapplication.ui.auth.RegisterScreen
import com.kelasxi.myapplication.ui.home.HomeScreen
import com.kelasxi.myapplication.ui.home.PickupDetailScreen
import com.kelasxi.myapplication.ui.marketplace.AddListingScreen
import com.kelasxi.myapplication.ui.marketplace.CartScreen
import com.kelasxi.myapplication.ui.marketplace.CartCheckoutScreen
import com.kelasxi.myapplication.ui.marketplace.EditListingScreen
import com.kelasxi.myapplication.ui.marketplace.MarketplaceScreen
import com.kelasxi.myapplication.ui.marketplace.PaymentScreen
import com.kelasxi.myapplication.ui.marketplace.ProductDetailScreen
import com.kelasxi.myapplication.ui.onboarding.OnboardingScreen
import com.kelasxi.myapplication.ui.onboarding.SplashScreen
import com.kelasxi.myapplication.ui.profile.MyOrdersScreen
import com.kelasxi.myapplication.ui.profile.MyShopScreen
import com.kelasxi.myapplication.ui.profile.AddressScreen
import com.kelasxi.myapplication.ui.profile.ProfileScreen
import com.kelasxi.myapplication.ui.profile.WishlistScreen
import com.kelasxi.myapplication.ui.courier.CourierHomeScreen
import com.kelasxi.myapplication.ui.courier.CourierRouteScreen
import com.kelasxi.myapplication.ui.map.MapPickerScreen
import com.kelasxi.myapplication.ui.map.MapPickerResult
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.kelasxi.myapplication.viewmodel.AddressViewModel
import com.kelasxi.myapplication.viewmodel.HomeViewModel
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel
import com.kelasxi.myapplication.viewmodel.AuthViewModel
import com.kelasxi.myapplication.viewmodel.CourierViewModel

data class BottomNavItem(
    val labelRes: Int,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(R.string.nav_home, Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(R.string.nav_marketplace, Screen.Marketplace.route, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    BottomNavItem(R.string.nav_profile, Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun TrashCareNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val homeViewModel: HomeViewModel = viewModel()
    val marketplaceViewModel: MarketplaceViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val addressViewModel: AddressViewModel = viewModel()
    val courierViewModel: CourierViewModel = viewModel()

    // Navigate to Login only after logout() fully completes (coroutine finished)
    LaunchedEffect(Unit) {
        authViewModel.logoutEvent.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Navigate courier to their home when isCourierLoggedIn becomes true
    val authUiState by authViewModel.uiState.collectAsState()
    LaunchedEffect(authUiState.isCourierLoggedIn) {
        if (authUiState.isCourierLoggedIn) {
            navController.navigate(Screen.CourierHome.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
                launchSingleTop = true
            }
            // Token is now stored — load courier data immediately
            courierViewModel.refresh()
        }
    }

    // Reload data for Home & Marketplace as soon as user login is confirmed
    // This handles the case where Google Sign-In completes after ViewModels are already created
    LaunchedEffect(authUiState.isLoggedIn) {
        if (authUiState.isLoggedIn) {
            homeViewModel.loadPickups()
            marketplaceViewModel.loadProducts()
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
                                    contentDescription = stringResource(item.labelRes),
                                    modifier = Modifier.scale(scale)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.labelRes),
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
            modifier = Modifier.padding(innerPadding),
            // UX: Transisi layar yang smooth — slide dari kanan saat forward, kiri saat back
            enterTransition       = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
            exitTransition        = { slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(200)) },
            popEnterTransition    = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
            popExitTransition     = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(200)) }
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onLoginAsCourier = {
                        navController.navigate(Screen.CourierHome.route) {
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
            composable(Screen.Home.route) { backEntry ->
                // Observe MapPicker results returned via savedStateHandle
                val mapLat = backEntry.savedStateHandle
                    .getStateFlow<Double?>(MapPickerResult.KEY_LAT, null)
                    .collectAsState().value
                val mapLng = backEntry.savedStateHandle
                    .getStateFlow<Double?>(MapPickerResult.KEY_LNG, null)
                    .collectAsState().value
                val mapAddress = backEntry.savedStateHandle
                    .getStateFlow(MapPickerResult.KEY_ADDRESS, "")
                    .collectAsState().value

                LaunchedEffect(mapLat, mapLng) {
                    if (mapLat != null && mapLng != null) {
                        homeViewModel.updateCoordinates(mapLat, mapLng, mapAddress)
                        // Clear so a second visit doesn't re-apply stale values
                        backEntry.savedStateHandle.remove<Double>(MapPickerResult.KEY_LAT)
                        backEntry.savedStateHandle.remove<Double>(MapPickerResult.KEY_LNG)
                        backEntry.savedStateHandle.remove<String>(MapPickerResult.KEY_ADDRESS)
                    }
                }

                HomeScreen(
                    viewModel = homeViewModel,
                    addressViewModel = addressViewModel,
                    onPickupClick = { pickup ->
                        homeViewModel.selectPickup(pickup)
                        navController.navigate(Screen.PickupDetail.createRoute(pickup.id))
                    },
                    onPickLocationClick = {
                        navController.navigate(Screen.MapPicker.route)
                    }
                )
            }
            composable(Screen.Marketplace.route) {
                MarketplaceScreen(
                    viewModel = marketplaceViewModel,
                    onProductClick = { product ->
                        marketplaceViewModel.selectProduct(product)
                        navController.navigate(Screen.ProductDetail.createRoute(product.id))
                    },
                    onCartClick = { navController.navigate(Screen.Cart.route) }
                )
            }
            composable(Screen.Cart.route) {
                CartScreen(
                    viewModel = marketplaceViewModel,
                    onBack = { navController.popBackStack() },
                    onCheckout = { _ ->
                        // CartCheckoutScreen handles address + calls checkoutCart()
                        // We navigate to a pre-checkout address screen
                        navController.navigate("cart_checkout_address")
                    }
                )
            }
            // Cart checkout address+review screen
            composable("cart_checkout_address") { backEntry ->
                // Observe MapPicker results returned via savedStateHandle
                val mapLat = backEntry.savedStateHandle
                    .getStateFlow<Double?>(MapPickerResult.KEY_LAT, null)
                    .collectAsState().value
                val mapLng = backEntry.savedStateHandle
                    .getStateFlow<Double?>(MapPickerResult.KEY_LNG, null)
                    .collectAsState().value

                LaunchedEffect(mapLat, mapLng) {
                    if (mapLat != null && mapLng != null) {
                        marketplaceViewModel.updateCheckoutLocation(mapLat, mapLng)
                        backEntry.savedStateHandle.remove<Double>(MapPickerResult.KEY_LAT)
                        backEntry.savedStateHandle.remove<Double>(MapPickerResult.KEY_LNG)
                        backEntry.savedStateHandle.remove<String>(MapPickerResult.KEY_ADDRESS)
                    }
                }

                CartCheckoutScreen(
                    viewModel = marketplaceViewModel,
                    onBack    = { navController.popBackStack() },
                    onPickLocationClick = {
                        navController.navigate(Screen.MapPicker.route)
                    },
                    onPaymentReady = { cartCheckoutId, paymentLink ->
                        navController.navigate(
                            Screen.CartCheckout.createRoute(cartCheckoutId, paymentLink)
                        ) {
                            popUpTo("cart_checkout_address") { inclusive = true }
                        }
                    }
                )
            }
            // CartCheckout payment screen: polls status after Mayar link opened
            composable(
                route = Screen.CartCheckout.route,
                arguments = listOf(
                    navArgument("cartCheckoutId") { type = NavType.StringType },
                    navArgument("paymentLink")    { type = NavType.StringType }
                )
            ) { backEntry ->
                val cartCheckoutId  = backEntry.arguments?.getString("cartCheckoutId") ?: ""
                val encodedLink     = backEntry.arguments?.getString("paymentLink") ?: ""
                val paymentLink     = java.net.URLDecoder.decode(encodedLink, "UTF-8")
                CartCheckoutScreen(
                    viewModel       = marketplaceViewModel,
                    onBack          = { navController.popBackStack() },
                    cartCheckoutId  = cartCheckoutId,
                    paymentLink     = paymentLink,
                    onPaymentReady  = { _, _ -> },
                    onPaid          = {
                        navController.navigate(Screen.MyOrders.route) {
                            popUpTo(Screen.Cart.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.ProductDetail.route) { backEntry ->
                val mapLat     = backEntry.savedStateHandle.get<Double>(MapPickerResult.KEY_LAT)
                val mapLng     = backEntry.savedStateHandle.get<Double>(MapPickerResult.KEY_LNG)
                val mapAddress = backEntry.savedStateHandle.get<String>(MapPickerResult.KEY_ADDRESS)
                LaunchedEffect(mapLat, mapLng) {
                    if (mapLat != null && mapLng != null) {
                        marketplaceViewModel.updateBuyNowLocation(mapLat, mapLng, mapAddress)
                        backEntry.savedStateHandle.remove<Double>(MapPickerResult.KEY_LAT)
                        backEntry.savedStateHandle.remove<Double>(MapPickerResult.KEY_LNG)
                        backEntry.savedStateHandle.remove<String>(MapPickerResult.KEY_ADDRESS)
                    }
                }
                ProductDetailScreen(
                    viewModel = marketplaceViewModel,
                    addressViewModel = addressViewModel,
                    onBack = { navController.popBackStack() },
                    onPickLocationClick = { navController.navigate(Screen.MapPicker.route) },
                    onOrderSuccess = {
                        navController.navigate(Screen.MyOrders.route) {
                            popUpTo(Screen.ProductDetail.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout      = { authViewModel.logout() },
                    onMyOrders    = { navController.navigate(Screen.MyOrders.route) },
                    onWishlist    = { navController.navigate(Screen.Wishlist.route) },
                    onMyShop      = { navController.navigate(Screen.MyShop.route) },
                    onAddresses   = { navController.navigate(Screen.Addresses.route) },
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel
                )
            }
            composable(Screen.MyOrders.route) {
                MyOrdersScreen(
                    viewModel = marketplaceViewModel,
                    onBack = { navController.popBackStack() },
                    onPayOrder = { orderId, paymentLink, paymentId ->
                        navController.navigate(
                            Screen.Payment.createRoute(orderId, paymentLink, paymentId)
                        )
                    },
                    onPayCartCheckout = { cartCheckoutId, paymentLink ->
                        navController.navigate(
                            Screen.CartCheckout.createRoute(cartCheckoutId, paymentLink)
                        )
                    },
                    onNavigateRoute = { lat, lng, address ->
                        navController.navigate(Screen.CourierRoute.createRoute(lat, lng, address))
                    }
                )
            }
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    viewModel = marketplaceViewModel,
                    onBack = { navController.popBackStack() },
                    onProductClick = { product ->
                        marketplaceViewModel.selectProduct(product)
                        navController.navigate(Screen.ProductDetail.createRoute(product.id))
                    }
                )
            }
            composable(Screen.MyShop.route) {
                MyShopScreen(
                    viewModel    = marketplaceViewModel,
                    onBack       = { navController.popBackStack() },
                    onAddListing = { navController.navigate(Screen.AddListing.route) },
                    onManage     = { productId ->
                        navController.navigate(Screen.EditListing.createRoute(productId))
                    }
                )
            }
            composable(Screen.AddListing.route) {
                AddListingScreen(
                    viewModel = marketplaceViewModel,
                    onBack    = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }
            composable(Screen.EditListing.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                EditListingScreen(
                    productId = productId,
                    viewModel = marketplaceViewModel,
                    onBack    = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }
            composable(Screen.Addresses.route) {
                AddressScreen(
                    viewModel = addressViewModel,
                    onBack    = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.Payment.ROUTE_WITH_QUERY,
                arguments = listOf(
                    navArgument("orderId")     { type = NavType.LongType },
                    navArgument("paymentId")   { type = NavType.StringType },
                    navArgument("paymentLink") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backEntry ->
                val orderId     = backEntry.arguments?.getLong("orderId") ?: 0L
                val paymentId   = backEntry.arguments?.getString("paymentId") ?: ""
                val encodedLink = backEntry.arguments?.getString("paymentLink") ?: ""
                val paymentLink = java.net.URLDecoder.decode(encodedLink, "UTF-8")
                PaymentScreen(
                    orderId     = orderId,
                    paymentLink = paymentLink,
                    paymentId   = paymentId,
                    viewModel   = marketplaceViewModel,
                    onBack      = { navController.popBackStack() },
                    onPaid      = {
                        navController.navigate(Screen.MyOrders.route) {
                            popUpTo(Screen.MyOrders.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.PickupDetail.route) {
                val pickup         = homeViewModel.selectedPickup.collectAsState().value
                val isRatingPickup = homeViewModel.isRatingPickup.collectAsState().value
                val rateSuccess    = homeViewModel.ratePickupSuccess.collectAsState().value
                if (pickup != null) {
                    PickupDetailScreen(
                        pickup         = pickup,
                        onBack         = {
                            homeViewModel.clearSelectedPickup()
                            navController.popBackStack()
                        },
                        onNavigateRoute = { lat, lng, address ->
                            navController.navigate(Screen.CourierRoute.createRoute(lat, lng, address))
                        },
                        onRatePickup   = { rating, review ->
                            homeViewModel.ratePickup(pickup.id, rating, review)
                        },
                        isRatingPickup = isRatingPickup,
                        rateSuccessMessage = rateSuccess,
                        onRateSuccessDismissed = homeViewModel::dismissRatePickupSuccess
                    )
                }
            }
            composable(Screen.CourierHome.route) {
                CourierHomeScreen(
                    viewModel = courierViewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateRoute = { lat, lng, address ->
                        navController.navigate(Screen.CourierRoute.createRoute(lat, lng, address))
                    }
                )
            }
            composable(Screen.MapPicker.route) {
                MapPickerScreen(navController = navController)
            }
            composable(
                route = Screen.CourierRoute.route,
                arguments = listOf(
                    navArgument("lat") { type = NavType.StringType },
                    navArgument("lng") { type = NavType.StringType },
                    navArgument("address") { type = NavType.StringType }
                )
            ) { backEntry ->
                val lat = backEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
                val lng = backEntry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0
                val address = java.net.URLDecoder.decode(
                    backEntry.arguments?.getString("address") ?: "", "UTF-8"
                )
                CourierRouteScreen(
                    destLat = lat,
                    destLng = lng,
                    destAddress = address,
                    navController = navController
                )
            }
        }
    }
}
