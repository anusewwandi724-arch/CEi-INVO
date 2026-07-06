package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SpiceItem::class, Invoice::class, InvoiceLineItem::class, Customer::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun spiceItemDao(): SpiceItemDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun customerDao(): CustomerDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.spiceItemDao(), database.invoiceDao(), database.customerDao())
                }
            }
        }

        private suspend fun populateDatabase(spiceDao: SpiceItemDao, invoiceDao: InvoiceDao, customerDao: CustomerDao) {
            // Default Spices of Ceyvana Premium Ceylon Spices
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
            spiceDao.insertSpiceItems(defaultSpices)

            // Let's also insert a default invoice for demo presentation
            val defaultInvoice = Invoice(
                invoiceNumber = "INV-2026-001",
                clientName = "Gourmet Flavors Distributors Ltd",
                clientEmail = "procurement@gourmetflavors.co.uk",
                clientPhone = "+44 20 7946 0958",
                clientAddress = "Suite 4B, Spice Way, London, UK",
                issueDate = System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000L), // 5 days ago
                dueDate = System.currentTimeMillis() + (9 * 24 * 60 * 60 * 1000L),  // 9 days from now
                status = "Paid",
                taxRate = 0.08,
                discountAmount = 50.0,
                subtotal = 312.5,
                taxAmount = 21.0,
                grandTotal = 283.5
            )
            
            val invoiceId = invoiceDao.insertInvoice(defaultInvoice).toInt()
            
            val defaultLineItems = listOf(
                InvoiceLineItem(
                    invoiceId = invoiceId,
                    spiceItemId = 1,
                    spiceName = "Ceylon Cinnamon Alba",
                    pricePerKg = 48.50,
                    quantityKg = 5.0,
                    totalPrice = 242.50
                ),
                InvoiceLineItem(
                    invoiceId = invoiceId,
                    spiceItemId = 3,
                    spiceName = "Ceylon Cardamom Green Jumbo",
                    pricePerKg = 75.00,
                    quantityKg = 1.0,
                    totalPrice = 75.00
                )
            )
            invoiceDao.insertLineItems(defaultLineItems)

            // Insert Default Customers
            val defaultCustomers = listOf(
                Customer(
                    storeName = "Global Curry Hub",
                    name = "John Curry",
                    phone = "+1 415 555 0192",
                    email = "info@globalcurry.com",
                    address = "12 Market St",
                    country = "United States",
                    city = "San Francisco",
                    postalCode = "94103",
                    notes = "Prefers Alba Cinnamon",
                    totalInvoiced = 14200.0
                ),
                Customer(
                    storeName = "Spice Traders Ltd.",
                    name = "Arthur Spice",
                    phone = "+44 20 7946 0912",
                    email = "orders@spicetraders.co.uk",
                    address = "45 Curry Lane",
                    country = "United Kingdom",
                    city = "London",
                    postalCode = "E1 6AN",
                    notes = "Bulk shipments",
                    totalInvoiced = 84500.0
                ),
                Customer(
                    storeName = "Ceylonese Bites",
                    name = "Nimal Perera",
                    phone = "+61 2 9382 0192",
                    email = "hello@ceylonesebites.com",
                    address = "88 Spice Court",
                    country = "Australia",
                    city = "Sydney",
                    postalCode = "2000",
                    notes = "Gourmet retail",
                    totalInvoiced = 9800.0
                ),
                Customer(
                    storeName = "Gourmet Flavors Distributors Ltd",
                    name = "Sarah Jenkins",
                    phone = "+44 20 7946 0958",
                    email = "procurement@gourmetflavors.co.uk",
                    address = "Suite 4B, Spice Way",
                    country = "United Kingdom",
                    city = "London",
                    postalCode = "EC1A 1BB",
                    notes = "Premium spices distributor",
                    totalInvoiced = 42500.0
                )
            )
            customerDao.insertCustomers(defaultCustomers)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ceyvana_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
