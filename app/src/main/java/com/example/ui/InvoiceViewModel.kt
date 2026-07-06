package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Invoice
import com.example.data.InvoiceLineItem
import com.example.data.InvoiceWithItems
import com.example.data.SpiceItem
import com.example.data.SpiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class InvoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SpiceRepository

    val allSpiceItems: StateFlow<List<SpiceItem>>
    val allInvoices: StateFlow<List<InvoiceWithItems>>

    // Authentication State
    val isLoggedIn = MutableStateFlow(false) // Set to false to show the login screen initially
    val userRole = MutableStateFlow("Administrator") // "Administrator", "Manager", "Sales Staff"
    val userEmail = MutableStateFlow("admin@ceyvana.com")
    val userName = MutableStateFlow("Admin Ceyvana")
    val loginError = MutableStateFlow<String?>(null)
    val showForgotPassword = MutableStateFlow(false)
    val resetSuccessMessage = MutableStateFlow<String?>(null)

    // Customers / CRM list
    val allClients: StateFlow<List<com.example.data.Customer>>
    val customerSearchQuery = MutableStateFlow("")
    val filteredClients: StateFlow<List<com.example.data.Customer>>

    // Dashboard reactive statistics
    val todaySales: StateFlow<Double>
    val monthlySales: StateFlow<Double>
    val pendingSalesAmount: StateFlow<Double>
    val stockAlertsCount: StateFlow<Int>

    // Search and Filters
    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow("All") // "All", "Paid", "Pending", "Draft"

    // Filtered Invoices
    val filteredInvoices: StateFlow<List<InvoiceWithItems>>

    // Navigation and Active View State
    val currentTab = MutableStateFlow("Dashboard") // "Dashboard", "Invoices", "Products", "Customers", "Reports", "Inventory", "Settings"
    val selectedInvoice = MutableStateFlow<InvoiceWithItems?>(null)
    val editingInvoice = MutableStateFlow<InvoiceWithItems?>(null)

    // Invoice Form State
    val clientName = MutableStateFlow("")
    val clientEmail = MutableStateFlow("")
    val clientPhone = MutableStateFlow("")
    val clientAddress = MutableStateFlow("")
    val invoiceNumber = MutableStateFlow("")
    val taxRate = MutableStateFlow(0.08) // Default 8% VAT
    val discountAmount = MutableStateFlow(0.0)
    val invoiceStatus = MutableStateFlow("Pending") // "Paid", "Pending", "Draft"
    val issueDate = MutableStateFlow(System.currentTimeMillis())
    val dueDate = MutableStateFlow(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L))

    // Selected spice items with their respective quantities (kg)
    val selectedLineItems = MutableStateFlow<List<FormLineItem>>(emptyList())

    // Calculations
    val subtotal: StateFlow<Double>
    val taxAmount: StateFlow<Double>
    val grandTotal: StateFlow<Double>

    data class FormLineItem(
        val spiceItem: SpiceItem,
        val selectedPackSize: String = "1kg",
        val selectedUnitPrice: Double = spiceItem.pricePerKg,
        val quantityKg: Double,
        val totalPrice: Double
    )

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = SpiceRepository(database.spiceItemDao(), database.invoiceDao(), database.customerDao())

        allSpiceItems = repository.allSpiceItems
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allInvoices = repository.allInvoices
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allClients = repository.allCustomers
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        filteredClients = combine(allClients, customerSearchQuery) { clients, query ->
            if (query.isBlank()) {
                clients
            } else {
                clients.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.storeName.contains(query, ignoreCase = true) ||
                            it.email.contains(query, ignoreCase = true) ||
                            it.phone.contains(query, ignoreCase = true) ||
                            it.city.contains(query, ignoreCase = true) ||
                            it.country.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Statistics Flow setup
        todaySales = allInvoices.combine(allInvoices) { invoices, _ ->
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val todayStart = calendar.timeInMillis
            
            invoices.filter {
                it.invoice.status == "Paid" && it.invoice.issueDate >= todayStart
            }.sumOf { it.invoice.grandTotal }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        monthlySales = allInvoices.combine(allInvoices) { invoices, _ ->
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val monthStart = calendar.timeInMillis
            
            invoices.filter {
                it.invoice.status == "Paid" && it.invoice.issueDate >= monthStart
            }.sumOf { it.invoice.grandTotal }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        pendingSalesAmount = allInvoices.combine(allInvoices) { invoices, _ ->
            invoices.filter { it.invoice.status == "Pending" }.sumOf { it.invoice.grandTotal }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        stockAlertsCount = allSpiceItems.combine(allSpiceItems) { items, _ ->
            items.count { it.stockLevelKg <= it.minStockLevelKg }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

        // Double safety pre-population call
        viewModelScope.launch {
            repository.populateIfEmpty()
        }

        // Combine filter states with base invoices
        filteredInvoices = combine(allInvoices, searchQuery, statusFilter) { invoices, query, filter ->
            invoices.filter { item ->
                val matchesQuery = item.invoice.invoiceNumber.contains(query, ignoreCase = true) ||
                        item.invoice.clientName.contains(query, ignoreCase = true) ||
                        item.invoice.clientEmail.contains(query, ignoreCase = true)
                
                val matchesFilter = filter == "All" || item.invoice.status == filter
                
                matchesQuery && matchesFilter
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Auto calculate subtotal from selected line items
        subtotal = selectedLineItems.combine(discountAmount) { items, discount ->
            val totalOfItems = items.sumOf { it.totalPrice }
            maxOf(0.0, totalOfItems)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        // Calculate tax based on subtotal (adjusted for discount)
        taxAmount = combine(subtotal, taxRate, discountAmount) { sub, rate, disc ->
            val netSubtotal = maxOf(0.0, sub - disc)
            netSubtotal * rate
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        // Grand Total
        grandTotal = combine(subtotal, taxAmount, discountAmount) { sub, tax, disc ->
            val netSubtotal = maxOf(0.0, sub - disc)
            netSubtotal + tax
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        generateNextInvoiceNumber()
    }

    fun generateNextInvoiceNumber() {
        viewModelScope.launch {
            val count = allInvoices.value.size
            val year = 2026
            val numStr = String.format("%03d", count + 1)
            invoiceNumber.value = "CEY-$year-$numStr"
        }
    }

    // Add spice to invoice
    fun addSpiceToInvoice(spice: SpiceItem, qty: Double, packSize: String = "1kg", unitPrice: Double = spice.pricePerKg) {
        val currentList = selectedLineItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.spiceItem.id == spice.id && it.selectedPackSize == packSize }
        
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            val newQty = existing.quantityKg + qty
            currentList[existingIndex] = FormLineItem(
                spiceItem = spice,
                selectedPackSize = packSize,
                selectedUnitPrice = unitPrice,
                quantityKg = newQty,
                totalPrice = newQty * unitPrice
            )
        } else {
            currentList.add(
                FormLineItem(
                    spiceItem = spice,
                    selectedPackSize = packSize,
                    selectedUnitPrice = unitPrice,
                    quantityKg = qty,
                    totalPrice = qty * unitPrice
                )
            )
        }
        selectedLineItems.value = currentList
    }

    // Legacy fallback
    fun addSpiceToInvoice(spice: SpiceItem, qty: Double) {
        addSpiceToInvoice(spice, qty, "1kg", spice.pricePerKg)
    }

    fun updateLineItemQuantity(spiceId: Int, packSize: String, qty: Double) {
        if (qty <= 0) {
            removeSpiceFromInvoice(spiceId, packSize)
            return
        }
        val currentList = selectedLineItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.spiceItem.id == spiceId && it.selectedPackSize == packSize }
        if (index >= 0) {
            val item = currentList[index]
            currentList[index] = FormLineItem(
                spiceItem = item.spiceItem,
                selectedPackSize = packSize,
                selectedUnitPrice = item.selectedUnitPrice,
                quantityKg = qty,
                totalPrice = qty * item.selectedUnitPrice
            )
            selectedLineItems.value = currentList
        }
    }

    // Legacy fallback
    fun updateLineItemQuantity(spiceId: Int, qty: Double) {
        val current = selectedLineItems.value.find { it.spiceItem.id == spiceId }
        val packSize = current?.selectedPackSize ?: "1kg"
        updateLineItemQuantity(spiceId, packSize, qty)
    }

    fun removeSpiceFromInvoice(spiceId: Int, packSize: String) {
        val currentList = selectedLineItems.value.toMutableList()
        currentList.removeAll { it.spiceItem.id == spiceId && it.selectedPackSize == packSize }
        selectedLineItems.value = currentList
    }

    // Legacy fallback
    fun removeSpiceFromInvoice(spiceId: Int) {
        val currentList = selectedLineItems.value.toMutableList()
        currentList.removeAll { it.spiceItem.id == spiceId }
        selectedLineItems.value = currentList
    }

    // Add a brand-new custom spice item to database
    fun createSpiceItem(
        name: String,
        sku: String,
        description: String,
        pricePerKg: Double,
        category: String,
        packSizes: String = "25g, 50g, 100g, 250g, 500g, 1kg",
        packPrices: String = "",
        status: String = "Active",
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            repository.insertSpiceItem(
                SpiceItem(
                    name = name,
                    sku = sku.uppercase(),
                    description = description,
                    pricePerKg = pricePerKg,
                    category = category,
                    packSizes = packSizes,
                    packPrices = packPrices,
                    status = status,
                    imageUrl = imageUrl
                )
            )
        }
    }

    // Update an existing spice item
    fun updateSpiceItem(spice: SpiceItem) {
        viewModelScope.launch {
            repository.insertSpiceItem(spice)
        }
    }

    // Delete a spice from catalog
    fun deleteSpice(spice: SpiceItem) {
        viewModelScope.launch {
            repository.deleteSpiceItem(spice)
        }
    }

    private fun parsePackSizeFromName(fullName: String): String {
        val regex = """\(([^)]+)\)""".toRegex()
        val match = regex.find(fullName)
        return match?.groupValues?.get(1) ?: "1kg"
    }

    private fun cleanSpiceName(fullName: String): String {
        return fullName.replace("""\s*\([^)]+\)""".toRegex(), "").trim()
    }

    // Load invoice for editing
    fun loadInvoiceForEditing(invoiceWithItems: InvoiceWithItems) {
        editingInvoice.value = invoiceWithItems
        val inv = invoiceWithItems.invoice
        
        clientName.value = inv.clientName
        clientEmail.value = inv.clientEmail
        clientPhone.value = inv.clientPhone
        clientAddress.value = inv.clientAddress
        invoiceNumber.value = inv.invoiceNumber
        taxRate.value = inv.taxRate
        discountAmount.value = inv.discountAmount
        invoiceStatus.value = inv.status
        issueDate.value = inv.issueDate
        dueDate.value = inv.dueDate

        // Map line items back to form items
        val list = invoiceWithItems.lineItems.map { line ->
            val cleanName = cleanSpiceName(line.spiceName)
            val matchedSpice = allSpiceItems.value.find { it.id == line.spiceItemId } ?: SpiceItem(
                id = line.spiceItemId,
                name = cleanName,
                sku = "",
                description = "",
                pricePerKg = line.pricePerKg,
                category = "Uncategorized"
            )
            val packSize = parsePackSizeFromName(line.spiceName)
            FormLineItem(
                spiceItem = matchedSpice,
                selectedPackSize = packSize,
                selectedUnitPrice = line.pricePerKg,
                quantityKg = line.quantityKg,
                totalPrice = line.totalPrice
            )
        }
        selectedLineItems.value = list
        currentTab.value = "Create"
    }

    fun clearForm() {
        editingInvoice.value = null
        clientName.value = ""
        clientEmail.value = ""
        clientPhone.value = ""
        clientAddress.value = ""
        taxRate.value = 0.08
        discountAmount.value = 0.0
        invoiceStatus.value = "Pending"
        issueDate.value = System.currentTimeMillis()
        dueDate.value = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
        selectedLineItems.value = emptyList()
        generateNextInvoiceNumber()
    }

    fun saveInvoice() {
        if (clientName.value.isBlank() || selectedLineItems.value.isEmpty()) return

        viewModelScope.launch {
            val netSubtotal = subtotal.value
            val netTax = taxAmount.value
            val netTotal = grandTotal.value

            val editId = editingInvoice.value?.invoice?.id ?: 0
            val inv = Invoice(
                id = editId,
                invoiceNumber = invoiceNumber.value,
                clientName = clientName.value.trim(),
                clientEmail = clientEmail.value.trim(),
                clientPhone = clientPhone.value.trim(),
                clientAddress = clientAddress.value.trim(),
                issueDate = issueDate.value,
                dueDate = dueDate.value,
                status = invoiceStatus.value,
                taxRate = taxRate.value,
                discountAmount = discountAmount.value,
                subtotal = netSubtotal,
                taxAmount = netTax,
                grandTotal = netTotal
            )

            val lineItems = selectedLineItems.value.map { item ->
                val displayName = if (item.selectedPackSize.lowercase() == "custom") {
                    item.spiceItem.name
                } else {
                    "${item.spiceItem.name} (${item.selectedPackSize})"
                }
                InvoiceLineItem(
                    invoiceId = editId,
                    spiceItemId = item.spiceItem.id,
                    spiceName = displayName,
                    pricePerKg = item.selectedUnitPrice,
                    quantityKg = item.quantityKg,
                    totalPrice = item.totalPrice
                )
            }

            if (editId > 0) {
                repository.updateInvoice(inv, lineItems)
            } else {
                repository.insertInvoice(inv, lineItems)
            }

            clearForm()
            currentTab.value = "Invoices"
        }
    }

    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
            if (selectedInvoice.value?.invoice?.id == invoice.id) {
                selectedInvoice.value = null
            }
        }
    }

    // Authentication & Role helper methods
    fun login(email: String, role: String) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) {
            loginError.value = "Email cannot be empty"
            return
        }
        userEmail.value = cleanEmail
        userRole.value = role
        userName.value = when (role) {
            "Administrator" -> "Admin Ceyvana"
            "Manager" -> "Manager Ceyvana"
            else -> "Sales Staff Member"
        }
        loginError.value = null
        isLoggedIn.value = true
    }

    fun logout() {
        isLoggedIn.value = false
        currentTab.value = "Dashboard"
        clearForm()
    }

    fun signInWithGoogle() {
        userEmail.value = "google.partner@ceyvana.com"
        userRole.value = "Administrator"
        userName.value = "Google Partner Account"
        loginError.value = null
        isLoggedIn.value = true
    }

    fun submitForgotPassword(email: String) {
        if (email.isBlank()) return
        resetSuccessMessage.value = "A password reset link has been sent to $email."
    }

    fun addClient(
        storeName: String,
        name: String,
        phone: String,
        email: String,
        address: String,
        country: String,
        city: String,
        postalCode: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertCustomer(
                com.example.data.Customer(
                    storeName = storeName.trim(),
                    name = name.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    address = address.trim(),
                    country = country.trim(),
                    city = city.trim(),
                    postalCode = postalCode.trim(),
                    notes = notes.trim(),
                    totalInvoiced = 0.0
                )
            )
        }
    }

    fun updateClient(customer: com.example.data.Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
        }
    }

    fun deleteClient(customer: com.example.data.Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    fun updateStock(spiceId: Int, newStock: Double) {
        viewModelScope.launch {
            val item = allSpiceItems.value.find { it.id == spiceId }
            if (item != null) {
                repository.insertSpiceItem(item.copy(stockLevelKg = newStock))
            }
        }
    }
}
