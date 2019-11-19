package com.example.medihelper.utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medihelper.localdata.dao.PlannedMedicineDao
import com.example.medihelper.localdata.entity.PlannedMedicineEntity
import com.example.medihelper.service.DateTimeService
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class UpdateRemindersWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params),
    KoinComponent {
    private val TAG = "UpdateRemindersWorker"

    companion object {
        private const val SHARED_PREF_NAME = "reminders-pref"
        private const val KEY_REMINDERS_LIST = "key-reminders-list"
    }

    private val plannedMedicineDao: PlannedMedicineDao by inject()
    private val dateTimeService: DateTimeService by inject()
    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun doWork(): Result {
        val currDate = dateTimeService.getCurrDate()
        val plannedMedicinesForCurrDate = plannedMedicineDao.getEntityListByDate(currDate)

        cancelCurrReminders()
        setReminders(plannedMedicinesForCurrDate)
        savePlannedMedicinesIds(plannedMedicinesForCurrDate.map { it.plannedMedicineId })

        return Result.success()
    }

    private fun cancelCurrReminders() {
        getPlannedMedicinesIds().forEach { plannedMedicineId ->
            alarmManager.cancel(getReminderIntent(plannedMedicineId))
        }
    }

    private fun setReminders(list: List<PlannedMedicineEntity>) {
        val currTime = dateTimeService.getCurrTime()
        list.forEach { plannedMedicine ->
            if (plannedMedicine.plannedTime > currTime) {
                val calendar = Calendar.getInstance().apply {
                    set(
                        plannedMedicine.plannedDate.year,
                        plannedMedicine.plannedDate.month - 1,
                        plannedMedicine.plannedDate.day,
                        plannedMedicine.plannedTime.hour,
                        plannedMedicine.plannedTime.minute,
                        0
                    )
                }
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    getReminderIntent(plannedMedicine.plannedMedicineId)
                )
            }
        }
    }

    private fun getReminderIntent(plannedMedicineId: Int): PendingIntent {
        return Intent(context, ReminderReceiver::class.java).let { intent ->
            intent.putExtra(ReminderReceiver.EXTRA_PLANNED_MEDICINE_ID, plannedMedicineId)
            PendingIntent.getBroadcast(context, plannedMedicineId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun savePlannedMedicinesIds(list: List<Int>) {
        sharedPref.edit(true) {
            putStringSet(KEY_REMINDERS_LIST, list.map { it.toString() }.toSet())
        }
    }

    private fun getPlannedMedicinesIds(): List<Int> {
        return sharedPref.getStringSet(KEY_REMINDERS_LIST, null)?.map { it.toInt() } ?: emptyList()
    }
}