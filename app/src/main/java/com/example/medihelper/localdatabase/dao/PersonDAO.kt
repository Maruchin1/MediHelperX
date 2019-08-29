package com.example.medihelper.localdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.medihelper.localdatabase.entities.PersonEntity
import com.example.medihelper.localdatabase.pojos.PersonItem

@Dao
interface PersonDAO {

    @Insert
    fun insert(person: PersonEntity): Long

    @Update
    fun update(person: PersonEntity)

    @Query("DELETE FROM persons WHERE person_id = :personID")
    fun delete(personID: Int)

    @Query("SELECT * FROM persons")
    fun getItemListLive(): LiveData<List<PersonItem>>

    @Query("SELECT * FROM persons WHERE person_id = :personID")
    fun getItemLive(personID: Int): LiveData<PersonItem>

    @Query("SELECT COUNT(*) FROM persons")
    fun getCount(): Int
}