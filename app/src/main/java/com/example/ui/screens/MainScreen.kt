package com.example.ui.screens

import android.widget.Toast
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.*
import com.example.ui.InvoiceViewModel
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: InvoiceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedInvoice by viewModel.selectedInvoice.collectAsStateWithLifecycle()

    if (!isLoggedIn) {
        LoginScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                if (selectedInvoice == null) {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = "Logo",
                                    tint = PremiumBrownGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "CEYVANA CIMS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp,
                                    color = PremiumBrownGold
                                )
                            }
                        },
                        actions = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(PremiumBrownGold.copy(alpha = 0.1f))
                                    .clickable { viewModel.currentTab.value = "Settings" }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile",
                                        tint = PremiumBrownGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = userRole,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PremiumBrownGold
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color(0xFFFCF8F5)
                        )
                    )
                }
            },
            bottomBar = {
                if (selectedInvoice == null) {
                    NavigationBar(
                        containerColor = Color(0xFFFCF8F5),
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        NavigationBarItem(
                            selected = currentTab == "Dashboard",
                            onClick = { viewModel.currentTab.value = "Dashboard" },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == "Dashboard") Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                    contentDescription = "Dashboard"
                                )
                            },
                            label = { Text("Dashboard", fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_dashboard")
                        )
                        NavigationBarItem(
                            selected = currentTab == "Invoices",
                            onClick = { viewModel.currentTab.value = "Invoices" },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == "Invoices") Icons.Filled.Receipt else Icons.Outlined.Receipt,
                                    contentDescription = "Invoices"
                                )
                            },
                            label = { Text("Invoices", fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_invoices")
                        )
                        NavigationBarItem(
                            selected = currentTab == "Products" || currentTab == "Spices",
                            onClick = { viewModel.currentTab.value = "Products" },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == "Products" || currentTab == "Spices") Icons.Filled.Eco else Icons.Outlined.Eco,
                                    contentDescription = "Products"
                                )
                            },
                            label = { Text("Products", fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_products")
                        )
                        NavigationBarItem(
                            selected = currentTab == "Settings",
                            onClick = { viewModel.currentTab.value = "Settings" },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == "Settings") Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text("Settings", fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_settings")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedInvoice != null,
                    transitionSpec = {
                        fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                    },
                    label = "InvoiceDetailTransition"
                ) { isDetailOpen ->
                    if (isDetailOpen) {
                        selectedInvoice?.let { invoice ->
                            InvoiceDetailsView(
                                invoice = invoice,
                                onBack = { viewModel.selectedInvoice.value = null },
                                onEdit = {
                                    if (userRole == "Sales Staff") {
                                        Toast.makeText(context, "Sales Staff cannot edit invoices directly", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.loadInvoiceForEditing(invoice)
                                        viewModel.selectedInvoice.value = null
                                    }
                                },
                                onDelete = {
                                    if (userRole == "Sales Staff") {
                                        Toast.makeText(context, "Sales Staff cannot delete invoices", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.deleteInvoice(invoice.invoice)
                                        viewModel.selectedInvoice.value = null
                                        Toast.makeText(context, "Invoice Deleted", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    } else {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                            },
                            label = "TabTransition"
                        ) { tab ->
                            when (tab) {
                                "Dashboard" -> DashboardView(viewModel)
                                "Invoices" -> InvoicesListView(viewModel)
                                "Spices", "Products" -> SpiceCatalogView(viewModel)
                                "Create" -> CreateInvoiceFormView(viewModel)
                                "Customers" -> CustomersView(viewModel)
                                "Reports" -> ReportsView(viewModel)
                                "Inventory" -> InventoryView(viewModel)
                                "Settings" -> SettingsView(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// DASHBOARD VIEW
// ----------------------------------------------------
@Composable
fun DashboardView(viewModel: InvoiceViewModel) {
    val invoices by viewModel.allInvoices.collectAsStateWithLifecycle()
    
    val todaySales by viewModel.todaySales.collectAsStateWithLifecycle()
    val monthlySales by viewModel.monthlySales.collectAsStateWithLifecycle()
    val allClients by viewModel.allClients.collectAsStateWithLifecycle()
    val allSpiceItems by viewModel.allSpiceItems.collectAsStateWithLifecycle()
    val pendingSalesAmount by viewModel.pendingSalesAmount.collectAsStateWithLifecycle()
    val stockAlertsCount by viewModel.stockAlertsCount.collectAsStateWithLifecycle()

    val currencyFormatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Brand Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_spice_banner_1783337037536),
                    contentDescription = "Ceyvana Spices Header Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Ceyvana Premium Spices",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "Invoice & Inventory Management System",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Metrics Header
        item {
            Text(
                text = "Operational Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepDarkBrown,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 2x3 grid of Metric Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "Today's Sales",
                        value = currencyFormatter.format(todaySales),
                        icon = Icons.Default.TrendingUp,
                        iconColor = StatusPaidText,
                        backgroundColor = StatusPaidBg,
                        modifier = Modifier.weight(1f).testTag("metric_today_sales")
                    )
                    MetricCard(
                        title = "Monthly Sales",
                        value = currencyFormatter.format(monthlySales),
                        icon = Icons.Default.MonetizationOn,
                        iconColor = PremiumBrownGold,
                        backgroundColor = PremiumBrownGold.copy(alpha = 0.08f),
                        modifier = Modifier.weight(1f).testTag("metric_monthly_sales")
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "Pending Payments",
                        value = currencyFormatter.format(pendingSalesAmount),
                        icon = Icons.Default.Schedule,
                        iconColor = StatusPendingText,
                        backgroundColor = StatusPendingBg,
                        modifier = Modifier.weight(1f).testTag("metric_pending_payments")
                    )
                    MetricCard(
                        title = "Stock Alerts",
                        value = "$stockAlertsCount Items",
                        icon = Icons.Default.Warning,
                        iconColor = if (stockAlertsCount > 0) Color.Red else MediumSpiceBrown,
                        backgroundColor = if (stockAlertsCount > 0) Color.Red.copy(alpha = 0.08f) else WarmCreamSurface,
                        modifier = Modifier.weight(1f).testTag("metric_stock_alerts")
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "Customers",
                        value = "${allClients.size} Profiles",
                        icon = Icons.Default.People,
                        iconColor = DeepDarkBrown,
                        backgroundColor = WarmCreamSurface,
                        modifier = Modifier.weight(1f).testTag("metric_customers")
                    )
                    MetricCard(
                        title = "Products",
                        value = "${allSpiceItems.size} Spices",
                        icon = Icons.Default.Eco,
                        iconColor = PremiumBrownGold,
                        backgroundColor = PremiumBrownGold.copy(alpha = 0.08f),
                        modifier = Modifier.weight(1f).testTag("metric_products")
                    )
                }
            }
        }

        // Action Buttons Header
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepDarkBrown,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Grid of Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButton(
                        title = "Create Invoice",
                        icon = Icons.Default.AddCircle,
                        onClick = {
                            viewModel.clearForm()
                            viewModel.currentTab.value = "Create"
                        },
                        modifier = Modifier.weight(1f).testTag("btn_create_invoice")
                    )
                    ActionButton(
                        title = "Products",
                        icon = Icons.Default.Eco,
                        onClick = { viewModel.currentTab.value = "Products" },
                        modifier = Modifier.weight(1f).testTag("btn_products")
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButton(
                        title = "Customers",
                        icon = Icons.Default.People,
                        onClick = { viewModel.currentTab.value = "Customers" },
                        modifier = Modifier.weight(1f).testTag("btn_customers")
                    )
                    ActionButton(
                        title = "Reports",
                        icon = Icons.Default.Assessment,
                        onClick = { viewModel.currentTab.value = "Reports" },
                        modifier = Modifier.weight(1f).testTag("btn_reports")
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButton(
                        title = "Inventory",
                        icon = Icons.Default.Inventory,
                        onClick = { viewModel.currentTab.value = "Inventory" },
                        modifier = Modifier.weight(1f).testTag("btn_inventory")
                    )
                    ActionButton(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        onClick = { viewModel.currentTab.value = "Settings" },
                        modifier = Modifier.weight(1f).testTag("btn_settings")
                    )
                }
            }
        }

        // Recent Invoices Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepDarkBrown
                )
                TextButton(onClick = { viewModel.currentTab.value = "Invoices" }) {
                    Text("See All", color = PremiumBrownGold)
                }
            }
        }

        // List of up to 3 recent invoices
        val recentInvoices = invoices.take(3)
        if (recentInvoices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmCreamSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No invoices registered yet. Click 'Create Invoice' above to create your first.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MediumSpiceBrown
                        )
                    }
                }
            }
        } else {
            items(recentInvoices) { item ->
                InvoiceListItem(
                    invoice = item,
                    onClick = { viewModel.selectedInvoice.value = item }
                )
            }
        }
    }
}

