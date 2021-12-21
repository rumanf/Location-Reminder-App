package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Intent
import org.koin.android.BuildConfig
import android.provider.Settings
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.android.inject



class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    //override val _viewModel: SaveReminderViewModel by activityViewModels()

    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map1: GoogleMap
    private val TAG = "Select Location"
    var reminderSelectedLocationStr = ""
    var selectedPOI: PointOfInterest = PointOfInterest(LatLng(0.0, 0.0), "", "")
    var userlatitude = 0.0
    var userlongitude = 0.0
    var latitude = 0.0
    var longitude = 0.0
    private val REQUEST_LOCATION_PERMISSION = 1
    private val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val runningOnQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        // setDisplayHomeAsUpEnabled(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }



    override fun onMapReady(googleMap: GoogleMap) {


        map1 = googleMap
        //These coordinates represent the latitude and longitude of the Googleplex.

        enableMyLocation()
        setMapLongClick(map1)
        setPoiClick(map1)
        setMapStyle(map1)

        val zoomLevel = 15f
        val googleplex = LatLng(37.422131, -122.084801)
          map1.animateCamera(CameraUpdateFactory.newLatLngZoom(googleplex, zoomLevel))

    }

    private fun onLocationSelected() {
        _viewModel.reminderSelectedLocationStr.value = reminderSelectedLocationStr
        _viewModel.selectedPOI.value = selectedPOI
        _viewModel.latitude.value = latitude
        _viewModel.longitude.value = longitude
        _viewModel.navigationCommand.value = NavigationCommand.Back
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        when {
            (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) -> {
                map1.isMyLocationEnabled = true
            }
            (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )) -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
            else ->
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION)
        } }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        //  Change the map type based on the user's selection.
        R.id.normal_map -> {
            GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map1.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map1.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map1.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->

            latitude = poi.latLng.latitude
            longitude = poi.latLng.longitude
            reminderSelectedLocationStr = poi.name
            Thread.sleep(1000)
            onLocationSelected()
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Custom Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            )
            latitude = latLng.latitude
            longitude = latLng.longitude
            reminderSelectedLocationStr = "Custom Location"
            Thread.sleep(500)
            onLocationSelected()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {

                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    enableMyLocation()

                } else {

                    Snackbar.make(
                        binding.map,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }.show()
                }


            } } }
}
