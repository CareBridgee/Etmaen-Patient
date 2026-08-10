package com.carenest.presentation.ui.auth.register

import org.junit.Assert.assertEquals
import org.junit.Test

class DateInputFormatterTest {

    @Test
    fun `adds separators while typing a date`() {
        assertEquals("1", formatDateInput("", "1"))
        assertEquals("12/", formatDateInput("1", "12"))
        assertEquals("12/3", formatDateInput("12/", "12/3"))
        assertEquals("12/34/", formatDateInput("12/3", "12/34"))
        assertEquals("12/34/2001", formatDateInput("12/34/200", "12/34/2001"))
    }

    @Test
    fun `normalizes pasted date and ignores unsupported characters`() {
        assertEquals("08/25/1995", formatDateInput("", "08-25-1995"))
        assertEquals("08/25/1995", formatDateInput("", "08 / 25 / 1995abc"))
    }

    @Test
    fun `keeps year digits in order after both automatic separators`() {
        val typedValues = listOf("0", "04", "04/2", "04/25", "04/25/2", "04/25/20", "04/25/200", "04/25/2005")
        var formatted = ""

        typedValues.forEach { incoming ->
            formatted = formatDateInput(formatted, incoming)
        }

        assertEquals("04/25/2005", formatted)
    }

    @Test
    fun `allows a generated separator to be deleted`() {
        assertEquals("12", formatDateInput("12/", "12"))
    }
}
