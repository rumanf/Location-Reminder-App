package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {


    private var shouldreturnerror=false

    fun setReturnError(errorstate: Boolean) {
        shouldreturnerror = errorstate
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldreturnerror) {
            return Result.Error("Test exception")
        }
        reminders?.let { return Result.Success(ArrayList(it)) }
        return Result.Error("Reminders not found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        var result = reminders?.find { it.id == id }

        if (shouldreturnerror) {
            return Result.Error("Test exception")
        }

        if (result != null) {
            return Result.Success(result)
        }
        else {return Result.Error("oppsie no reminder there")
    }}

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}


