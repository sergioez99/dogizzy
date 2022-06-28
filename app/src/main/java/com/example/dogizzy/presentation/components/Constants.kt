package com.example.dogizzy.presentation.components

object Constants {
    const val TAG = "Dogizzy"

    const val MESSAGES = "messages"
    const val MESSAGE = "message"
    const val SENT_BY = "sent_by"
    const val SENT_ON = "sent_on"
    val IS_CURRENT_USER = "is_current_user"

    const val DATE_MASK = "--/--/----"
    const val DATE_LENGTH = 8 // Equals to "##/##/####".count { it == '#' }
}