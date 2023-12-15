package com.geryardiansyah.aplikasimap

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var searchView: SearchView
    private var previousMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = searchView.query.toString()
                var addressList: List<Address>? = null
                if (location != null && location.isNotBlank()) {
                    val geocoder = Geocoder(this@MainActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    if (addressList != null && addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)

                        previousMarker?.remove()
                        val newMarker = mGoogleMap?.addMarker(MarkerOptions().position(latLng).title(location))
                        previousMarker = newMarker
                        zoomInMap(latLng)
                    } else {
                        showPlaceNotFoundNotification()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val mapOptionButton: ImageButton = findViewById(R.id.mapOptionsButton)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            menuItem -> changeMap(menuItem.itemId)
                true
            }

            mapOptionButton.setOnClickListener {
                popupMenu.show()
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }

    private fun zoomInMap(latLng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    private fun showPlaceNotFoundNotification() {
        Toast.makeText(this@MainActivity, "Tempat tidak ditemukan", Toast.LENGTH_SHORT).show()
    }

    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.normal_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }
}