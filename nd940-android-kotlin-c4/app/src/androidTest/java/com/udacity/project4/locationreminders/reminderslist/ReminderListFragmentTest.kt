package com.udacity.project4.locationreminders.reminderslist

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.util.MainCoroutineRuleAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    test the navigation of the fragments.

    private val reminder1 = ReminderDTO("Title1", "Description1", "location1", 0.0, 0.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "location2", 1.0, 1.0)
    private  var remindersDataSource= FakeDataSource()
    private lateinit var ReminderListViewModel:RemindersListViewModel
    private val errormessage="No Data"


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRuleAndroidTest()

    //need to create a viewmodel as the fragment will need it to get info and display it

    @Before
    fun init() {
        ReminderListViewModel = RemindersListViewModel(
            getApplicationContext(), remindersDataSource as ReminderDataSource )

        stopKoin()
        val myModule = module {
            single {
                ReminderListViewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }

//navigation test
    @Test
    fun buttonclicked_navigatetosavereminder() = mainCoroutineRule.runBlockingTest{
        // GIVEN - On the home screen
    val navController = mock(NavController::class.java)
    navController.setGraph(R.navigation.nav_graph)
    val scenario = launchFragmentInContainer(themeResId = R.style.Theme_AppCompat) {
        ReminderListFragment().also { fragment ->
            fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    Navigation.setViewNavController(fragment.requireView(), navController)
                }

        // WHEN - Click on the "+" button
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder())
        }}}}

    //test ui item
    @Test
    fun confirmUIDisplays_givenreminders() = mainCoroutineRule.runBlockingTest{

        //given data and  viewmodel and set values to the remindersList.value , viewmodel needs a datasource :(

        remindersDataSource.saveReminder(reminder1)
        remindersDataSource.saveReminder(reminder2)

        // WHEN - Details fragment launched to display task

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // check data is shown
        onView(withText(reminder1.title)).check(matches(isDisplayed()))
    }

            @Test
    fun errorsnackbar_onNoData() = mainCoroutineRule.runBlockingTest{


        // WHEN - Details fragment launched to display task
        remindersDataSource.deleteAllReminders()
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        // check data is shown
        onView(withText(errormessage)).check(matches(isDisplayed()))
    }}

