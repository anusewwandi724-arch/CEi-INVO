package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SpiceItemDao {
    @Query("SELECT * FROM spice_items ORDER BY name ASC")
    fun getAllSpiceItems(): Flow<List<SpiceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpiceItem(item: SpiceItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpiceItems(items: List<SpiceItem>)

    @Delete
    suspend fun deleteSpiceItem(item: SpiceItem)
}

@Dao
interface InvoiceDao {
    @Transaction
    @Query("SELECT * FROM invoices ORDER BY issueDate DESC")
    fun getInvoicesWithItems(): Flow<List<InvoiceWithItems>>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id")
    fun getInvoiceWithItemsById(id: Int): Flow<InvoiceWithItems?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItems(items: List<InvoiceLineItem>)

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    @Query("DELETE FROM invoice_line_items WHERE invoiceId = :invoiceId")
    suspend fun deleteLineItemsForInvoice(invoiceId: Int)

    @Transaction
    suspend fun insertInvoiceWithItems(invoice: Invoice, items: List<InvoiceLineItem>) {
        val invoiceId = insertInvoice(invoice).toInt()
        val itemsWithId = items.map { it.copy(invoiceId = invoiceId) }
        insertLineItems(itemsWithId)
    }

    @Transaction
    suspend fun updateInvoiceWithItems(invoice: Invoice, items: List<InvoiceLineItem>) {
        updateInvoice(invoice)
        deleteLineItemsForInvoice(invoice.id)
        val itemsWithId = items.map { it.copy(invoiceId = invoice.id) }
        insertLineItems(itemsWithId)
    }
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<Customer>)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("DELETE FROM customers")
    suspend fun deleteAllCustomers()
}

