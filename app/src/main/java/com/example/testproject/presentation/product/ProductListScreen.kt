package com.example.testproject.presentation.product


import ProductListViewModelFactory
import android.app.Application
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.testproject.ProductCard
import com.example.testproject.R
import com.example.testproject.data.model.Product
import com.example.testproject.data.remote.RetrofitClient
import com.example.testproject.data.repository.AuthRepositoryImpl
import com.example.testproject.ui.theme.TestProjectTheme


// Define the Tangerine Regular font family
val tangerineFontFamily = FontFamily(
    Font(R.font.tangerine_regular, weight = androidx.compose.ui.text.font.FontWeight.Normal) // Ensure you have the font file in res/font
)

// Dummy data for categories
val categories = listOf(
    Category("Serums", R.drawable.categorysample),
    Category("Cleaners", R.drawable.product1),
    Category("Toner", R.drawable.categorysample),
    Category("Moisturisers", R.drawable.product1),
    Category("Sunscreens", R.drawable.categorysample),
    Category("Masks", R.drawable.product1),
)

fun Product.getPrimaryImage(): String {
    return images.firstOrNull { it.primary }?.url ?: ""
}

data class Category(val name: String, val imageResId: Int)

@Preview(showBackground = true)
@Composable
fun PreviewShopScreen() {
    TestProjectTheme {
//        ProductListScreen()
   }
}

