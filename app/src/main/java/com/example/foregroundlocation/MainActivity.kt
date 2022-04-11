package com.example.foregroundlocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var address:List<Address>
    private var geocoder:Geocoder?=null
 private lateinit var  text_location:TextView
    private lateinit var button_location:Button
    private lateinit var mFusedLocationProvider: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null
    private var locationCallback: LocationCallback? = null
    private val locationRequestCode = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_location=findViewById(R.id.button_location)
        text_location=findViewById(R.id.text_location)
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = (3 * 1000).toLong()
        locationRequest?.fastestInterval = (5 * 1000).toLong()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                if (p0.locations == null) {
                    return
                }
                for (location in p0.locations) {
                    if (location != null) {
                        mCurrentLocation = location
                        if (mFusedLocationProvider != null) {
                            mFusedLocationProvider?.removeLocationUpdates(locationCallback!!)
                        }
                    }
                }
            }
        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//
//        }
//        else
//        {
//            requestLocation()
//        }
        requestLocation()

            button_location.setOnClickListener {
                var location = getCurrentLocation()
                geocoder = Geocoder(this, Locale.getDefault())
                address = geocoder!!.getFromLocation(location!!.latitude,location!!.longitude, 1) as ArrayList<Address>
//            text_location.text = location?.latitude.toString() + " " + location?.longitude.toString()
                text_location.text =address.get(0).getAddressLine(0)
            }


    }
  private fun requestLocation(){
      if (ActivityCompat.checkSelfPermission(
              this,
              Manifest.permission.ACCESS_FINE_LOCATION
          ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
              this,
              Manifest.permission.ACCESS_COARSE_LOCATION
          ) != PackageManager.PERMISSION_GRANTED
      ) {
          ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
              locationRequestCode)
          return
      }else{
          mFusedLocationProvider.lastLocation.addOnSuccessListener(this) { location ->
              if (location != null) {
                  mCurrentLocation = location
              } else {
                  mFusedLocationProvider.requestLocationUpdates(locationRequest!!, locationCallback!!,
                      Looper.myLooper()!! )
              }
          }
      }

  }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation()

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getCurrentLocation(): Location? {
        return mCurrentLocation ?: null
    }
}