package com.maruchin.medihelper.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.google.firebase.firestore.CollectionReference
import com.maruchin.medihelper.data.framework.FirestoreEntityRepo
import com.maruchin.medihelper.data.framework.getDocumenstLive
import com.maruchin.medihelper.data.mappers.MedicinePlanMapper
import com.maruchin.medihelper.data.utils.AppFirebase
import com.maruchin.medihelper.domain.entities.Plan
import com.maruchin.medihelper.domain.repositories.PlanRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PlanRepoImpl(
    private val appFirebase: AppFirebase,
    private val mapper: MedicinePlanMapper
) : FirestoreEntityRepo<Plan>(
    collectionRef = appFirebase.plans,
    entityMapper = mapper
), PlanRepo {

    private val collectionRef: CollectionReference
        get() = appFirebase.plans

    override suspend fun deleteListById(ids: List<String>) = withContext(Dispatchers.IO) {
        appFirebase.runBatch { batch ->
            ids.forEach { id ->
                val docRef = collectionRef.document(id)
                batch.delete(docRef)
            }
        }
        return@withContext
    }

    override suspend fun getListByMedicine(medicineId: String): List<Plan> = withContext(Dispatchers.IO) {
        val docsQuery = collectionRef.whereEqualTo("medicineId", medicineId).get().await()
        return@withContext getEntitiesFromQuery(docsQuery)
    }

    override suspend fun getListByProfile(profileId: String): List<Plan> = withContext(Dispatchers.IO) {
        val docsQuery = collectionRef.whereEqualTo("profileId", profileId).get().await()
        return@withContext getEntitiesFromQuery(docsQuery)
    }

    override suspend fun getListByType(type: Plan.Type): List<Plan> = withContext(Dispatchers.IO) {
        val docsQuery = collectionRef.whereEqualTo("planType", type.toString()).get().await()
        return@withContext getEntitiesFromQuery(docsQuery)
    }

    override suspend fun getListLiveByProfile(profileId: String): LiveData<List<Plan>> =
        withContext(Dispatchers.IO) {
            val docsLive = collectionRef.whereEqualTo("profileId", profileId).getDocumenstLive()
            return@withContext Transformations.switchMap(docsLive) { snapshotList ->
                liveData {
                    val value = snapshotList.map {
                        mapper.mapToEntity(it.id, it.data!!)
                    }
                    emit(value)
                }
            }
        }
}