@Composable
fun PurpleHeartIcon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(45.dp) // Size of the white circle
            .clip(CircleShape)
            .background(Color.Black)
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Heart icon",
            modifier = Modifier.size(27.dp), // Size of the heart icon
            tint = Color(0xFFE1BEE7) // Purple
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(modifier: Modifier = Modifier
,navController: NavController,) {
    // Define the custom background color
    val backgroundColor = Color(0xFF373435) // Hex color #373435

    val context = LocalContext.current
    val application = context.applicationContext as Application // Get Application context

    val repository = AuthRepositoryImpl(RetrofitClient.api)

    // Pass the Application context to the ViewModelFactory
    val viewModel: ProductListViewModel = viewModel(
        factory = ProductListViewModelFactory(application, repository)
    )
    val isLoading = viewModel.isLoading.collectAsState()
    val productState = viewModel.productState.collectAsState()

    LaunchedEffect(productState.value) {
        Log.d("ProductListScreen", "Products from API: ${productState.value}")
    }



    Scaffold(
        modifier = modifier, // Apply the modifier passed from MainActivity
        topBar = {
            TopAppBar(


                title = {
                    Text(
                        text = "Shop",
                        style = TextStyle(color = Color.White, fontFamily = tangerineFontFamily, fontSize = 24.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Handle back button click
                        println("Back button clicked")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White // Set icon color to white for visibility
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.Top, // **<-- Apply vertical alignment to the Row containing actions**
                        modifier = Modifier.fillMaxHeight() // Ensure the row fills the height
                    ) {
                        // Search icon
                        IconButton(onClick = {
                            println("Search icon clicked")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.padding(top = 4.dp) // Example: add top padding to icon
                            )
                        }
                        // Like icon
                        IconButton(onClick = {
                            println("Like icon clicked")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Favorite",
                                tint = Color.White,
                                modifier = Modifier.padding(top = 4.dp) // Example: add top padding to icon
                            )
                        }
                        // Shop/Cart icon
                        IconButton(onClick = {
                            println("Shopping cart icon clicked")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Shopping Cart",
                                tint = Color.White,
                                modifier = Modifier.padding(top = 4.dp) // Example: add top padding to icon
                            )
                        }

                        // Add a FAB for ticket creation
                            FloatingActionButton(onClick = {navController.navigate("createticketscreen") }) {
                                Icon(Icons.Default.Add, "Create Ticket")
                            }

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor, // A slightly lighter shade for the top bar for contrast
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        content = { paddingValues ->


            Column (
                modifier = Modifier
                    .padding(paddingValues)

            ){
                // Main content area
                // Main content area with LazyColumn
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        //  .padding(start = 8.dp ,end = 8.dp) // Apply padding from Scaffold
                        .background(backgroundColor) // Set the background color to #373435
                        .padding(horizontal =4.dp), // Horizontal padding for the content
                    verticalArrangement = Arrangement.spacedBy(26.dp), // Spacing between items
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Display product images using items from LazyColumn
                    item {

                        Box(){

                            Image(
                                painter = painterResource(id = R.drawable.top_backgroud_black), // Replace with your desired drawable
                                contentDescription = "Shop Banner Image", // Meaningful content description

                            )


                            Row(
                                modifier = Modifier.padding(top = 46.dp, start = 46.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(

                                ) {
                                    Text(
                                        text = "GET 20% OFF",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Get 20% off",
                                        style = TextStyle(
                                            color = Color.White.copy(alpha = 0.7f), // Slightly transparent
                                            fontSize = 16.sp,

                                            )
                                    )
                                    Spacer(modifier = Modifier.height(25.dp))

                                    Row (
                                        modifier = Modifier.fillMaxWidth().padding(end = 40.dp, bottom = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom

                                    ) {

                                        Text(
                                            text = "12-16 October",
                                            modifier = Modifier
                                                .background(
                                                    color = Color(0xFFB2FF59), // Green background
                                                    shape = RoundedCornerShape(8.dp) // Rounded corners
                                                )
                                                .padding(horizontal = 10.dp, vertical = 3.dp),
                                            style = TextStyle(
                                                color = Color.Black, // Black text on green background
                                                fontSize = 12.sp,

                                                )
                                        )

                                        Image(
                                            painter = painterResource(id = R.drawable.image_placeholder),
                                            contentDescription = "Image description",
                                            modifier = Modifier.size(40.dp).padding(bottom = 5.dp),
                                            colorFilter = ColorFilter.tint(Color.White)
                                        )
                                    }

                                }




//
//                        Icon(
//                            imageVector = Icons.Default., // Placeholder icon
//                            contentDescription = "Promotion Icon",
//                            tint = Color.White.copy(alpha = 0.5f), // Faded white icon
//                            modifier = Modifier.size(64.dp) // Large icon size
//                        )
                            }
                        }}




                    // Categories Section
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Categories",
                                    style = TextStyle(
                                        fontFamily = tangerineFontFamily,
                                        fontSize = 24.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "See all",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    ),
                                    modifier = Modifier.padding(end = 8.dp) // Adjust padding as needed
                                )
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(categories) { category ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(80.dp) // Fixed width for each category item
                                    ) {
                                        Card(
                                            shape = CircleShape,
                                            modifier = Modifier.size(70.dp), // Size of the circular image background
                                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray) // Dark background for the circle
                                        ) {
                                            Image(
                                                painter = painterResource(id = category.imageResId),
                                                contentDescription = category.name,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp), // Padding inside the circle
                                                contentScale = ContentScale.Fit // Fit the image inside the circle
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = category.name,
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }


                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "New products",
                                    style = TextStyle(
                                        fontFamily = tangerineFontFamily,
                                        fontSize = 24.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "See all",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    ),
                                    modifier = Modifier.padding(end = 8.dp) // Adjust padding as needed
                                )
                            }
                        }
                    }



                    items(productState.value) { product ->

                        Box(){

                            Image(
                                modifier = Modifier.fillMaxWidth(),
                                painter = painterResource(id = R.drawable.grey_card_product123), // Replace with your desired drawable
                                contentDescription = "Shop Banner Image", // Meaningful content description

                            )


                            Row(
                                modifier = Modifier.padding(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally

                                ) {


                                    Row (
                                        modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {

                                        PurpleHeartIcon()

                                        Text(
                                            text = "Best seller",
                                            modifier = Modifier
                                                .background(
                                                    color = Color.Black, // Green background
                                                    shape = RoundedCornerShape(99.dp) // Rounded corners
                                                )
                                                .padding(horizontal = 16.dp, vertical = 4.dp),
                                            style = TextStyle(
                                                color = Color(0xFFB2FF59), // Black text on green background
                                                fontSize = 18.sp,

                                                )
                                        )


                                    }


                                    AsyncImage(
                                        model = product.getPrimaryImage(),
                                        contentDescription = product.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )


                                    Box(
                                        modifier= Modifier.width(340.dp)
                                            .padding(top = 70.dp, bottom = 10.dp)
                                    ){

                                        Image(

                                            painter = painterResource(id = R.drawable.black_card), // Replace with your desired drawable
                                            contentDescription = "Shop Banner Image", // Meaningful content description
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp) // Adjust the height as needed
                                        )


                                        ProductCard(product)
                                        Button(
                                            onClick = { /* TODO: Handle cart click */ },
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF535353)), // Button background
                                            modifier = Modifier
                                                .padding(bottom = 10.dp, end = 2.dp)
                                                .size(50.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFFB2FF59), // Green border
                                                    shape = CircleShape
                                                )
                                                .align(Alignment.BottomEnd),
                                            contentPadding = PaddingValues(end = 2.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.cart3), // Drawable icon
                                                contentDescription = "Add to cart",
                                                tint = Color(0xFFB2FF59), // Light green icon
                                                modifier = Modifier.size(27.dp)
                                            )
                                        }

                                    }

                                }




//
//
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }





                }}
        }
    )
}




