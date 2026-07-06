package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation

@Entity(tableName = "spice_items")
data class SpiceItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sku: String,
    val description: String,
    val pricePerKg: Double,
    val category: String, // e.g., "Whole Spices", "Ground Spices", "Blends"
    val stockLevelKg: Double = 25.0,
    val minStockLevelKg: Double = 8.0,
    val packSizes: String = "25g, 50g, 100g, 250g, 500g, 1kg",
    val packPrices: String = "", // e.g. "25g:1.50, 50g:2.80"
    val status: String = "Active",
    val imageUrl: String = ""
) {
    fun getPackPricesMap(): Map<String, Double> {
        if (packPrices.isBlank()) {
            val sizes = listOf("25g", "50g", "100g", "150g", "200g", "250g", "400g", "500g", "750g", "1kg")
            return sizes.associateWith { size ->
                val grams = when {
                    size.endsWith("kg") -> size.replace("kg", "").toDoubleOrNull()?.let { it * 1000 } ?: 1000.0
                    size.endsWith("g") -> size.replace("g", "").toDoubleOrNull() ?: 100.0
                    else -> 100.0
                }
                val proportional = (grams / 1000.0) * pricePerKg
                val markup = when {
                    grams <= 50 -> 1.25
                    grams <= 200 -> 1.15
                    grams <= 500 -> 1.05
                    else -> 1.0
                }
                Math.round(proportional * markup * 100.0) / 100.0
            }
        }
        
        return packPrices.split(",").mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                val size = parts[0].trim()
                val price = parts[1].trim().toDoubleOrNull()
                if (price != null) size to price else null
            } else null
        }.toMap()
    }

    fun getAvailablePackPrices(): Map<String, Double> {
        val allPrices = getPackPricesMap()
        val activeSizes = packSizes.split(",").map { it.trim() }.filter { it.isNotBlank() }
        if (activeSizes.isEmpty()) {
            return allPrices
        }
        return activeSizes.associateWith { size ->
            allPrices[size] ?: run {
                val grams = when {
                    size.endsWith("kg") -> size.replace("kg", "").toDoubleOrNull()?.let { it * 1000 } ?: 1000.0
                    size.endsWith("g") -> size.replace("g", "").toDoubleOrNull() ?: 100.0
                    else -> 100.0
                }
                val proportional = (grams / 1000.0) * pricePerKg
                Math.round(proportional * 100.0) / 100.0
            }
        }
    }
}

@Entity(
    tableName = "invoices"
)
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceNumber: String,
    val clientName: String,
    val clientEmail: String,
    val clientPhone: String,
    val clientAddress: String,
    val issueDate: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L), // default 7 days due
    val status: String, // "Paid", "Pending", "Draft"
    val taxRate: Double = 0.08, // 8% VAT
    val discountAmount: Double = 0.0,
    val subtotal: Double = 0.0,
    val taxAmount: Double = 0.0,
    val grandTotal: Double = 0.0
)

@Entity(
    tableName = "invoice_line_items",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class InvoiceLineItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceId: Int,
    val spiceItemId: Int,
    val spiceName: String,
    val pricePerKg: Double,
    val quantityKg: Double,
    val totalPrice: Double
)

data class InvoiceWithItems(
    @Embedded val invoice: Invoice,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val lineItems: List<InvoiceLineItem>
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val storeName: String,
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val country: String,
    val city: String,
    val postalCode: String,
    val notes: String,
    val totalInvoiced: Double = 0.0
)

