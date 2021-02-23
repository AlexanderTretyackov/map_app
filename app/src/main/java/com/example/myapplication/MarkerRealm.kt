package com.example.myapplication

import com.google.android.gms.maps.model.LatLng
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId


open class MarkerRealm : RealmObject() {
    @PrimaryKey
    var _id : ObjectId = ObjectId()
    open var longitude: Double = 0.0
    open var latitude: Double = 0.0
    open var photosUri: RealmList<String> = RealmList()
//    public open var latitude: Double = 0.0
//    public open var longitude: Double = 0.0
}