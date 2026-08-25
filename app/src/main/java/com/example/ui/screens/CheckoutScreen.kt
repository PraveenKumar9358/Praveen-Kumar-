package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Address
import com.example.data.model.PaymentMethod
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val cartWithProducts by viewModel.cartWithProducts.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val selectedAddressId by viewModel.selectedAddressId.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()

    var showAddAddressDialog by remember { mutableStateOf(false) }

    // New address form fields
    var newFullName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newHouse by remember { mutableStateOf("") }
    var newArea by remember { mutableStateOf("") }
    var newCity by remember { mutableStateOf("Bengaluru") }
    var newState by remember { mutableStateOf("Karnataka") }
    var newPincode by remember { mutableStateOf("560034") }
    var newType by remember { mutableStateOf("Home") }

    val subtotal = cartWithProducts.sumOf { it.product.price * it.cartItem.quantity }
    var couponDiscount = 0.0
    appliedCoupon?.let { cp ->
        couponDiscount = ((subtotal * cp.discountPercent) / 100.0).coerceAtMost(cp.maxDiscount)
    }
    val deliveryFee = if (subtotal > 499) 0.0 else 40.0
    val finalTotal = (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0.0)

    val activeAddress = addresses.find { it.id == selectedAddressId } ?: addresses.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("checkout_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Step 1: Delivery Address Selection
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(4.dp), color = ShopnovaBlue) {
                                    Text("1", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("DELIVERY ADDRESS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            TextButton(onClick = { showAddAddressDialog = true }) {
                                Text("+ Add New", color = ShopnovaBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        if (addresses.isEmpty()) {
                            Text("No saved address. Please add one.", color = TextSecondary, fontSize = 12.sp)
                        } else {
                            addresses.forEach { addr ->
                                val isSelected = (addr.id == (activeAddress?.id ?: 0L))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) ShopnovaBlue else BorderLight,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(if (isSelected) ShopnovaBlueSoft.copy(alpha = 0.5f) else Color.White)
                                        .clickable { viewModel.selectAddress(addr.id) }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.selectAddress(addr.id) }
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(addr.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Spacer(Modifier.width(6.dp))
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEEEEEE)) {
                                                Text(addr.addressType, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(addr.fullAddressText, fontSize = 12.sp, color = TextPrimary)
                                        Text("Phone: ${addr.phone}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Step 2: Order Items Summary
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(4.dp), color = ShopnovaBlue) {
                                Text("2", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("ORDER SUMMARY (${cartWithProducts.size} Items)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(8.dp))

                        cartWithProducts.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.product.title.take(32)}... (x${item.cartItem.quantity})", fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                Text("₹%,d".format((item.product.price * item.cartItem.quantity).toLong()), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Step 3: Payment Method
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(4.dp), color = ShopnovaBlue) {
                                Text("3", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("PAYMENT METHOD", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(10.dp))

                        PaymentMethod.values().forEach { method ->
                            val isSelected = (selectedPaymentMethod == method)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) ShopnovaBlue else BorderLight,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(if (isSelected) ShopnovaBlueSoft.copy(alpha = 0.5f) else Color.White)
                                    .clickable { viewModel.setPaymentMethod(method) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.setPaymentMethod(method) }
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(method.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(
                                        text = when (method) {
                                            PaymentMethod.UPI -> "Instant payment via Google Pay, PhonePe, Paytm"
                                            PaymentMethod.CREDIT_DEBIT_CARD -> "Visa, MasterCard, RuPay, Maestro"
                                            PaymentMethod.NET_BANKING -> "All Major Indian Banks Supported"
                                            PaymentMethod.CASH_ON_DELIVERY -> "Pay cash upon delivery at your doorstep"
                                            PaymentMethod.SHOPNOVA_WALLET -> "Fast 1-click checkout with Shopnova Balance"
                                        },
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom CTA
        Surface(
            color = SurfaceWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total to Pay", fontSize = 11.sp, color = TextSecondary)
                    Text("₹%,d".format(finalTotal.toLong()), fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }

                Button(
                    onClick = { viewModel.placeCurrentOrder() },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopnovaGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(46.dp).testTag("confirm_pay_btn")
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("PAY & PLACE ORDER", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }

        // Add Address Dialog
        if (showAddAddressDialog) {
            AlertDialog(
                onDismissRequest = { showAddAddressDialog = false },
                title = { Text("Add Delivery Address") },
                text = {
                    Column {
                        OutlinedTextField(value = newFullName, onValueChange = { newFullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("10-Digit Mobile") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(value = newHouse, onValueChange = { newHouse = it }, label = { Text("House/Flat/Building") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(value = newArea, onValueChange = { newArea = it }, label = { Text("Area / Locality") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = newCity, onValueChange = { newCity = it }, label = { Text("City") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = newPincode, onValueChange = { newPincode = it }, label = { Text("PIN") }, modifier = Modifier.weight(1f))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newFullName.isNotBlank() && newPhone.isNotBlank() && newHouse.isNotBlank()) {
                                viewModel.saveAddress(
                                    Address(
                                        fullName = newFullName,
                                        phone = newPhone,
                                        houseNo = newHouse,
                                        area = newArea,
                                        city = newCity,
                                        state = newState,
                                        pincode = newPincode,
                                        addressType = newType,
                                        isDefault = true
                                    )
                                )
                                showAddAddressDialog = false
                            }
                        }
                    ) {
                        Text("Save Address")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddAddressDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
