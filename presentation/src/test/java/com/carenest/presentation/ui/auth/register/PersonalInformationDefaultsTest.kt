package com.carenest.presentation.ui.auth.register

import org.junit.Assert.assertEquals
import org.junit.Test

class PersonalInformationDefaultsTest {

    @Test
    fun `backend user placeholder is not shown as editable first name`() {
        assertEquals("", "User".toEditableFirstName())
        assertEquals("", "user".toEditableFirstName())
    }

    @Test
    fun `real first name remains editable`() {
        assertEquals("Aya", "Aya".toEditableFirstName())
    }
}