//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ShopScreen(modifier: Modifier = Modifier) {
//    // Define the custom background color
//    val backgroundColor = Color(0xFF373435) // Hex color #373435
//
//    Scaffold(
//        modifier = modifier, // Apply the modifier passed from MainActivity
//        topBar = {
//            TopAppBar(
//
//
//                title = {
//                    Text(
//                        text = "Now",
//                        style = TextStyle(color = Color.White, fontFamily = tangerineFontFamily, fontSize = 24.sp)
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        // Handle back button click
//                        println("Back button clicked")
//                    }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color.White // Set icon color to white for visibility
//                        )
//                    }
//                },
//                actions = {
//                    Row(
//                        verticalAlignment = Alignment.Top, // **<-- Apply vertical alignment to the Row containing actions**
//                        modifier = Modifier.fillMaxHeight() // Ensure the row fills the height
//                    ) {
//                        // Search icon
//                        IconButton(onClick = {
//                            println("Search icon clicked")
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.Search,
//                                contentDescription = "Search",
//                                tint = Color.White,
//                                modifier = Modifier.padding(top = 4.dp) // Example: add top padding to icon
//                            )
//                        }
//                        // Like icon
//                        IconButton(onClick = {
//                            println("Like icon clicked")
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.Favorite,
//                                contentDescription = "Favorite",
//                                tint = Color.White,
//                                modifier = Modifier.padding(top = 4.dp) // Example: add top padding to icon
//                            )
//                        }
//                        // Shop/Cart icon
//                        IconButton(onClick = {
//                            println("Shopping cart icon clicked")
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.ShoppingCart,
//                                contentDescription = "Shopping Cart",
//                                tint = Color.White,
//                                modifier = Modifier.padding(top = 4.dp) // Example: add top padding to icon
//                            )
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = backgroundColor, // A slightly lighter shade for the top bar for contrast
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White,
//                    actionIconContentColor = Color.White
//                )
//            )
//        },
//        content = { paddingValues ->
//
//
//            Column (
//                modifier = Modifier
//                    .padding(paddingValues)
//
//            ){
//                // Main content area
//                // Main content area with LazyColumn
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        //  .padding(start = 8.dp ,end = 8.dp) // Apply padding from Scaffold
//                        .background(backgroundColor) // Set the background color to #373435
//                        .padding(horizontal =4.dp), // Horizontal padding for the content
//                    verticalArrangement = Arrangement.spacedBy(26.dp), // Spacing between items
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//
//                    // Display product images using items from LazyColumn
//                    item {
//
//                        Box(){
//
//                            Image(
//                                painter = painterResource(id = R.drawable.top_backgroud_black), // Replace with your desired drawable
//                                contentDescription = "Shop Banner Image", // Meaningful content description
//
//                            )
//
//
//                            Row(
//                                modifier = Modifier.padding(top = 46.dp, start = 46.dp),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Column(
//
//                                ) {
//                                    Text(
//                                        text = "GET 20% OFF",
//                                        style = TextStyle(
//                                            color = Color.White,
//                                            fontSize = 28.sp,
//                                            fontWeight = FontWeight.Bold)
//                                    )
//                                    Spacer(modifier = Modifier.height(10.dp))
//                                    Text(
//                                        text = "Get 20% off",
//                                        style = TextStyle(
//                                            color = Color.White.copy(alpha = 0.7f), // Slightly transparent
//                                            fontSize = 16.sp,
//
//                                            )
//                                    )
//                                    Spacer(modifier = Modifier.height(25.dp))
//
//                                    Row (
//                                        modifier = Modifier.fillMaxWidth().padding(end = 40.dp, bottom = 10.dp),
//                                        horizontalArrangement = Arrangement.SpaceBetween,
//                                        verticalAlignment = Alignment.Bottom
//
//                                    ) {
//
//                                        Text(
//                                            text = "12-16 October",
//                                            modifier = Modifier
//                                                .background(
//                                                    color = Color(0xFFB2FF59), // Green background
//                                                    shape = RoundedCornerShape(8.dp) // Rounded corners
//                                                )
//                                                .padding(horizontal = 10.dp, vertical = 3.dp),
//                                            style = TextStyle(
//                                                color = Color.Black, // Black text on green background
//                                                fontSize = 12.sp,
//
//                                                )
//                                        )
//
//                                        Image(
//                                            painter = painterResource(id = R.drawable.image_placeholder),
//                                            contentDescription = "Image description",
//                                            modifier = Modifier.size(40.dp).padding(bottom = 5.dp),
//                                            colorFilter = ColorFilter.tint(Color.White)
//                                        )
//                                    }
//
//                                }
//
//
//
//
////
////                        Icon(
////                            imageVector = Icons.Default., // Placeholder icon
////                            contentDescription = "Promotion Icon",
////                            tint = Color.White.copy(alpha = 0.5f), // Faded white icon
////                            modifier = Modifier.size(64.dp) // Large icon size
////                        )
//                            }
//                        }}
//
//
//
//
//                    // Categories Section
//                    item {
//                        Column(
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(bottom = 8.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    text = "Categories",
//                                    style = TextStyle(
//                                        fontFamily = tangerineFontFamily,
//                                        fontSize = 24.sp,
//                                        color = Color.White,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                )
//                                Text(
//                                    text = "See all",
//                                    style = TextStyle(
//                                        fontSize = 16.sp,
//                                        color = Color.White.copy(alpha = 0.7f)
//                                    ),
//                                    modifier = Modifier.padding(end = 8.dp) // Adjust padding as needed
//                                )
//                            }
//
//                            LazyRow(
//                                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                items(categories) { category ->
//                                    Column(
//                                        horizontalAlignment = Alignment.CenterHorizontally,
//                                        modifier = Modifier.width(80.dp) // Fixed width for each category item
//                                    ) {
//                                        Card(
//                                            shape = CircleShape,
//                                            modifier = Modifier.size(70.dp), // Size of the circular image background
//                                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray) // Dark background for the circle
//                                        ) {
//                                            Image(
//                                                painter = painterResource(id = category.imageResId),
//                                                contentDescription = category.name,
//                                                modifier = Modifier
//                                                    .fillMaxSize()
//                                                    .padding(8.dp), // Padding inside the circle
//                                                contentScale = ContentScale.Fit // Fit the image inside the circle
//                                            )
//                                        }
//                                        Spacer(modifier = Modifier.height(8.dp))
//                                        Text(
//                                            text = category.name,
//                                            style = TextStyle(
//                                                fontSize = 14.sp,
//                                                color = Color.White
//                                            )
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//
//                    item {
//                        Column(
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(bottom = 8.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    text = "New products",
//                                    style = TextStyle(
//                                        fontFamily = tangerineFontFamily,
//                                        fontSize = 24.sp,
//                                        color = Color.White,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                )
//                                Text(
//                                    text = "See all",
//                                    style = TextStyle(
//                                        fontSize = 16.sp,
//                                        color = Color.White.copy(alpha = 0.7f)
//                                    ),
//                                    modifier = Modifier.padding(end = 8.dp) // Adjust padding as needed
//                                )
//                            }
//                        }
//                    }
//
//
//
//
//
//
//                    item {
//
//                        Box(){
//
//                            Image(
//                                modifier = Modifier.fillMaxWidth(),
//                                painter = painterResource(id = R.drawable.grey_card_product123), // Replace with your desired drawable
//                                contentDescription = "Shop Banner Image", // Meaningful content description
//
//                            )
//
//
//                            Row(
//                                modifier = Modifier.padding(),
//                                verticalAlignment = Alignment.Top,
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Column(
//                                    horizontalAlignment = Alignment.CenterHorizontally
//
//                                ) {
//
//
//                                    Row (
//                                        modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
//                                        horizontalArrangement = Arrangement.SpaceBetween,
//                                        verticalAlignment = Alignment.CenterVertically
//
//                                    ) {
//
//                                        PurpleHeartIcon()
//
//                                        Text(
//                                            text = "Best seller",
//                                            modifier = Modifier
//                                                .background(
//                                                    color = Color.Black, // Green background
//                                                    shape = RoundedCornerShape(99.dp) // Rounded corners
//                                                )
//                                                .padding(horizontal = 16.dp, vertical = 4.dp),
//                                            style = TextStyle(
//                                                color = Color(0xFFB2FF59), // Black text on green background
//                                                fontSize = 18.sp,
//
//                                                )
//                                        )
//
//
//                                    }
//
//                                    Image(
//
//                                        painter = painterResource(id = R.drawable.product1), // Replace with your desired drawable
//                                        contentDescription = "Shop Banner Image", // Meaningful content description
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .height(290.dp) // Adjust the height as needed
//                                    )
//
//                                    Box(
//                                        modifier= Modifier.width(340.dp)
//                                    ){
//
//                                        Image(
//
//                                            painter = painterResource(id = R.drawable.black_card), // Replace with your desired drawable
//                                            contentDescription = "Shop Banner Image", // Meaningful content description
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .height(160.dp) // Adjust the height as needed
//                                        )
//
//
////                                        ProductCard(name = "clencera")
////                                    Button(
////                                        onClick = { /* TODO: Handle cart click */ },
////                                        shape = CircleShape,
////                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF535353)), // Darker grey button
////                                        modifier = Modifier.padding(bottom = 10.dp, end = 2.dp).size(50.dp).align(alignment = Alignment.BottomEnd), // Button size
////                                        contentPadding = PaddingValues(0.dp) // Remove default padding
////                                    ) {
////                                        Icon(
////                                            imageVector = Icons.Filled.ShoppingCart,
////                                            contentDescription = "Add to cart",
////                                            tint = Color(0xFFB2FF59), // Light green icon
////                                            modifier = Modifier.size(30.dp) // Icon size
////                                        )
////                                    }
//
//                                        Button(
//                                            onClick = { /* TODO: Handle cart click */ },
//                                            shape = CircleShape,
//                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF535353)), // Button background
//                                            modifier = Modifier
//                                                .padding(bottom = 10.dp, end = 2.dp)
//                                                .size(50.dp)
//                                                .border(
//                                                    width = 1.dp,
//                                                    color = Color(0xFFB2FF59), // Green border
//                                                    shape = CircleShape
//                                                )
//                                                .align(Alignment.BottomEnd),
//                                            contentPadding = PaddingValues(end = 2.dp)
//                                        ) {
//                                            Icon(
//                                                painter = painterResource(id = R.drawable.cart3), // Drawable icon
//                                                contentDescription = "Add to cart",
//                                                tint = Color(0xFFB2FF59), // Light green icon
//                                                modifier = Modifier.size(27.dp)
//                                            )
//                                        }
//                                    }
//
//                                }
//
//
//
//
////
////
//                            }
//                        }
//                    }
//
//                    item {
//
//                        Box(){
//
//                            Image(
//                                modifier = Modifier.fillMaxWidth(),
//                                painter = painterResource(id = R.drawable.grey_card_product123), // Replace with your desired drawable
//                                contentDescription = "Shop Banner Image", // Meaningful content description
//
//                            )
//
//
//                            Row(
//                                modifier = Modifier.padding(),
//                                verticalAlignment = Alignment.Top,
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Column(
//                                    horizontalAlignment = Alignment.CenterHorizontally
//
//                                ) {
//
//
//                                    Row (
//                                        modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
//                                        horizontalArrangement = Arrangement.SpaceBetween,
//                                        verticalAlignment = Alignment.CenterVertically
//
//                                    ) {
//
//                                        PurpleHeartIcon()
//
//                                        Text(
//                                            text = "Best seller",
//                                            modifier = Modifier
//                                                .background(
//                                                    color = Color.Black, // Green background
//                                                    shape = RoundedCornerShape(99.dp) // Rounded corners
//                                                )
//                                                .padding(horizontal = 16.dp, vertical = 4.dp),
//                                            style = TextStyle(
//                                                color = Color(0xFFB2FF59), // Black text on green background
//                                                fontSize = 18.sp,
//
//                                                )
//                                        )
//
//
//                                    }
//
//                                    Image(
//
//                                        painter = painterResource(id = R.drawable.categorysample), // Replace with your desired drawable
//                                        contentDescription = "Shop Banner Image", // Meaningful content description
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .height(290.dp) // Adjust the height as needed
//                                    )
//
//                                    Box(
//                                        modifier= Modifier.width(340.dp)
//                                    ){
//
//                                        Image(
//
//                                            painter = painterResource(id = R.drawable.black_card), // Replace with your desired drawable
//                                            contentDescription = "Shop Banner Image", // Meaningful content description
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .height(160.dp) // Adjust the height as needed
//                                        )
//
//
////                                        ProductCard()
//                                        Button(
//                                            onClick = { /* TODO: Handle cart click */ },
//                                            shape = CircleShape,
//                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF535353)), // Button background
//                                            modifier = Modifier
//                                                .padding(bottom = 10.dp, end = 2.dp)
//                                                .size(50.dp)
//                                                .border(
//                                                    width = 1.dp,
//                                                    color = Color(0xFFB2FF59), // Green border
//                                                    shape = CircleShape
//                                                )
//                                                .align(Alignment.BottomEnd),
//                                            contentPadding = PaddingValues(end = 2.dp)
//                                        ) {
//                                            Icon(
//                                                painter = painterResource(id = R.drawable.cart3), // Drawable icon
//                                                contentDescription = "Add to cart",
//                                                tint = Color(0xFFB2FF59), // Light green icon
//                                                modifier = Modifier.size(27.dp)
//                                            )
//                                        }
//
//                                    }
//
//                                }
//
//
//
//
////
////
//                            }
//                        }
//                    }
//
//
//
//
//
//
//                }}
//        }
//    )
//}


