package com.example.medihelper.localdatabase.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "person_id")
    val personID: Int = 0,

    @ColumnInfo(name = "person_remote_id")
    var personRemoteID: Long? = null,

    @ColumnInfo(name = "person_name")
    var personName: String,

    @ColumnInfo(name = "person_color_res_id")
    var personColorResID: Int,

    @ColumnInfo(name = "main_person")
    var mainPerson: Boolean = false,

    @ColumnInfo(name = "connection_key")
    var connectionKey: String? = null,

    @ColumnInfo(name = "synchronized_with_server")
    var synchronizedWithServer: Boolean = false
)