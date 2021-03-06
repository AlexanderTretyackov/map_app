package com.example.myapplication

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_photos.*
import org.bson.types.ObjectId
import java.util.*


class PhotosActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE: Int = 101
    val REQUEST_IMAGE_CAPTURE = 1
    val LIST_URI_KEY = "LIST_URI_KEY"
    var imageUri: Uri? = null
    var markerId : String = ""
    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: PhotosAdapter
    private var listUri: ArrayList<Uri> = ArrayList()
    lateinit var realm: Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        Realm.init(this);
        val config = RealmConfiguration.Builder()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build()
        realm = Realm.getInstance(config)


        cameraButton.setOnClickListener {
            if (checkPersmission())
                takePicture()
            else
                requestPermission()
        }

        val extras = intent.extras
        listUri = extras?.getParcelableArrayList<Uri>(LIST_URI_KEY) as ArrayList<Uri>
        markerId = extras.getString("MARKER_ID")!!
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        adapter = PhotosAdapter(this, listUri)
        recyclerView.adapter = adapter
    }

    fun addPhotoUriToMarker(markerId:String, newPhotoUri:Uri){
        val markerObjectId = ObjectId(markerId)
        val markerFound = realm.where(MarkerRealm::class.java).equalTo("_id", markerObjectId).findFirst()
        if(markerFound != null)
        {
            realm.executeTransaction { r : Realm ->
                markerFound.photosUri.add(newPhotoUri.toString())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(LIST_URI_KEY, listUri);
        outState.putString("MARKER_ID", markerId);
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        listUri.clear()
        markerId = savedInstanceState?.getString("MARKER_ID")!!
        var savedListUri = savedInstanceState?.getParcelableArrayList<Uri>(LIST_URI_KEY) as ArrayList<Uri>
        for(uri in savedListUri){
            listUri.add(uri)
        }
        adapter.notifyDataSetChanged()
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA),
                PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        &&grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    Toast.makeText(this,"???? ???? ???????????????????????? ???????????????????? :(",Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {

            }
        }
    }

    private fun takePicture() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageUri?.let { listUri.add(it) }
            adapter.notifyItemInserted(listUri.size-1)
            addPhotoUriToMarker(markerId, imageUri!!)
        }
    }

}