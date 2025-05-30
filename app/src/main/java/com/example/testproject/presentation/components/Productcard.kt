package com.example.testproject.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testproject.data.model.Product
import com.example.testproject.presentation.product.tangerineFontFamily

@Composable
fun ProductCard(
    product: Product,
    name: String = "Glow"
) {

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top =10.dp )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = product.title,
                    color = Color(0xFFB2FF59), // Light green
                    fontSize = 28.sp, // Increased font size
                    fontWeight = FontWeight.Bold,

                    style = TextStyle(
                                    fontFamily = tangerineFontFamily,
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFB2FF59), CircleShape) // Green dot
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = product.stockStatus,
                        color = Color(0xFFB2FF59), // Green text
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "French clay and algae-powered cleanser",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Skin Tightness",
                    color = Color.White,
                    fontSize = 13.sp, // Slightly increased
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " • ",
                    color = Color.White,
                    fontSize = 13.sp, // Dot separator
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dry & Dehydrated Skin",
                    color = Color.White,
                    fontSize = 13.sp, // Slightly increased
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically, // Align items to the bottom of the row
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RS. ${product.price}",
                            color = Color(0xFFE1BEE7), // Light green
                            fontSize = 18.sp, // Increased font size
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RS. ${product.mrp}",
                            color = Color.Gray,
                            fontSize = 14.sp, // Increased font size
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Star",
                                tint = Color(0xFFFFD740), // Yellow star
                                modifier = Modifier.size(18.dp) // Star size
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "249 reviews",
                            color = Color.White,
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline // Underlined
                        )
                    }
                }


            }
        }
    }