// Helper Composable for Metric Cards
@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SubtleBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MediumSpiceBrown,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = DeepDarkBrown
                )
            }
        }
    }
}

// Helper Composable for Quick Actions
@Composable
fun ActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PremiumBrownGold.copy(alpha = 0.06f)),
        border = BorderStroke(1.dp, PremiumBrownGold.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PremiumBrownGold,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DeepDarkBrown
            )
        }
    }
}

// ----------------------------------------------------
// INVOICES LIST VIEW (Search, Filter, View)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesListView(viewModel: InvoiceViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
    val invoices by viewModel.filteredInvoices.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App bar
        TopAppBar(
            title = { Text("Invoice Registry", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search by Invoice # or Client Name...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("invoice_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Status Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("All", "Paid", "Pending", "Draft")
            filters.forEach { filter ->
                val isSelected = statusFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.statusFilter.value = filter },
                    label = { Text(filter) },
                    modifier = Modifier.testTag("filter_chip_$filter")
                )
            }
        }

        // Invoice List
        if (invoices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "Empty list",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No matching invoices found.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(invoices) { item ->
                    InvoiceListItem(
                        invoice = item,
                        onClick = { viewModel.selectedInvoice.value = item }
                    )
                }
            }
        }
    }
}

@Composable
fun InvoiceListItem(
    invoice: InvoiceWithItems,
    onClick: () -> Unit
) {
    val inv = invoice.invoice
    val itemsCount = invoice.lineItems.sumOf { it.quantityKg }
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val (statusBg, statusText) = when (inv.status) {
        "Paid" -> Pair(StatusPaidBg, StatusPaidText)
        "Pending" -> Pair(StatusPendingBg, StatusPendingText)
        "Overdue" -> Pair(StatusOverdueBg, StatusOverdueText)
        else -> Pair(StatusDraftBg, StatusDraftText)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("invoice_card_${inv.invoiceNumber}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SubtleBorder),
        colors = CardDefaults.cardColors(containerColor = LightCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inv.invoiceNumber,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = PremiumBrownGold
                )
                
                // Status Pill
                Box(
                    modifier = Modifier
                        .background(statusBg, shape = RoundedCornerShape(99.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = inv.status.uppercase(),
                        color = statusText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = inv.clientName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DarkNeutralText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = SubtleBorder)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Issued: ${dateFormatter.format(Date(inv.issueDate))}",
                        fontSize = 11.sp,
                        color = MediumSpiceBrown
                    )
                    Text(
                        text = "$itemsCount kg spices sold",
                        fontSize = 12.sp,
                        color = DarkNeutralText
                    )
                }

                Text(
                    text = currencyFormatter.format(inv.grandTotal),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = DarkNeutralText
                )
            }
        }
    }
}

