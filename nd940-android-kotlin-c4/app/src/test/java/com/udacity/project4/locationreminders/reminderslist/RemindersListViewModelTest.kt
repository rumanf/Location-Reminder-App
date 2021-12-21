package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    //tests viewmodel and live data item

    private lateinit var testFakeDataSource: FakeDataSource
//
    @Before
    fun setupFakeDataSource() {
        MockitoAnnotations.initMocks(this)
        testFakeDataSource = FakeDataSource()
    }



    @Test
    fun addNoData_returnsEmpty() {
        val RemindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())
        RemindersListViewModel.loadReminders()
        assertThat(RemindersListViewModel.showNoData.value, `is`(true))
    }

    @Test
    fun loadWhenRemindersAreUnavailable_callErrorToDisplay()=mainCoroutineRule.runBlockingTest {
        val fakeDataSource=FakeDataSource()
        fakeDataSource.setReturnError(true)

        val RemindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)

        // WHEN - Get the reminders
        RemindersListViewModel.loadReminders()

        //refesh function from fakedatasource
        //assertThat(RemindersListViewModel.showNoData.value, `is`(true))
        assertThat(RemindersListViewModel.showSnackBar.value, (checkNotNull( true)))
    }

    @Test
    fun check_loading_status() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()

        val RemindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), testFakeDataSource)

        val reminderItem=ReminderDTO(
            "Title",
            "Description",
            "Location",
            0.0,
            0.0
        )

        testFakeDataSource.saveReminder(reminderItem)

        mainCoroutineRule.dispatcher.pauseDispatcher()
        RemindersListViewModel.loadReminders()

        var loading = RemindersListViewModel.showLoading.getOrAwaitValue()

        assertThat(loading, `is`(true) )

        mainCoroutineRule.resumeDispatcher()

         loading = RemindersListViewModel.showLoading.getOrAwaitValue()

        assertThat(loading, `is`(false))
    }




//    @Test
//    fun testLiveData()= mainCoroutineRule.runBlockingTest{
//
//        // Given a fresh TasksViewModel
//        val RemindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())
//
//
//        // When adding a new task
//        var reminderlist = RemindersListViewModel.remindersList.getOrAwaitValue()
//        RemindersListViewModel.loadReminders()
//
//        // Then the new task event is triggered
//
//        assertThat(reminderlist,  (checkNotNull( false)))
//
//    }








}

