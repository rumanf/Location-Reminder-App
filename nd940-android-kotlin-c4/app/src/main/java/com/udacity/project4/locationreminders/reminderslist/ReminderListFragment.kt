package com.udacity.project4.locationreminders.reminderslist

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import com.udacity.project4.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.provider.Settings
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.udacity.project4.locationreminders.RemindersActivity
import org.koin.android.BuildConfig


class ReminderListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminders, container, false
            )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }
    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        _viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        //use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
//                 logout implementation
                AuthUI.getInstance().signOut(requireContext())
                //go back to login page
                val intent = Intent(activity, AuthenticationActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }
}

//    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
//            android.os.Build.VERSION_CODES.Q
//
//    // check permissions for location/geofencing...
//    @TargetApi(29)
//    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
//        val foregroundLocationApproved = (
//                PackageManager.PERMISSION_GRANTED ==
//                        ActivityCompat.checkSelfPermission(requireContext(),
//                            Manifest.permission.ACCESS_FINE_LOCATION))
//        val backgroundPermissionApproved =
//            if (runningQOrLater) {
//                PackageManager.PERMISSION_GRANTED ==
//                        checkSelfPermission(
//                            requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                        )
//            } else {
//                true
//            }
//        return foregroundLocationApproved && backgroundPermissionApproved
//    }
//
//    /*
//     *  Requests ACCESS_FINE_LOCATION and (on Android 10+ (Q) ACCESS_BACKGROUND_LOCATION.
//     */
//    @TargetApi(29 )
//    private fun requestForegroundAndBackgroundLocationPermissions() {
//        if (foregroundAndBackgroundLocationPermissionApproved())
//            return
//
//        // Else request the permission
//        // this provides the result[LOCATION_PERMISSION_INDEX]
//        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
//
//        val resultCode = when {
//            runningQOrLater -> {
//                // this provides the result[BACKGROUND_LOCATION_PERMISSION_INDEX]
//                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
//            }
//            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
//        }
//        Log.d(TAG, "Request foreground only location permission")
//        requestPermissions(
//            permissionsArray,
//            resultCode
//        )
//    }
//
//    //check results for permissions
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        Log.d(TAG, "onRequestPermissionResult")
//
//        if (
//            grantResults.isEmpty() ||
//            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
//            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
//                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
//                    PackageManager.PERMISSION_DENIED))
//        {
//            Snackbar.make(
//                binding.refreshLayout,
//                R.string.permission_denied_explanation,
//                Snackbar.LENGTH_INDEFINITE
//            )
//                .setAction(R.string.settings) {
//                    startActivity(Intent().apply {
//                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    })
//                }.show()
//        }
//        //else {}
//
//
//    }
//
//    //CHECK DEVICE LOCATION IS TURNED ON
//
//    private fun checkDeviceLocationSettings(resolve:Boolean = true) {
//        val locationRequest = LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_LOW_POWER
//        }
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//        val settingsClient = LocationServices.getSettingsClient(requireActivity())
//        val locationSettingsResponseTask =
//            settingsClient.checkLocationSettings(builder.build())
//        locationSettingsResponseTask.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException && resolve){
//                try {
//                    exception.startResolutionForResult(requireActivity(),
//                        REQUEST_TURN_DEVICE_LOCATION_ON)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
//                }
//            } else {
//                Snackbar.make(
//                    binding.refreshLayout,
//                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
//                ).setAction(android.R.string.ok) {
//                    checkDeviceLocationSettings()
//                }.show()
//            }
//        }
//        locationSettingsResponseTask.addOnCompleteListener {
//            if ( it.isSuccessful ) {
//                // ADD TASK IF REQUIRED
//            }
//        }
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
//            checkDeviceLocationSettings(false)
//        }
//    }
//
//}
//private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
//private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
//private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
//private const val TAG = "ReminderList"
//private const val LOCATION_PERMISSION_INDEX = 0
//private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1