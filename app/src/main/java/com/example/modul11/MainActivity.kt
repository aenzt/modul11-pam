package com.example.modul11

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.modul11.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationProviderClient: FusedLocationProviderClient
    lateinit var addresses: List<Address>
    lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        geocoder = Geocoder(this, Locale.getDefault())

        binding.btnFind.setOnClickListener {
            location
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "Izin lokasi tidak diaktifkan!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                location
            }
        }
    }

    private val location: Unit
        get() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // get Permission
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 10
                )
            } else {
                // get Location
                locationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        binding.latitude.text =
                            location.latitude.toString()
                        binding.longitude.text =
                            location.longitude.toString()
                        binding.altitude.text =
                            location.altitude.toString()
                        binding.akurasi.text =
                            location.accuracy.toString() + "%"
                        try {
                            addresses =
                                geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                                ) as List<Address>
                            binding.alamat.text = addresses[0].getAddressLine(0)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext, "Lokasi tidak aktif!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener { e: Exception ->
                    Toast.makeText(
                        applicationContext, e.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
}