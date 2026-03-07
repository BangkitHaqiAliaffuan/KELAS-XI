package com.kelasxi.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Marketplace : Screen("marketplace")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object MyOrders : Screen("my_orders")
    object Wishlist : Screen("wishlist")
    object MyShop : Screen("my_shop")
    object AddListing : Screen("add_listing")
    object Addresses : Screen("addresses")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object EditListing : Screen("edit_listing/{productId}") {
        fun createRoute(productId: String) = "edit_listing/$productId"
    }
    object PickupDetail : Screen("pickup_detail/{pickupId}") {
        fun createRoute(pickupId: String) = "pickup_detail/$pickupId"
    }
    object CourierHome : Screen("courier_home")
    object MapPicker : Screen("map_picker")
    object CourierRoute : Screen("courier_route/{lat}/{lng}/{address}") {
        fun createRoute(lat: Double, lng: Double, address: String): String {
            val encoded = java.net.URLEncoder.encode(address, "UTF-8")
            return "courier_route/$lat/$lng/$encoded"
        }
    }
    object Payment : Screen("payment/{orderId}/{paymentId}") {
        fun createRoute(orderId: Long, paymentLink: String, paymentId: String): String {
            val encodedLink = java.net.URLEncoder.encode(paymentLink, "UTF-8")
            return "payment/$orderId/$paymentId?link=$encodedLink"
        }
        const val ROUTE_WITH_QUERY = "payment/{orderId}/{paymentId}?link={paymentLink}"
    }
    /** Cart checkout: address + items review before paying */
    object CartCheckout : Screen("cart_checkout/{cartCheckoutId}/{paymentLink}") {
        fun createRoute(cartCheckoutId: String, paymentLink: String): String {
            val encodedLink = java.net.URLEncoder.encode(paymentLink, "UTF-8")
            return "cart_checkout/$cartCheckoutId/$encodedLink"
        }
    }
}
