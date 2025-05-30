package com.example.testproject.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.testproject.presentation.login.LoginScreen
import com.example.testproject.presentation.otp.OtpScreen
import com.example.testproject.presentation.product.ProductListScreen
import com.example.testproject.presentation.ticket.CreateTicketScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController)
        }

        composable(
            route = "otp_screen/{phone}/{code}",
            arguments = listOf(
                navArgument("phone") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            val code = backStackEntry.arguments?.getString("code") ?: ""
            OtpScreen(navController = navController, phoneNumber = phone, countryCode = code)
        }

        composable("shopScreen") {

            ProductListScreen(navController = navController)
        }

        composable("createticketscreen") { // Define the new composable destination
            // You might want to pass the user's name if you have it in a ViewModel
            CreateTicketScreen(userName = "mustakim") // Replace with actual user name
        }


    }
}
