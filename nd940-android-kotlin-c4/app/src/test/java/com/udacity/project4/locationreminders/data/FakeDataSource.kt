package com.udacity.project4.locationreminders.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.data.ReminderDataSource


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource{

    //private var _shouldreturnerror=MutableLiveData<Boolean>()
    private var shouldreturnerror=false

    fun setReturnError(errorstate: Boolean) {
       // shouldreturnerror.value=errorstate
        shouldreturnerror=errorstate
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
        if (shouldreturnerror) {
            return Result.Error("Test exception")
        }
        var result = reminders?.find { it.id == id }

        if (result != null) {
            return Result.Success(result)
        }
        else {return Result.Error("oppsie no reminder there")
    }}

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}


