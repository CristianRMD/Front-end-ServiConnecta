package com.example.serviconnecta.feature.client.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.serviconnecta.core.ui.components.RatingBar
import com.example.serviconnecta.core.utils.FormatUtils
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSummaryScreen(
    serviceId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDateTime: (String) -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    LaunchedEffect(serviceId) {
        viewModel.loadServiceDetail(serviceId)
        viewModel.loadLocations()
    }

    val uiState by viewModel.uiState.collectAsState()
    val service = uiState.service
    val selectedLocation = uiState.selectedLocation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de la reserva") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (service != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.LightGray, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Image, null, modifier = Modifier.align(Alignment.Center))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(service.title, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RatingBar(rating = service.rating, starSize = 14)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("(${FormatUtils.formatRating(service.rating)})", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(FormatUtils.formatPrice(service.price), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text(FormatUtils.formatPrice(service.price), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Descuento")
                        Text(FormatUtils.formatPrice(0.0))
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text(FormatUtils.formatPrice(service.price), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dirección", style = MaterialTheme.typography.labelSmall)
                                Text(selectedLocation?.address ?: "Seleccionar ubicación", fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ArrowForward, null)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onNavigateToDateTime(serviceId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Seleccionar espacio")
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

// CORREGIDO: Se agregó @OptIn(ExperimentalMaterial3Api::class) aquí
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDateTimeScreen(
    serviceId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPayment: (String, String, String, String) -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadServiceDetail(serviceId)
        viewModel.loadLocations()
    }

    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateValue by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }

    val timeSlots = listOf("08:00 a.m.", "11:00 a.m.", "03:00 p.m.", "06:00 p.m.")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Fecha") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.service?.let { service ->
                Card {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color.LightGray, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Image, null, modifier = Modifier.align(Alignment.Center))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(service.title, fontWeight = FontWeight.Bold)
                            Row {
                                RatingBar(rating = service.rating, starSize = 14)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("(${FormatUtils.formatRating(service.rating)})", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(FormatUtils.formatPrice(service.price))
                        }
                    }
                }
            }

            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedDateValue?.let {
                    "${it.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es"))} ${it.dayOfMonth} de ${it.month.getDisplayName(TextStyle.FULL, Locale("es"))}"
                } ?: "Seleccionar Fecha")
            }

            if (selectedDateValue != null) {
                Text("Seleccionar Horario", fontWeight = FontWeight.Bold)
                Text("Cuatro (4) disponibles", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                timeSlots.chunked(2).forEach { rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSlots.forEach { slot ->
                            OutlinedButton(
                                onClick = { selectedTimeSlot = slot },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedTimeSlot == slot) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                            ) {
                                Text(slot)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (selectedDateValue != null && selectedTimeSlot != null && uiState.selectedLocation != null) {
                        val dateStr = "${selectedDateValue!!.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es"))}, ${selectedDateValue!!.dayOfMonth} de ${selectedDateValue!!.month.getDisplayName(TextStyle.FULL, Locale("es"))}, ${selectedDateValue!!.year}"
                        onNavigateToPayment(serviceId, dateStr, selectedTimeSlot!!, uiState.selectedLocation!!.id)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedDateValue != null && selectedTimeSlot != null
            ) {
                Text("Ir a Pagar")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDateValue = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val today = LocalDate.now()
    val dates = (0..30).map { today.plusDays(it.toLong()) }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Seleccionar Fecha", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                dates.take(10).forEach { date ->
                    TextButton(
                        onClick = { onDateSelected(date) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es"))} ${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.SHORT, Locale("es"))}")
                    }
                }
            }
        }
    }
}