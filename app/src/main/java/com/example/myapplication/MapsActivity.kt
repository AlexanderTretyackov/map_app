package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import io.realm.RealmConfiguration
import org.bson.types.ObjectId
import java.io.Console


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val PHOTOS_RESULT = 123
    private lateinit var mMap: GoogleMap
    var markersList = ArrayList<LatLng>()
    val LIST_URI_KEY = "LIST_URI_KEY"
    var photosUri: ArrayList<ArrayList<Uri>> = ArrayList()
    lateinit var realm: Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Realm.init(this);
        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        realm = Realm.getInstance(config)
        val markersQuery = realm.where(MarkerRealm::class.java).findAll()
        for(m in markersQuery){
           markersList.add(LatLng(m.latitude, m.longitude))
        }


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
        outState.putSerializable("photosUri", photosUri);
        outState.putParcelableArrayList("markersList", markersList);
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        photosUri = savedInstanceState?.getSerializable("photosUri") as ArrayList<ArrayList<Uri>>
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
            val markerIndex = markersList.indexOf(marker.position)
            intent.putExtra("MARKER_INDEX", markerIndex)
            intent.putParcelableArrayListExtra(LIST_URI_KEY, photosUri[markerIndex])
            startActivityForResult(intent, PHOTOS_RESULT);
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
                val newMarker = mMap.addMarker(MarkerOptions().position(location))
                //Добавляем в список маркеров новый
                markersList.add(location)
                //Добавляем в список с uri фотографий новый список для нового маркера
                photosUri.add(ArrayList())
                //добавляем новый маркер в БД
                realm.executeTransaction { r : Realm ->
                    val markerRealm = MarkerRealm()
                    markerRealm.latitude = location.latitude
                    markerRealm.longitude = location.longitude
                    r.insert(markerRealm)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTOS_RESULT && resultCode == Activity.RESULT_OK) {
            val markerIndex = data?.getIntExtra("MARKER_INDEX", -1)
            if(markerIndex != null && markerIndex >= 0) {
                photosUri[markerIndex] = data?.getParcelableArrayListExtra<Uri>(LIST_URI_KEY) as ArrayList<Uri>
            }
        }
    }
}