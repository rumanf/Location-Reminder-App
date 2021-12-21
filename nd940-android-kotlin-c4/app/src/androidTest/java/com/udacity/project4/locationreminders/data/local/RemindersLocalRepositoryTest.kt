package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.util.MainCoroutineRuleAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRuleAndroidTest()

    private val reminder1 = ReminderDTO("Title1", "Description1", "location1", 0.0, 0.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "location2", 1.0, 1.0)

    private val Reminderslist = listOf(reminder1,reminder2).sortedBy { it.id }
    private lateinit var database: RemindersDatabase

    // Class under test
    private lateinit var remindersRepository: RemindersLocalRepository


    @Before
    fun createRepository() {

        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        // Get a reference to the class under test
        remindersRepository = RemindersLocalRepository(
            database.reminderDao(), Dispatchers.Main
        )
    }


    @After
    fun closeDb() = database.close()


    @Test
    fun savereminder_requestsFromLocalDataSource()=mainCoroutineRule.runBlockingTest{
        // When tasks are requested from the tasks repository
        remindersRepository.saveReminder(reminder1)
        remindersRepository.saveReminder(reminder2)

        val repositoryresult=remindersRepository.getReminders() as Result.Success
        // Then tasks are loaded from the remote data source
        assertThat(repositoryresult.data.size,`is`(Reminderslist.size))

   //next test
        val repositoryresult2=remindersRepository.getReminder("0") as Result.Success
        assertThat(repositoryresult2.data.id,`is`(reminder1.id))

    //last test erro
        val repositoryresult3=remindersRepository.getReminder("3")
        assertThat(repositoryresult3 is Result.Error, notNullValue())
        repositoryresult3 as Result.Error
        assertThat(repositoryresult3.message, `is`("Reminder not found!"))

    }


}



