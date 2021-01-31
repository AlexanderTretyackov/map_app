package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var markersList = ArrayList<LatLng>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
//        outState?.run {
//            putString("KEY", textView.text.toString())
//        }
        outState.putParcelableArrayList("markersList", markersList);
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.
        getParcelableArrayList<LatLng>("markersList")?.
        forEach{
            markersList.add(it)
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        markersList.forEach{
            mMap.addMarker(MarkerOptions().position(it))
        }
        mMap.setOnMarkerClickListener { marker ->
            val intent = Intent(this, PhotosActivity ::class.java)
            //intent.putExtra("key", value)
            startActivity(intent)
            true
        }
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng :LatLng) {
                // Clears the previously touched position
                //mMap.clear();
                // Animating to the touched position
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));

                val location = LatLng(latlng.latitude,latlng.longitude)
                mMap.addMarker(MarkerOptions().position(location))
                //Добавляем в список маркеров новый
                markersList.add(location)
            }
        })
    }
}