package com.example.bulksender.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.bulksender.models.Contact
import com.opencsv.CSVReader
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStreamReader
import kotlin.Exception

class ContactImporter(private val context: Context) {

    /**
     * Import contacts from a file (CSV or Excel)
     * @param fileUri URI of the file to import
     * @return Result containing either the list of contacts or an error message
     */
    fun importContacts(fileUri: Uri): Result<List<Contact>> {
        return try {
            val fileName = getFileName(fileUri)
            
            val contacts = when {
                fileName.endsWith(".csv", ignoreCase = true) -> importFromCSV(fileUri)
                fileName.endsWith(".xlsx", ignoreCase = true) || 
                fileName.endsWith(".xls", ignoreCase = true) -> importFromExcel(fileUri)
                else -> throw IllegalArgumentException("Unsupported file format. Please use CSV or Excel files.")
            }

            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun importFromCSV(fileUri: Uri): List<Contact> {
        val contacts = mutableListOf<Contact>()
        var headers: Array<String>? = null

        context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
            val reader = CSVReader(InputStreamReader(inputStream))
            
            // Read headers
            headers = reader.readNext()?.map { it.trim().lowercase() }?.toTypedArray()
                ?: throw Exception("CSV file is empty or invalid")

            // Validate required columns
            val nameIndex = headers.indexOf("name")
            val phoneIndex = headers.findPhoneColumnIndex()
            
            if (phoneIndex == -1) {
                throw Exception("No phone number column found in CSV")
            }

            // Read data rows
            var row: Array<String>?
            while (reader.readNext().also { row = it } != null) {
                row?.let {
                    if (it.size >= headers.size) {
                        // Extract variables
                        val variables = headers.mapIndexed { index, header ->
                            header to it.getOrNull(index)?.trim().orEmpty()
                        }.toMap().toMutableMap()

                        // Create contact
                        val name = if (nameIndex != -1) it[nameIndex].trim() else ""
                        val phone = it[phoneIndex].trim()
                        
                        if (Contact.isValidPhoneNumber(phone)) {
                            contacts.add(
                                Contact(
                                    name = name,
                                    phoneNumber = Contact.formatPhoneNumber(phone),
                                    variables = variables,
                                    isValid = true
                                )
                            )
                        }
                    }
                }
            }
        }

        return contacts
    }

    private fun importFromExcel(fileUri: Uri): List<Contact> {
        val contacts = mutableListOf<Contact>()

        context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            // Read headers
            val headerRow = sheet.getRow(0)
            val headers = (0 until headerRow.lastCellNum).map { 
                headerRow.getCell(it)?.stringCellValue?.trim()?.lowercase() ?: ""
            }.toTypedArray()

            // Validate required columns
            val nameIndex = headers.indexOf("name")
            val phoneIndex = headers.findPhoneColumnIndex()
            
            if (phoneIndex == -1) {
                throw Exception("No phone number column found in Excel")
            }

            // Read data rows
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex)
                if (row != null) {
                    // Extract variables
                    val variables = headers.mapIndexed { index, header ->
                        header to (row.getCell(index)?.toString()?.trim() ?: "")
                    }.toMap().toMutableMap()

                    // Create contact
                    val name = if (nameIndex != -1) {
                        row.getCell(nameIndex)?.toString()?.trim() ?: ""
                    } else ""
                    
                    val phone = row.getCell(phoneIndex)?.toString()?.trim() ?: ""
                    
                    if (Contact.isValidPhoneNumber(phone)) {
                        contacts.add(
                            Contact(
                                name = name,
                                phoneNumber = Contact.formatPhoneNumber(phone),
                                variables = variables,
                                isValid = true
                            )
                        )
                    }
                }
            }
        }

        return contacts
    }

    private fun getFileName(uri: Uri): String {
        var fileName = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        return fileName
    }

    private fun Array<String>.findPhoneColumnIndex(): Int {
        val phoneHeaders = listOf("phone", "phone number", "mobile", "contact", "number")
        return this.indexOfFirst { header -> 
            phoneHeaders.any { it == header.lowercase() }
        }
    }

    companion object {
        private const val TAG = "ContactImporter"
    }
}
