package com.kelasxi.waveoffood.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object RestaurantDetail : Screen("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant_detail/$restaurantId"
    }
    object FoodDetail : Screen("food_detail/{foodId}") {
        fun createRoute(foodId: String) = "food_detail/$foodId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderSuccess : Screen("order_success")
    object Profile : Screen("profile")
    object ForgotPassword : Screen("forgot_password")
}
