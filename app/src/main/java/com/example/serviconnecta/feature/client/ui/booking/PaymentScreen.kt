package com.example.serviconnecta.feature.client.ui.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviconnecta.feature.client.domain.model.PaymentMethod
import com.example.serviconnecta.feature.client.domain.model.PaymentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    serviceId: String,
    date: String,
    time: String,
    locationId: String,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadServiceDetail(serviceId)
        viewModel.loadPaymentMethods()
        viewModel.loadLocations()
        viewModel.selectDate(date)
        viewModel.selectTime(time)
        val location = viewModel.uiState.value.locations.find { it.id == locationId }
        location?.let { viewModel.selectLocation(it) }
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.showPaymentSuccess) {
        if (uiState.showPaymentSuccess) {
            kotlinx.coroutines.delay(2000)
            onPaymentSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Método de Pago") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.paymentMethods.forEach { method ->
                PaymentMethodCard(
                    paymentMethod = method,
                    isSelected = uiState.selectedPaymentMethod == method,
                    onClick = { viewModel.selectPaymentMethod(method) }
                )
            }

            Button(
                onClick = { /* Add new payment method */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Nueva Tarjeta")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::confirmBooking,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.selectedPaymentMethod != null && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Pagar")
                }
            }
        }
    }

    if (uiState.showPaymentSuccess) {
        PaymentSuccessDialog()
    }
}

@Composable
private fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (paymentMethod.type) {
                        PaymentType.CASH -> Icons.Default.Money
                        PaymentType.PAYPAL -> Icons.Default.Payment
                        PaymentType.GOOGLE_PAY -> Icons.Default.Payment
                        // CORREGIDO: Icons.Default.Apple no existe. Usamos Smartphone o Payment como placeholder.
                        PaymentType.APPLE_PAY -> Icons.Default.Smartphone
                        PaymentType.CARD -> Icons.Default.CreditCard
                    },
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        when (paymentMethod.type) {
                            PaymentType.CASH -> "Efectivo"
                            PaymentType.PAYPAL -> "PayPal"
                            PaymentType.GOOGLE_PAY -> "Google Pay"
                            PaymentType.APPLE_PAY -> "Apple Pay"
                            PaymentType.CARD -> "** ** ** ${paymentMethod.last4}"
                        },
                        fontWeight = FontWeight.Bold
                    )
                    if (paymentMethod.type == PaymentType.CARD) {
                        Text(
                            paymentMethod.cardholderName ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@Composable
fun PaymentSuccessDialog() {
    Dialog(onDismissRequest = { }) {
        Card {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Pago Exitoso", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("¡Gracias por su preferencia!", style = MaterialTheme.typography.bodyMedium)
                Text("Su pago ha sido procesado exitosamente.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}