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
import io.realm.RealmList
import org.bson.types.ObjectId
import java.io.Console


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val PHOTOS_RESULT = 123
    private lateinit var mMap: GoogleMap
    var markersList = ArrayList<MarkerRealm>()
    val LIST_URI_KEY = "LIST_URI_KEY"
    lateinit var realm: Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restoreData()

        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

//    override fun onSaveInstanceState(outState: Bundle) {
////        outState?.run {
////            putString("KEY", textView.text.toString())
////        }
//        outState.putSerializable("markers", markersList);
//        super.onSaveInstanceState(outState)
//    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        //markersList = savedInstanceState?.getSerializable("markersList") as ArrayList<MarkerRealm>
        restoreData()
    }

    fun restoreData(){
        Realm.init(this);
        val config = RealmConfiguration.Builder()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build()
        realm = Realm.getInstance(config)
        val markersQuery = realm.where(MarkerRealm::class.java).findAll()
        for(m in markersQuery){
            markersList.add(m)
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
            mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)))
        }
        mMap.setOnMarkerClickListener { markerClicked ->
            val intent = Intent(this, PhotosActivity ::class.java)
            val markerFound = markersList.find{mr -> mr.latitude == markerClicked.position.latitude &&
                    mr.longitude == markerClicked.position.longitude}
            intent.putExtra("MARKER_ID", markerFound?._id.toString())
            var photosUri : ArrayList<Uri> = ArrayList()
            markerFound?.photosUri?.forEach{
                photosUri.add(Uri.parse(it))
            }
            intent.putParcelableArrayListExtra(LIST_URI_KEY, photosUri)
            startActivity(intent);
            true
        }

        mMap.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng :LatLng) {
                val location = LatLng(latlng.latitude,latlng.longitude)
                mMap.addMarker(MarkerOptions().position(location))
                //создаем новый маркер для хранения
                val markerRealm = MarkerRealm()
                markerRealm.latitude = location.latitude
                markerRealm.longitude = location.longitude

                //добавляем новый маркер в БД
                realm.executeTransaction { r : Realm ->
                    r.insert(markerRealm)
                }
                //Добавляем в список маркеров новый
                markersList.add(markerRealm)
            }
        })
    }
}