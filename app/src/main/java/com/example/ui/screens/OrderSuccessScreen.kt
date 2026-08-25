package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

@Composable
fun OrderSuccessScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val lastOrder by viewModel.lastPlacedOrder.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .padding(20.dp)
            .testTag("order_success_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(ShopnovaGreen)
        ) {
            Icon(
                Icons.Default.DoneAll,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Order Placed Successfully! 🎉",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Order ID: ${lastOrder?.id ?: "SN-948201"}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ShopnovaBlue
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Expected Delivery by ${lastOrder?.expectedDeliveryDate ?: "in 2 days"}",
            fontSize = 13.sp,
            color = ShopnovaGreen,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Delivery To:", fontSize = 12.sp, color = TextSecondary)
                Text(lastOrder?.customerName ?: "Praveen Kumar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(lastOrder?.addressLine ?: "", fontSize = 12.sp, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = DividerColor)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Paid (${lastOrder?.paymentMethod ?: "UPI"}):", fontSize = 13.sp)
                    Text(lastOrder?.formattedTotal ?: "₹0", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ShopnovaGreen)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    lastOrder?.let { viewModel.selectOrder(it) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Track Order")
            }

            Button(
                onClick = { viewModel.navigateTo(AppDestination.HOME) },
                colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue),
                modifier = Modifier.weight(1f)
            ) {
                Text("Continue Shopping")
            }
        }
    }
}