// ----------------------------------------------------
// SPICE CATALOG VIEW (Product Module)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpiceCatalogView(viewModel: InvoiceViewModel) {
    val spices by viewModel.allSpiceItems.collectAsStateWithLifecycle()
    var showAddSpiceDialog by remember { mutableStateOf(false) }
    var selectedSpiceToEdit by remember { mutableStateOf<SpiceItem?>(null) }
    var selectedSpiceForInvoice by remember { mutableStateOf<SpiceItem?>(null) }
    var qtyToAdd by remember { mutableStateOf("1.0") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val context = LocalContext.current

    // Filtered spices based on selected category filter
    val filteredSpices = remember(spices, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") {
            spices
        } else {
            spices.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("Product Catalog", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DeepDarkBrown)
                    Text("Manage Ceylon Spices & Pack Sizes", fontSize = 11.sp, color = MediumSpiceBrown)
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        selectedSpiceToEdit = null
                        showAddSpiceDialog = true
                    },
                    modifier = Modifier.testTag("add_custom_spice_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddBox, 
                        contentDescription = "Add New Product",
                        tint = PremiumBrownGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("All", "Whole Spices", "Ground Spices", "Blends", "Luxury Spices").forEach { category ->
                FilterChip(
                    selected = selectedCategoryFilter == category,
                    onClick = { selectedCategoryFilter = category },
                    label = { Text(category, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PremiumBrownGold.copy(alpha = 0.15f),
                        selectedLabelColor = PremiumBrownGold
                    )
                )
            }
        }

        if (filteredSpices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = MediumSpiceBrown.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No products found in this category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MediumSpiceBrown
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1), // Responsive single column listing
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp)
            ) {
                items(filteredSpices) { spice ->
                    SpiceCatalogItem(
                        spice = spice,
                        onDelete = {
                            viewModel.deleteSpice(spice)
                            Toast.makeText(context, "${spice.name} removed from Catalog", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = {
                            selectedSpiceToEdit = spice
                            showAddSpiceDialog = true
                        },
                        onAddToInvoice = {
                            selectedSpiceForInvoice = spice
                            qtyToAdd = "1.0"
                        }
                    )
                }
            }
        }
    }

    // Add to Invoice Dialog
    if (selectedSpiceForInvoice != null) {
        // Size Selector
        var selectedPackSizeForInvoice by remember(selectedSpiceForInvoice) {
            val availableSizes = selectedSpiceForInvoice?.getAvailablePackPrices()?.keys?.toList() ?: emptyList()
            mutableStateOf(availableSizes.firstOrNull() ?: "1kg")
        }
        
        val availablePrices = selectedSpiceForInvoice?.getAvailablePackPrices() ?: emptyMap()
        val selectedPrice = availablePrices[selectedPackSizeForInvoice] ?: selectedSpiceForInvoice?.pricePerKg ?: 0.0

        Dialog(onDismissRequest = { selectedSpiceForInvoice = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Add to Invoice Draft",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PremiumBrownGold
                    )
                    Text(
                        text = selectedSpiceForInvoice?.name ?: "",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = DeepDarkBrown
                    )
                    
                    Text("Select Pack Size", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepDarkBrown)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        availablePrices.forEach { (size, price) ->
                            FilterChip(
                                selected = selectedPackSizeForInvoice == size,
                                onClick = { selectedPackSizeForInvoice = size },
                                label = { Text("$size ($${String.format("%.2f", price)})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PremiumBrownGold.copy(alpha = 0.15f),
                                    selectedLabelColor = PremiumBrownGold
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = qtyToAdd,
                        onValueChange = { qtyToAdd = it },
                        label = { Text(if (selectedPackSizeForInvoice.lowercase() == "custom") "Quantity (kg)" else "Quantity (packs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_qty_input")
                    )

                    val calculatedTotal = (qtyToAdd.toDoubleOrNull() ?: 0.0) * selectedPrice
                    if (calculatedTotal > 0.0) {
                        Text(
                            text = "Line Total: $${String.format("%.2f", calculatedTotal)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PremiumBrownGold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { selectedSpiceForInvoice = null }) {
                            Text("Cancel", color = MediumSpiceBrown)
                        }
                        Button(
                            onClick = {
                                val qty = qtyToAdd.toDoubleOrNull() ?: 1.0
                                if (qty > 0 && selectedSpiceForInvoice != null) {
                                    viewModel.addSpiceToInvoice(selectedSpiceForInvoice!!, qty, selectedPackSizeForInvoice, selectedPrice)
                                    Toast.makeText(context, "${selectedSpiceForInvoice?.name} Added!", Toast.LENGTH_SHORT).show()
                                    selectedSpiceForInvoice = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                            modifier = Modifier.testTag("confirm_add_to_invoice")
                        ) {
                            Text("Add to Draft")
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Spice Dialog
    if (showAddSpiceDialog) {
        val isEditing = selectedSpiceToEdit != null
        
        var spiceName by remember(selectedSpiceToEdit) { mutableStateOf(selectedSpiceToEdit?.name ?: "") }
        var spiceSku by remember(selectedSpiceToEdit) { mutableStateOf(selectedSpiceToEdit?.sku ?: "") }
        var spiceDesc by remember(selectedSpiceToEdit) { mutableStateOf(selectedSpiceToEdit?.description ?: "") }
        var spicePrice by remember(selectedSpiceToEdit) { mutableStateOf(selectedSpiceToEdit?.pricePerKg?.toString() ?: "") }
        var spiceCat by remember(selectedSpiceToEdit) { mutableStateOf(selectedSpiceToEdit?.category ?: "Whole Spices") }
        var spiceStatus by remember(selectedSpiceToEdit) { mutableStateOf(selectedSpiceToEdit?.status ?: "Active") }
        
        // Pack sizes selection
        val standardSizes = listOf("25g", "50g", "100g", "150g", "200g", "250g", "400g", "500g", "750g", "1kg", "Custom")
        var selectedSizes by remember(selectedSpiceToEdit) {
            val initialSizes = selectedSpiceToEdit?.packSizes?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: listOf("25g", "50g", "100g", "250g", "500g", "1kg")
            mutableStateOf(initialSizes.toSet())
        }

        var customPackPricesMap by remember(selectedSpiceToEdit, spicePrice) {
            val baseVal = spicePrice.toDoubleOrNull() ?: 10.0
            val initialPricesMap = selectedSpiceToEdit?.getPackPricesMap() ?: emptyMap()
            // Make sure all standard sizes have a default pricing calculated if not set yet
            val updatedMap = initialPricesMap.toMutableMap()
            standardSizes.forEach { size ->
                if (!updatedMap.containsKey(size)) {
                    val grams = when {
                        size.lowercase() == "custom" -> 100.0
                        size.endsWith("kg") -> size.replace("kg", "").toDoubleOrNull()?.let { it * 1000 } ?: 1000.0
                        size.endsWith("g") -> size.replace("g", "").toDoubleOrNull() ?: 100.0
                        else -> 100.0
                    }
                    val proportional = (grams / 1000.0) * baseVal
                    val markup = when {
                        grams <= 50 -> 1.25
                        grams <= 200 -> 1.15
                        grams <= 500 -> 1.05
                        else -> 1.0
                    }
                    updatedMap[size] = Math.round(proportional * markup * 100.0) / 100.0
                }
            }
            mutableStateOf(updatedMap.toMap())
        }

        Dialog(onDismissRequest = { showAddSpiceDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isEditing) "Edit Product Details" else "Add New Product",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PremiumBrownGold
                    )

                    OutlinedTextField(
                        value = spiceName,
                        onValueChange = { spiceName = it },
                        label = { Text("Product Name *") },
                        placeholder = { Text("e.g. Chilli Powder") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_spice_name")
                    )

                    OutlinedTextField(
                        value = spiceSku,
                        onValueChange = { spiceSku = it },
                        label = { Text("Product ID (SKU) *") },
                        placeholder = { Text("e.g. CEY-CHI-POW") },
                        enabled = !isEditing, // SKU acts as a read-only identifier during edits
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = spiceDesc,
                        onValueChange = { spiceDesc = it },
                        label = { Text("Description") },
                        placeholder = { Text("Premium pure ground spice details...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = spicePrice,
                        onValueChange = { spicePrice = it },
                        label = { Text("Base Price ($/kg) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Selector
                    Text("Category *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepDarkBrown)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Whole Spices", "Ground Spices", "Blends", "Luxury Spices").forEach { cat ->
                            FilterChip(
                                selected = spiceCat == cat,
                                onClick = { spiceCat = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PremiumBrownGold.copy(alpha = 0.15f),
                                    selectedLabelColor = PremiumBrownGold
                                )
                            )
                        }
                    }

                    // Pack Sizes Multi-Selector
                    Text("Available Pack Sizes *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepDarkBrown)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        standardSizes.forEach { size ->
                            val isSelected = selectedSizes.contains(size)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedSizes = if (isSelected) {
                                        selectedSizes - size
                                    } else {
                                        selectedSizes + size
                                    }
                                },
                                label = { Text(size, fontSize = 11.sp) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PremiumBrownGold.copy(alpha = 0.15f),
                                    selectedLabelColor = PremiumBrownGold
                                )
                            )
                        }
                    }

                    // Dynamic Pack Sizes Pricing
                    if (selectedSizes.isNotEmpty()) {
                        Text("Set Individual Prices ($) *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepDarkBrown)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedSizes.toList().sortedBy {
                                val weightStr = it.replace("g", "").replace("kg", "")
                                val weightNum = weightStr.toDoubleOrNull() ?: 1.0
                                if (it.contains("kg")) weightNum * 1000 else if (it.lowercase() == "custom") 999999.0 else weightNum
                            }.forEach { size ->
                                val currentPrice = customPackPricesMap[size] ?: 0.0

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(WarmCreamSurface, shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$size Pack Size",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DeepDarkBrown
                                    )
                                    OutlinedTextField(
                                        value = if (currentPrice == 0.0) "" else currentPrice.toString(),
                                        onValueChange = { newVal ->
                                            val dVal = newVal.toDoubleOrNull() ?: 0.0
                                            customPackPricesMap = customPackPricesMap + (size to dVal)
                                        },
                                        prefix = { Text("$ ", fontSize = 12.sp, color = MediumSpiceBrown) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier
                                            .width(110.dp)
                                            .height(48.dp),
                                        singleLine = true,
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                    )
                                }
                            }
                        }
                    }

                    // Status Selector
                    Text("Status", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepDarkBrown)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Active", "Inactive", "Discontinued").forEach { status ->
                            val isSelected = spiceStatus == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { spiceStatus = status },
                                label = { Text(status, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (status) {
                                        "Active" -> StatusPaidBg
                                        "Inactive" -> StatusDraftBg
                                        else -> StatusOverdueBg
                                    },
                                    selectedLabelColor = when (status) {
                                        "Active" -> StatusPaidText
                                        "Inactive" -> StatusDraftText
                                        else -> StatusOverdueText
                                    }
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddSpiceDialog = false }) {
                            Text("Cancel", color = MediumSpiceBrown)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val price = spicePrice.toDoubleOrNull() ?: 0.0
                                if (spiceName.isNotBlank() && spiceSku.isNotBlank() && price > 0 && selectedSizes.isNotEmpty()) {
                                    val packSizesJoined = selectedSizes.toList().sortedBy {
                                        val weightStr = it.replace("g", "").replace("kg", "")
                                        val weightNum = weightStr.toDoubleOrNull() ?: 1.0
                                        if (it.contains("kg")) weightNum * 1000 else if (it.lowercase() == "custom") 999999.0 else weightNum
                                    }.joinToString(", ")

                                    val packPricesJoined = selectedSizes.map { size ->
                                        val p = customPackPricesMap[size] ?: run {
                                            val grams = when {
                                                size.lowercase() == "custom" -> 100.0
                                                size.endsWith("kg") -> size.replace("kg", "").toDoubleOrNull()?.let { it * 1000 } ?: 1000.0
                                                size.endsWith("g") -> size.replace("g", "").toDoubleOrNull() ?: 100.0
                                                else -> 100.0
                                            }
                                            val proportional = (grams / 1000.0) * price
                                            Math.round(proportional * 100.0) / 100.0
                                        }
                                        "$size:$p"
                                    }.joinToString(", ")

                                    if (isEditing && selectedSpiceToEdit != null) {
                                        val updatedItem = selectedSpiceToEdit!!.copy(
                                            name = spiceName,
                                            description = spiceDesc,
                                            pricePerKg = price,
                                            category = spiceCat,
                                            packSizes = packSizesJoined,
                                            packPrices = packPricesJoined,
                                            status = spiceStatus
                                        )
                                        viewModel.updateSpiceItem(updatedItem)
                                        Toast.makeText(context, "$spiceName Updated successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.createSpiceItem(
                                            name = spiceName,
                                            sku = spiceSku,
                                            description = spiceDesc,
                                            pricePerKg = price,
                                            category = spiceCat,
                                            packSizes = packSizesJoined,
                                            packPrices = packPricesJoined,
                                            status = spiceStatus
                                        )
                                        Toast.makeText(context, "$spiceName Added to Catalog", Toast.LENGTH_SHORT).show()
                                    }
                                    showAddSpiceDialog = false
                                } else {
                                    Toast.makeText(context, "Please fill in all required fields marked with *", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                            modifier = Modifier.testTag("submit_new_spice_btn")
                        ) {
                            Text("Save Product")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpiceCatalogItem(
    spice: SpiceItem,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onAddToInvoice: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    
    // Status color mappings
    val statusBg = when (spice.status.lowercase()) {
        "active" -> StatusPaidBg
        "inactive" -> StatusDraftBg
        else -> StatusOverdueBg
    }
    val statusText = when (spice.status.lowercase()) {
        "active" -> StatusPaidText
        "inactive" -> StatusDraftText
        else -> StatusOverdueText
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("spice_card_${spice.sku}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SubtleBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Product Image (Visual placeholder matching luxury spice look)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WarmCreamSurface),
                contentAlignment = Alignment.Center
            ) {
                val imageRes = if (spice.sku.contains("CIN") || spice.sku.contains("ALBA")) {
                    R.drawable.img_spice_banner_1783337037536
                } else {
                    R.drawable.img_premium_spices_1783340971012
                }
                
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = spice.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Right Side: Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Category & Status Badge Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .background(WarmCreamSurface, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = spice.category.uppercase(),
                            color = PremiumBrownGold,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .background(statusBg, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = spice.status.uppercase(),
                            color = statusText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Name
                Text(
                    text = spice.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = DeepDarkBrown
                )

                // ID (SKU)
                Text(
                    text = "ID: ${spice.sku}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MediumSpiceBrown
                )

                // Pack Sizes
                if (spice.packSizes.isNotBlank()) {
                    val availablePrices = spice.getAvailablePackPrices()
                    Row(
                        modifier = Modifier
                            .padding(vertical = 1.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sizes:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MediumSpiceBrown
                        )
                        spice.packSizes.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { size ->
                            val szPrice = availablePrices[size]
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF3EFEA), shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (szPrice != null) "$size ($${String.format("%.2f", szPrice)})" else size,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepDarkBrown
                                )
                            }
                        }
                    }
                }

                // Description
                Text(
                    text = spice.description,
                    fontSize = 12.sp,
                    color = DarkNeutralText.copy(alpha = 0.8f),
                    maxLines = 2,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Bottom Row: Price and Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currencyFormatter.format(spice.pricePerKg)} / kg",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = PremiumBrownGold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Edit Action
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(32.dp).testTag("edit_spice_${spice.sku}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Product",
                                tint = PremiumBrownGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Delete Action (only custom spices with ID > 9)
                        if (spice.id > 9) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(32.dp).testTag("delete_spice_${spice.sku}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Product",
                                    tint = StatusOverdueText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Add to Invoice Button
                        Button(
                            onClick = onAddToInvoice,
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("add_to_invoice_btn_${spice.sku}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PostAdd,
                                contentDescription = "Add to Invoice",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CREATE / EDIT INVOICE FORM VIEW
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceFormView(viewModel: InvoiceViewModel) {
    val context = LocalContext.current

    val clientName by viewModel.clientName.collectAsStateWithLifecycle()
    val clientEmail by viewModel.clientEmail.collectAsStateWithLifecycle()
    val clientPhone by viewModel.clientPhone.collectAsStateWithLifecycle()
    val clientAddress by viewModel.clientAddress.collectAsStateWithLifecycle()
    val customers by viewModel.allClients.collectAsStateWithLifecycle()
    val invoiceNumber by viewModel.invoiceNumber.collectAsStateWithLifecycle()
    val taxRate by viewModel.taxRate.collectAsStateWithLifecycle()
    val discountAmount by viewModel.discountAmount.collectAsStateWithLifecycle()
    val invoiceStatus by viewModel.invoiceStatus.collectAsStateWithLifecycle()
    val selectedLineItems by viewModel.selectedLineItems.collectAsStateWithLifecycle()

    val subtotal by viewModel.subtotal.collectAsStateWithLifecycle()
    val taxAmount by viewModel.taxAmount.collectAsStateWithLifecycle()
    val grandTotal by viewModel.grandTotal.collectAsStateWithLifecycle()
    val editingInvoice by viewModel.editingInvoice.collectAsStateWithLifecycle()

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (editingInvoice != null) "Edit Invoice ${editingInvoice?.invoice?.invoiceNumber}" else "New Invoice Draft",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Client details section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Client Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (customers.isNotEmpty()) {
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(customers) { customer ->
                                val displayText = if (customer.storeName.isNotBlank()) "${customer.name} (${customer.storeName})" else customer.name
                                FilterChip(
                                    selected = (clientName == customer.name),
                                    onClick = {
                                        viewModel.clientName.value = customer.name
                                        viewModel.clientEmail.value = customer.email
                                        viewModel.clientPhone.value = customer.phone
                                        viewModel.clientAddress.value = if (customer.storeName.isNotBlank()) {
                                            "${customer.storeName}, ${customer.address}, ${customer.city}, ${customer.country}"
                                        } else {
                                            "${customer.address}, ${customer.city}, ${customer.country}"
                                        }
                                    },
                                    label = { Text(displayText, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { viewModel.clientName.value = it },
                        label = { Text("Client Name *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_client_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = clientEmail,
                        onValueChange = { viewModel.clientEmail.value = it },
                        label = { Text("Client Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    OutlinedTextField(
                        value = clientPhone,
                        onValueChange = { viewModel.clientPhone.value = it },
                        label = { Text("Client Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    OutlinedTextField(
                        value = clientAddress,
                        onValueChange = { viewModel.clientAddress.value = it },
                        label = { Text("Client Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Invoice Parameters (Status & Metadata)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Invoice Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { viewModel.invoiceNumber.value = it },
                        label = { Text("Invoice Reference Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Status Dropdown selector
                    Text("Payment Status:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Pending", "Paid", "Draft").forEach { status ->
                            FilterChip(
                                selected = invoiceStatus == status,
                                onClick = { viewModel.invoiceStatus.value = status },
                                label = { Text(status) },
                                modifier = Modifier.testTag("status_chip_$status")
                            )
                        }
                    }
                }
            }

            // Line Items List Inside Form
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selected Spices",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        TextButton(
                            onClick = { viewModel.currentTab.value = "Spices" },
                            modifier = Modifier.testTag("form_add_spice_link")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Browse catalog")
                        }
                    }

                    if (selectedLineItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No spices added yet.\nTap 'Browse Catalog' to add spices.",
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        selectedLineItems.forEach { lineItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (lineItem.selectedPackSize.lowercase() == "custom") {
                                            lineItem.spiceItem.name
                                        } else {
                                            "${lineItem.spiceItem.name} (${lineItem.selectedPackSize})"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${currencyFormatter.format(lineItem.selectedUnitPrice)} / unit",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Sub: ${currencyFormatter.format(lineItem.totalPrice)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            val step = if (lineItem.selectedPackSize.lowercase() == "custom") 0.5 else 1.0
                                            viewModel.updateLineItemQuantity(
                                                lineItem.spiceItem.id,
                                                lineItem.selectedPackSize,
                                                lineItem.quantityKg - step
                                            )
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RemoveCircleOutline,
                                            contentDescription = "Decrease",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Text(
                                        text = if (lineItem.selectedPackSize.lowercase() == "custom") {
                                            "${lineItem.quantityKg} kg"
                                        } else {
                                            "${lineItem.quantityKg.toInt()} units"
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    IconButton(
                                        onClick = {
                                            val step = if (lineItem.selectedPackSize.lowercase() == "custom") 0.5 else 1.0
                                            viewModel.updateLineItemQuantity(
                                                lineItem.spiceItem.id,
                                                lineItem.selectedPackSize,
                                                lineItem.quantityKg + step
                                            )
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddCircleOutline,
                                            contentDescription = "Increase",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.removeSpiceFromInvoice(lineItem.spiceItem.id, lineItem.selectedPackSize) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove item",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Pricing Adjustments (Discount & Taxes)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Adjustments & Discounts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = if (discountAmount == 0.0) "" else discountAmount.toString(),
                        onValueChange = {
                            viewModel.discountAmount.value = it.toDoubleOrNull() ?: 0.0
                        },
                        label = { Text("Discount Value ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = (taxRate * 100).toString(),
                        onValueChange = {
                            val rate = it.toDoubleOrNull() ?: 8.0
                            viewModel.taxRate.value = rate / 100.0
                        },
                        label = { Text("Tax Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Calculation Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal:", fontSize = 14.sp)
                        Text(currencyFormatter.format(subtotal), fontWeight = FontWeight.Bold)
                    }
                    if (discountAmount > 0.0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Discount:", fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                            Text("- ${currencyFormatter.format(discountAmount)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("VAT (${taxRate * 100}%):", fontSize = 14.sp)
                        Text(currencyFormatter.format(taxAmount), fontWeight = FontWeight.Bold)
                    }

                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Grand Total:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = currencyFormatter.format(grandTotal),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Submit Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.clearForm()
                        viewModel.currentTab.value = "Invoices"
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Discard")
                }

                Button(
                    onClick = {
                        if (clientName.isBlank()) {
                            Toast.makeText(context, "Client Name is required!", Toast.LENGTH_SHORT).show()
                        } else if (selectedLineItems.isEmpty()) {
                            Toast.makeText(context, "Please add at least one spice item!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.saveInvoice()
                            Toast.makeText(context, "Invoice Saved Successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_invoice_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Invoice")
                }
            }
        }
    }
}

// ----------------------------------------------------
// HIGH-FIDELITY DIGITAL INVOICE DETAILS VIEW
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailsView(
    invoice: InvoiceWithItems,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val inv = invoice.invoice
    val items = invoice.lineItems
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    val context = LocalContext.current

    val (statusBg, statusText) = when (inv.status) {
        "Paid" -> Pair(StatusPaidBg, StatusPaidText)
        "Pending" -> Pair(StatusPendingBg, StatusPendingText)
        "Overdue" -> Pair(StatusOverdueBg, StatusOverdueText)
        else -> Pair(StatusDraftBg, StatusDraftText)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(inv.invoiceNumber, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit, modifier = Modifier.testTag("detail_edit_btn")) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.testTag("detail_delete_btn")) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Scrollable Digital Receipt paper
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Digital Invoice Sheet Paper design
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Brand Logo and Header Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_app_icon_1783337018522),
                                    contentDescription = "Ceyvana Logo",
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Column {
                                    Text(
                                        text = "CEYVANA",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 1.5.sp
                                    )
                                    Text(
                                        text = "Premium Ceylon Spices",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "INVOICE",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = inv.invoiceNumber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        // Metadata (Dates, Status)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ISSUED TO:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(inv.clientName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (inv.clientEmail.isNotBlank()) Text(inv.clientEmail, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (inv.clientPhone.isNotBlank()) Text(inv.clientPhone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (inv.clientAddress.isNotBlank()) Text(inv.clientAddress, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("DATE OF ISSUE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(dateFormatter.format(Date(inv.issueDate)), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text("DUE DATE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(dateFormatter.format(Date(inv.dueDate)), fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .background(statusBg, shape = RoundedCornerShape(99.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = inv.status.uppercase(),
                                        color = statusText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Spice Product", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(2f))
                            Text("Rate", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text("Qty (kg)", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }

                        // Table Rows
                        items.forEach { line ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = line.spiceName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(2f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = currencyFormatter.format(line.pricePerKg),
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = "${line.quantityKg}",
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = currencyFormatter.format(line.totalPrice),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        // Financial Summary Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.6f),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal:", fontSize = 12.sp)
                                Text(currencyFormatter.format(inv.subtotal), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }

                            if (inv.discountAmount > 0.0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(0.6f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Discount:", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                    Text("- ${currencyFormatter.format(inv.discountAmount)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(0.6f),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("VAT (${inv.taxRate * 100}%):", fontSize = 12.sp)
                                Text(currencyFormatter.format(inv.taxAmount), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }

                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), modifier = Modifier.width(180.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(0.7f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("GRAND TOTAL:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = currencyFormatter.format(inv.grandTotal),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Brand Slogan footer
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Thank you for choosing Ceyvana Premium Spices!",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ceylon Cinnamon • Cardamom • Cloves • Black Pepper",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Receipt Actions panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Mock sharing invoice PDF...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share PDF")
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "Mock printing invoice standard copy...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = "Print")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Print Invoice")
                }
            }
        }
    }
}

// ----------------------------------------------------
// LOGIN SCREEN VIEW
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: InvoiceViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val showForgotPassword by viewModel.showForgotPassword.collectAsStateWithLifecycle()
    val resetSuccessMessage by viewModel.resetSuccessMessage.collectAsStateWithLifecycle()
    var forgotEmail by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8F5))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Elegant Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PremiumBrownGold.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Ceyvana Logo",
                    tint = PremiumBrownGold,
                    modifier = Modifier.size(44.dp)
                )
            }
            
            Text(
                text = "CEYVANA CIMS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = DeepDarkBrown,
                letterSpacing = 2.sp
            )
            
            Text(
                text = "Premium Ceylon Spices • Invoice System v1.0",
                fontSize = 12.sp,
                color = MediumSpiceBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (showForgotPassword) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SubtleBorder),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Forgot Password",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepDarkBrown
                        )
                        Text(
                            text = "Enter your verified business email address. A recovery link will be simulated for testing.",
                            fontSize = 13.sp,
                            color = MediumSpiceBrown
                        )

                        OutlinedTextField(
                            value = forgotEmail,
                            onValueChange = { forgotEmail = it },
                            label = { Text("Business Email Address") },
                            modifier = Modifier.fillMaxWidth().testTag("forgot_email_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        if (resetSuccessMessage != null) {
                            Text(
                                text = resetSuccessMessage ?: "",
                                color = StatusPaidText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                if (forgotEmail.isNotBlank()) {
                                    viewModel.submitForgotPassword(forgotEmail)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("send_reset_link_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Send Reset Link", color = Color.White)
                        }

                        TextButton(
                            onClick = {
                                viewModel.showForgotPassword.value = false
                                viewModel.resetSuccessMessage.value = null
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Back to Sign In", color = PremiumBrownGold)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SubtleBorder),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Log In",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DeepDarkBrown
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("e.g. staff@ceyvana.com") },
                            modifier = Modifier.fillMaxWidth().testTag("login_email"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("login_password"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 13.sp,
                                color = PremiumBrownGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { viewModel.showForgotPassword.value = true }
                                    .padding(4.dp)
                            )
                        }

                        if (loginError != null) {
                            Text(
                                text = loginError ?: "",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = {
                                if (email.isBlank()) {
                                    email = "admin@ceyvana.com"
                                }
                                viewModel.login(email, "Administrator")
                            },
                            modifier = Modifier.fillMaxWidth().testTag("login_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Sign In", color = Color.White)
                        }

                        // Google Sign-In Styled Authentic Button
                        OutlinedButton(
                            onClick = {
                                viewModel.signInWithGoogle()
                            },
                            modifier = Modifier.fillMaxWidth().testTag("google_signin_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SubtleBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepDarkBrown)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Google Icon",
                                    tint = PremiumBrownGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Continue with Google", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Demo Login Header
                Text(
                    text = "QUICK TESTING ROLES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MediumSpiceBrown,
                    letterSpacing = 1.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            email = "admin@ceyvana.com"
                            password = "••••••••"
                            viewModel.login(email, "Administrator")
                        },
                        modifier = Modifier.weight(1f).testTag("quick_login_admin"),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepDarkBrown),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text("Admin", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            email = "manager@ceyvana.com"
                            password = "••••••••"
                            viewModel.login(email, "Manager")
                        },
                        modifier = Modifier.weight(1f).testTag("quick_login_manager"),
                        colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text("Manager", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            email = "sales@ceyvana.com"
                            password = "••••••••"
                            viewModel.login(email, "Sales Staff")
                        },
                        modifier = Modifier.weight(1f).testTag("quick_login_staff"),
                        colors = ButtonDefaults.buttonColors(containerColor = MediumSpiceBrown),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text("Staff", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CUSTOMERS VIEW
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersView(viewModel: InvoiceViewModel) {
    val searchQuery by viewModel.customerSearchQuery.collectAsStateWithLifecycle()
    val filteredClients by viewModel.filteredClients.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var editingCustomerId by remember { mutableStateOf<Int?>(null) }

    // Form states
    var cStore by remember { mutableStateOf("") }
    var cName by remember { mutableStateOf("") }
    var cEmail by remember { mutableStateOf("") }
    var cPhone by remember { mutableStateOf("") }
    var cAddress by remember { mutableStateOf("") }
    var cCountry by remember { mutableStateOf("") }
    var cCity by remember { mutableStateOf("") }
    var cPostalCode by remember { mutableStateOf("") }
    var cNotes by remember { mutableStateOf("") }

    var selectedClientDetail by remember { mutableStateOf<com.example.data.Customer?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search & Add Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Customers Directory",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = DeepDarkBrown
            )
            
            IconButton(
                onClick = {
                    isEditMode = false
                    editingCustomerId = null
                    cStore = ""
                    cName = ""
                    cEmail = ""
                    cPhone = ""
                    cAddress = ""
                    cCountry = ""
                    cCity = ""
                    cPostalCode = ""
                    cNotes = ""
                    showAddDialog = true
                },
                modifier = Modifier
                    .background(PremiumBrownGold, CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Customer", tint = Color.White)
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.customerSearchQuery.value = it },
            placeholder = { Text("Search by name, store, email, city...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("customer_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (filteredClients.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No customers matched your query",
                            color = MediumSpiceBrown,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(filteredClients) { client ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedClientDetail = client }
                            .testTag("customer_card_${client.id}"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SubtleBorder),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(PremiumBrownGold.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (client.name.isNotBlank()) client.name.take(1).uppercase() else "C",
                                        fontWeight = FontWeight.Bold,
                                        color = PremiumBrownGold,
                                        fontSize = 18.sp
                                    )
                                }
                                Column {
                                    Text(
                                        text = client.name,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepDarkBrown
                                    )
                                    if (client.storeName.isNotBlank()) {
                                        Text(
                                            text = client.storeName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PremiumBrownGold
                                        )
                                    }
                                    Text(
                                        text = "${client.city}, ${client.country}",
                                        fontSize = 11.sp,
                                        color = MediumSpiceBrown
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = NumberFormat.getCurrencyInstance(Locale.US).format(client.totalInvoiced),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = PremiumBrownGold
                                )
                                Text(
                                    text = "Total Spent",
                                    fontSize = 9.sp,
                                    color = MediumSpiceBrown,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Customer Detail Dialog
    selectedClientDetail?.let { client ->
        Dialog(onDismissRequest = { selectedClientDetail = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SubtleBorder),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customer Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = DeepDarkBrown
                        )
                        IconButton(onClick = { selectedClientDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(PremiumBrownGold.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (client.name.isNotBlank()) client.name.take(1).uppercase() else "C",
                                    fontWeight = FontWeight.Black,
                                    color = PremiumBrownGold,
                                    fontSize = 28.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(client.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepDarkBrown)
                            if (client.storeName.isNotBlank()) {
                                Text(client.storeName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = PremiumBrownGold)
                            }
                        }
                    }

                    HorizontalDivider(color = SubtleBorder)

                    // Details Rows
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = PremiumBrownGold, modifier = Modifier.size(18.dp))
                        Text(text = "Store: ${client.storeName.ifBlank { "N/A" }}", fontSize = 13.sp, color = DarkNeutralText)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = PremiumBrownGold, modifier = Modifier.size(18.dp))
                        Text(client.email, fontSize = 13.sp, color = DarkNeutralText)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = PremiumBrownGold, modifier = Modifier.size(18.dp))
                        Text(client.phone, fontSize = 13.sp, color = DarkNeutralText)
                    }

                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = PremiumBrownGold, modifier = Modifier.size(18.dp))
                        Column {
                            Text(client.address, fontSize = 13.sp, color = DarkNeutralText)
                            Text("${client.city}, ${client.country} ${client.postalCode}", fontSize = 12.sp, color = MediumSpiceBrown)
                        }
                    }

                    if (client.notes.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = PremiumBrownGold, modifier = Modifier.size(18.dp))
                            Text(client.notes, fontSize = 13.sp, color = DarkNeutralText)
                        }
                    }

                    HorizontalDivider(color = SubtleBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Generated Revenue", fontSize = 12.sp, color = MediumSpiceBrown, fontWeight = FontWeight.Bold)
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale.US).format(client.totalInvoiced),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = StatusPaidText
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Open in Edit Mode
                                isEditMode = true
                                editingCustomerId = client.id
                                cStore = client.storeName
                                cName = client.name
                                cEmail = client.email
                                cPhone = client.phone
                                cAddress = client.address
                                cCountry = client.country
                                cCity = client.city
                                cPostalCode = client.postalCode
                                cNotes = client.notes
                                selectedClientDetail = null
                                showAddDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit")
                        }

                        Button(
                            onClick = {
                                selectedClientDetail = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Done", color = Color.White)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.deleteClient(client)
                            selectedClientDetail = null
                            Toast.makeText(context, "Customer Profile Deleted", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete Customer")
                    }
                }
            }
        }
    }

    // Add / Edit Customer Dialog
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SubtleBorder),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isEditMode) "Edit Customer Profile" else "New Client Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = DeepDarkBrown
                    )

                    OutlinedTextField(
                        value = cStore,
                        onValueChange = { cStore = it },
                        label = { Text("Store/Company Name") },
                        modifier = Modifier.fillMaxWidth().testTag("add_customer_store"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cName,
                        onValueChange = { cName = it },
                        label = { Text("Customer Name") },
                        modifier = Modifier.fillMaxWidth().testTag("add_customer_name"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cEmail,
                        onValueChange = { cEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth().testTag("add_customer_email"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cPhone,
                        onValueChange = { cPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("add_customer_phone"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cAddress,
                        onValueChange = { cAddress = it },
                        label = { Text("Postal Address") },
                        modifier = Modifier.fillMaxWidth().testTag("add_customer_address"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = cCity,
                            onValueChange = { cCity = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f).testTag("add_customer_city"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = cPostalCode,
                            onValueChange = { cPostalCode = it },
                            label = { Text("Postal Code") },
                            modifier = Modifier.weight(1f).testTag("add_customer_postal"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = cCountry,
                        onValueChange = { cCountry = it },
                        label = { Text("Country") },
                        modifier = Modifier.fillMaxWidth().testTag("add_customer_country"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cNotes,
                        onValueChange = { cNotes = it },
                        label = { Text("Internal Business Notes") },
                        modifier = Modifier.fillMaxWidth().testTag("add_customer_notes"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (cName.isBlank()) {
                                    Toast.makeText(context, "Customer name is mandatory", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (isEditMode && editingCustomerId != null) {
                                    val original = filteredClients.find { it.id == editingCustomerId }
                                    if (original != null) {
                                        viewModel.updateClient(
                                            original.copy(
                                                storeName = cStore.trim(),
                                                name = cName.trim(),
                                                phone = cPhone.trim(),
                                                email = cEmail.trim(),
                                                address = cAddress.trim(),
                                                country = cCountry.trim(),
                                                city = cCity.trim(),
                                                postalCode = cPostalCode.trim(),
                                                notes = cNotes.trim()
                                            )
                                        )
                                        Toast.makeText(context, "Customer Profile Updated", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    viewModel.addClient(cStore, cName, cPhone, cEmail, cAddress, cCountry, cCity, cPostalCode, cNotes)
                                    Toast.makeText(context, "Customer Profile Created", Toast.LENGTH_SHORT).show()
                                }
                                showAddDialog = false
                                // Clear inputs
                                cStore = ""
                                cName = ""
                                cEmail = ""
                                cPhone = ""
                                cAddress = ""
                                cCountry = ""
                                cCity = ""
                                cPostalCode = ""
                                cNotes = ""
                            },
                            modifier = Modifier.weight(1f).testTag("add_customer_submit"),
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isEditMode) "Save Changes" else "Save Profile", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// REPORTS VIEW (Analysis, Charts)
// ----------------------------------------------------
@Composable
fun ReportsView(viewModel: InvoiceViewModel) {
    val invoices by viewModel.allInvoices.collectAsStateWithLifecycle()
    val spiceItems by viewModel.allSpiceItems.collectAsStateWithLifecycle()
    val clients by viewModel.allClients.collectAsStateWithLifecycle()

    val totalPaid = invoices.filter { it.invoice.status == "Paid" }.sumOf { it.invoice.grandTotal }
    val totalPending = invoices.filter { it.invoice.status == "Pending" }.sumOf { it.invoice.grandTotal }
    val overallRevenue = totalPaid + totalPending

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Ceyvana Sales Reports",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = DeepDarkBrown
            )
            Text(
                text = "Premium analysis and financial breakdown",
                fontSize = 12.sp,
                color = MediumSpiceBrown
            )
        }

        // Summary Financial Cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Total Generated Revenue Portfolio", fontSize = 12.sp, color = MediumSpiceBrown, fontWeight = FontWeight.Bold)
                    Text(
                        text = currencyFormatter.format(overallRevenue),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = PremiumBrownGold
                    )

                    Divider(color = SubtleBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Collected (Paid)", fontSize = 11.sp, color = MediumSpiceBrown)
                            Text(currencyFormatter.format(totalPaid), fontWeight = FontWeight.Bold, color = StatusPaidText)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Outstanding (Pending)", fontSize = 11.sp, color = MediumSpiceBrown)
                            Text(currencyFormatter.format(totalPending), fontWeight = FontWeight.Bold, color = StatusPendingText)
                        }
                    }
                }
            }
        }

        // Invoice Ratio Meter
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Invoiced Collection Progress", fontSize = 12.sp, color = MediumSpiceBrown, fontWeight = FontWeight.Bold)
                    
                    val paidRatio = if (overallRevenue > 0) (totalPaid / overallRevenue).toFloat() else 0f
                    
                    // Linear Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarmCreamSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(paidRatio)
                                .fillMaxHeight()
                                .background(StatusPaidText)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${(paidRatio * 100).toInt()}% Paid", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusPaidText)
                        Text("${100 - (paidRatio * 100).toInt()}% Outstanding", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusPendingText)
                    }
                }
            }
        }

        // Top Selling Spices list
        item {
            Text(
                text = "Product Revenue & Share Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepDarkBrown,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(spiceItems.take(5)) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = PremiumBrownGold, modifier = Modifier.size(16.dp))
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepDarkBrown)
                        }

                        Text(
                            text = currencyFormatter.format(item.pricePerKg * (if (item.id % 2 == 0) 120.0 else 80.0)),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PremiumBrownGold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(WarmCreamSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (item.id % 2 == 0) 0.85f else 0.45f)
                                .fillMaxHeight()
                                .background(PremiumBrownGold)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// INVENTORY VIEW
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryView(viewModel: InvoiceViewModel) {
    val spiceItems by viewModel.allSpiceItems.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedItemForStock by remember { mutableStateOf<SpiceItem?>(null) }
    var stockInputValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Inventory Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = DeepDarkBrown
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(spiceItems) { spice ->
                val isLowStock = spice.stockLevelKg <= spice.minStockLevelKg
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inventory_card_${spice.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isLowStock) StatusOverdueText.copy(alpha = 0.5f) else SubtleBorder
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLowStock) StatusOverdueBg.copy(alpha = 0.5f) else Color.White
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(spice.name, fontWeight = FontWeight.Bold, color = DeepDarkBrown)
                                Text("SKU-CEY-${spice.id}00", fontSize = 11.sp, color = MediumSpiceBrown)
                            }

                            if (isLowStock) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StatusOverdueBg)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("LOW STOCK", color = StatusOverdueText, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Current Stock Level", fontSize = 10.sp, color = MediumSpiceBrown, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${spice.stockLevelKg} kg",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = if (isLowStock) StatusOverdueText else StatusPaidText
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("/ Min ${spice.minStockLevelKg} kg", fontSize = 11.sp, color = MediumSpiceBrown)
                                }
                            }

                            Button(
                                onClick = {
                                    selectedItemForStock = spice
                                    stockInputValue = spice.stockLevelKg.toString()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLowStock) StatusOverdueText else PremiumBrownGold
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("adjust_stock_${spice.id}")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Adjust Stock", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Adjust Stock Dialog
    selectedItemForStock?.let { spice ->
        Dialog(onDismissRequest = { selectedItemForStock = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SubtleBorder),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Adjust Stock Level",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = DeepDarkBrown
                    )
                    Text(
                        text = spice.name,
                        fontSize = 13.sp,
                        color = MediumSpiceBrown,
                        fontWeight = FontWeight.Bold
                    )

                    Divider(color = SubtleBorder)

                    // Quick Increase buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                val currentVal = stockInputValue.toDoubleOrNull() ?: 0.0
                                stockInputValue = (currentVal + 5.0).toString()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+5 kg")
                        }

                        OutlinedButton(
                            onClick = {
                                val currentVal = stockInputValue.toDoubleOrNull() ?: 0.0
                                stockInputValue = (currentVal + 10.0).toString()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+10 kg")
                        }

                        OutlinedButton(
                            onClick = {
                                val currentVal = stockInputValue.toDoubleOrNull() ?: 0.0
                                val newVal = if (currentVal - 5.0 >= 0.0) currentVal - 5.0 else 0.0
                                stockInputValue = newVal.toString()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("-5 kg")
                        }
                    }

                    OutlinedTextField(
                        value = stockInputValue,
                        onValueChange = { stockInputValue = it },
                        label = { Text("Absolute Stock Level (kg)") },
                        modifier = Modifier.fillMaxWidth().testTag("stock_level_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedItemForStock = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val parsed = stockInputValue.toDoubleOrNull()
                                if (parsed == null || parsed < 0.0) {
                                    Toast.makeText(context, "Invalid stock value entered", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.updateStock(spice.id, parsed)
                                selectedItemForStock = null
                                Toast.makeText(context, "Stock Level Synchronized Successfully", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("save_stock_level_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumBrownGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SETTINGS VIEW
// ----------------------------------------------------
@Composable
fun SettingsView(viewModel: InvoiceViewModel) {
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "System Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = DeepDarkBrown
            )
        }

        // Profile details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SubtleBorder),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(PremiumBrownGold.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = PremiumBrownGold, modifier = Modifier.size(36.dp))
                    }

                    Column {
                        Text(userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepDarkBrown)
                        Text(userEmail, fontSize = 12.sp, color = MediumSpiceBrown)
                    }
                }

                Divider(color = SubtleBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Security Role Access Level", fontSize = 12.sp, color = MediumSpiceBrown)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PremiumBrownGold.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(userRole, color = PremiumBrownGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // App System parameters card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SubtleBorder),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ceyvana Premium Ceylon Spices (Pvt) Ltd", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepDarkBrown)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("CIMS Application Version", fontSize = 12.sp, color = MediumSpiceBrown)
                    Text("1.0.0 Stable (Clean Minimalism)", fontSize = 12.sp, color = DeepDarkBrown, fontWeight = FontWeight.SemiBold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Operational Country Code", fontSize = 12.sp, color = MediumSpiceBrown)
                    Text("LK / USA", fontSize = 12.sp, color = DeepDarkBrown, fontWeight = FontWeight.SemiBold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Database Encryption Sync", fontSize = 12.sp, color = MediumSpiceBrown)
                    Text("Active (Room SQLite)", fontSize = 12.sp, color = StatusPaidText, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Large Red Logout button
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = StatusOverdueText),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_logout")
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Secure Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
