package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

        @get:Rule
        var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var testFakeDataSource: FakeDataSource

    @Before
    fun setupFakeDataSource() {
        MockitoAnnotations.initMocks(this)
        testFakeDataSource = FakeDataSource()
    }


    @Test
    fun validatedata_incompletedata_return()= mainCoroutineRule.runBlockingTest{

        // Given a fresh TasksViewModel
        val saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())
        val reminderItem=ReminderDataItem(
            "",
            "Description",
            "Location",
            0.0,
            0.0
        )

        // When adding a new task
        val test= saveReminderViewModel.validateEnteredData(reminderItem)

        // Then the new task event is triggered
        assertThat(test, `is`(false))

    }

//    @Test
//    fun saveReminder_RemindersSaved()= mainCoroutineRule.runBlockingTest{
//
//        // Given a fresh TasksViewModel
//        val saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), testFakeDataSource)
//        val reminderItem= ReminderDTO(
//            "Title",
//            "Description",
//            "Location",
//            0.0,
//            0.0
//        )
//
//
//        // When adding a new task
//
//        testFakeDataSource.saveReminder(reminderItem)
//        mainCoroutineRule.dispatcher.pauseDispatcher()
//        //livedata testing
//        val latitudeitem =saveReminderViewModel.latitude.getOrAwaitValue()
//        val longitudeitem = saveReminderViewModel.longitude.getOrAwaitValue()
//        val reminderTitleitem = saveReminderViewModel.reminderTitle.getOrAwaitValue()
//        val reminderDescriptionitem = saveReminderViewModel.reminderDescription.getOrAwaitValue()
//        // Then the new task event is triggered
//        mainCoroutineRule.resumeDispatcher()
//
//        assertThat(latitudeitem, `is`(reminderItem.latitude))




}
