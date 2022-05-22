package com.lifegate.app.ui.fragments.choose

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ca.allanwang.kau.utils.visible
import com.lifegate.app.databinding.ChooseLocationFragmentBinding
import com.lifegate.app.utils.hideProgress
import com.lifegate.app.utils.safeNavigate
import com.lifegate.app.utils.showProgress
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ChooseLocationFragment : Fragment(), KodeinAware {

    companion object {
        fun newInstance() = ChooseLocationFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: ChooseLocationViewModel
    private lateinit var navController: NavController
    private lateinit var binding: ChooseLocationFragmentBinding
    private val factory: ChooseLocationViewModelFactory by instance()
    private val requestcode = 100
    private var currentLocation: Location? = null
    var locationManager: LocationManager?=null
    var locationByGps : Location?=null
    var locationByNetwork : Location?=null
    var latitude : Double?=0.0
    var longitude : Double?=0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(ChooseLocationViewModel::class.java)
        binding = ChooseLocationFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        //viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        isLocationPermissionGranted()
        binding.item.setOnClickListener {

            getCurrentLocation()

//            view?.context?.toast("Abu Al Hasaniya City is selected for demo purpose")
//            viewModel.saveUserSelectedCity("65569")
//            goToNextFrag()
        }



    }

    private fun goToNextFrag() {
        val action = ChooseLocationFragmentDirections.actionChooseLocationFragmentToOnBoardFragment()
        navController.safeNavigate(action)
    }

    private fun getCurrentLocation(){

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
//------------------------------------------------------//
        val hasNetwork = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                try {
                    binding?.loadingOverlay.visibility = View.GONE
                    locationByGps= location
                    currentLocation = locationByGps
                    latitude = currentLocation?.latitude
                    longitude = currentLocation?.longitude
                    Log.d("Location",latitude?.toString()!!)
                    viewModel.saveUserCurrentLatitude(latitude?.toString())
                    viewModel.saveUserCurrentLongitude(longitude?.toString())
                    if(locationManager !=null)
                        locationManager?.removeUpdates(this)
                    goToNextFrag()
                }catch (e:Exception){

                }

            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
//------------------------------------------------------//
        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByNetwork= location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        if (isLocationPermissionGranted()){
            if (hasGps!!) {
                binding?.loadingOverlay.visibility = View.VISIBLE
                if (checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0F,
                    gpsLocationListener
                )
            } else{
                buildAlertMessageNoGps()
            }
//------------------------------------------------------//
            if (hasNetwork!!) {
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0F,
                    networkLocationListener
                )
            }


            val lastKnownLocationByGps =
                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }
//------------------------------------------------------//
            val lastKnownLocationByNetwork =
                locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }
//------------------------------------------------------//
            if (locationByGps != null && locationByNetwork != null) {
//                hideProgress()
                if (locationByGps?.accuracy!! > locationByNetwork!!.accuracy) {
                    currentLocation = locationByGps
                    latitude = currentLocation?.latitude
                    longitude = currentLocation?.longitude
                    Log.d("Location",latitude?.toString()!!)
                    viewModel.saveUserCurrentLatitude(latitude?.toString())
                    viewModel.saveUserCurrentLongitude(longitude?.toString())
                    goToNextFrag()
                    // use latitude and longitude as per your need
                } else {
                    currentLocation = locationByNetwork
                    latitude = currentLocation?.latitude
                    longitude = currentLocation?.longitude
                    Log.d("Location",latitude?.toString()!!)
                    viewModel.saveUserCurrentLatitude(latitude?.toString())
                    viewModel.saveUserCurrentLongitude(longitude?.toString())
                    goToNextFrag()
                    // use latitude and longitude as per your need
                }
            }else{
//                getCurrentLocation()
            }

        }else{

        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) {

            requestMultiplePermissions.launch(
                arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,  Manifest.permission.ACCESS_FINE_LOCATION))
            return false
        } else {
            return true
        }

    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
            }
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            } else {
            }
        }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestcode -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val hasGps = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    if (!hasGps!!){
                        buildAlertMessageNoGps()
                    }
//                    getCurrentLocation()
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                        requireContext(),
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}