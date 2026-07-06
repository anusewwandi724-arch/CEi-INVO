package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SpiceRepository(
    private val spiceItemDao: SpiceItemDao,
    private val invoiceDao: InvoiceDao,
    private val customerDao: CustomerDao
) {
    val allSpiceItems: Flow<List<SpiceItem>> = spiceItemDao.getAllSpiceItems()
    val allInvoices: Flow<List<InvoiceWithItems>> = invoiceDao.getInvoicesWithItems()
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()

    fun getInvoiceById(id: Int): Flow<InvoiceWithItems?> {
        return invoiceDao.getInvoiceWithItemsById(id)
    }

    suspend fun insertSpiceItem(item: SpiceItem) {
        spiceItemDao.insertSpiceItem(item)
    }

    suspend fun deleteSpiceItem(item: SpiceItem) {
        spiceItemDao.deleteSpiceItem(item)
    }

    suspend fun insertInvoice(invoice: Invoice, items: List<InvoiceLineItem>) {
        invoiceDao.insertInvoiceWithItems(invoice, items)
    }

    suspend fun updateInvoice(invoice: Invoice, items: List<InvoiceLineItem>) {
        invoiceDao.updateInvoiceWithItems(invoice, items)
    }

    suspend fun deleteInvoice(invoice: Invoice) {
        invoiceDao.deleteInvoice(invoice)
    }

    suspend fun insertCustomer(customer: Customer) {
        customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer)
    }

    suspend fun populateIfEmpty() {
        val existing = allSpiceItems.firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaultSpices = listOf(
                SpiceItem(
                    name = "Ceylon Cinnamon Alba",
                    sku = "CEY-CIN-ALBA",
                    description = "The highest grade of pure Ceylon Cinnamon. Extremely thin, sweet, golden quills with an delicate aroma.",
                    pricePerKg = 48.50,
                    category = "Whole Spices"
                ),
                SpiceItem(
                    name = "Ceylon Cinnamon C5 Special",
                    sku = "CEY-CIN-C5SP",
                    description = "Premium C5 grade cinnamon quills. Slender, sweet, with deep spiced highlights and natural golden oils.",
                    pricePerKg = 38.00,
                    category = "Whole Spices"
                ),
                SpiceItem(
                    name = "Ceylon Cardamom Green Jumbo",
                    sku = "CEY-CAR-JUMB",
                    description = "Rare jumbo-sized deep green cardamom pods, meticulously hand-graded from high-altitude estates.",
                    pricePerKg = 75.00,
                    category = "Whole Spices"
                ),
                SpiceItem(
                    name = "Ceylon Cardamom Green Medium",
                    sku = "CEY-CAR-MEDM",
                    description = "Standard selection aromatic cardamom pods. Perfect intense herbal, citrusy-spicy notes.",
                    pricePerKg = 58.00,
                    category = "Whole Spices"
                ),
                SpiceItem(
                    name = "Premium Handpicked Cloves",
                    sku = "CEY-CLV-PREM",
                    description = "Large, plump whole cloves rich in clove essential oil (eugenol). Delivers a pungent, sweet warming flavour.",
                    pricePerKg = 29.50,
                    category = "Whole Spices"
                ),
                SpiceItem(
                    name = "Ceylon Black Pepper G1",
                    sku = "CEY-PEP-G1",
                    description = "Bold, heavy black peppercorns sorted for maximum piperine content, giving a clean, powerful heat and bite.",
                    pricePerKg = 19.80,
                    category = "Whole Spices",
                    stockLevelKg = 4.0,
                    minStockLevelKg = 8.0
                ),
                SpiceItem(
                    name = "Ceylon Nutmeg Whole",
                    sku = "CEY-NUT-WHL",
                    description = "Whole high-grade nutmeg kernels. Richly oil-dense, delivering warm, sweet woodsy aromatic depth.",
                    pricePerKg = 26.00,
                    category = "Whole Spices",
                    stockLevelKg = 18.0,
                    minStockLevelKg = 8.0
                ),
                SpiceItem(
                    name = "Organic Ceylon Saffron",
                    sku = "CEY-SAF-PURE",
                    description = "Ultra-premium delicate crimson stigmas. Infuses dishes with sweet-metallic fragrance and vibrant golden color.",
                    pricePerKg = 1450.00,
                    category = "Luxury Spices",
                    stockLevelKg = 1.5,
                    minStockLevelKg = 3.0
                ),
                SpiceItem(
                    name = "Ceyvana Signature Spice Blend",
                    sku = "CEY-BLD-PREM",
                    description = "Perfect ratio blend of roasted cinnamon, cardamom, cloves, nutmeg, and black pepper for curries.",
                    pricePerKg = 34.00,
                    category = "Blends"
                )
            )
            spiceItemDao.insertSpiceItems(defaultSpices)
        }
    }
}
