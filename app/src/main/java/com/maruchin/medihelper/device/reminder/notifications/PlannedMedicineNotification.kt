package com.maruchin.medihelper.device.reminder.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.maruchin.medihelper.R
import com.maruchin.medihelper.domain.model.PlannedMedicineNotifData
import com.maruchin.medihelper.presentation.LauncherActivity

class PlannedMedicineNotification(
    private val context: Context,
    private val data: PlannedMedicineNotifData
) : BaseNotification(context) {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE_MEDICINE_TAKEN = 1
        private const val REQUEST_CODE_OPEN_APP = 2

        fun cancel(plannedMedicineId: String, context: Context) {
            val manager = NotificationManagerCompat.from(context)
            manager.cancel(
                plannedMedicineId,
                NOTIFICATION_ID
            )
        }
    }

    override val channelId: String
        get() = "planned-medicine-notification-channel-id"
    override val channelName: String
        get() = "planned-medicine-notification-channel-name"

    override fun launch() {
        val notification = buildNotification()
        val manager = NotificationManagerCompat.from(context)
        manager.notify(data.plannedMedicineId,
            NOTIFICATION_ID, notification)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getTitleText())
            .setContentText(getMessageText())
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.baseline_check_24,
                "Przyjęty",
                getMedicineTakenPendingIntent(data.plannedMedicineId)
            )
            .addAction(
                R.drawable.ic_pill_black_24dp,
                "Szczegóły",
                getOpenAppPendingIntent()
            )
            .build()
    }

    private fun getTitleText(): String {
        return "${data.profileName} - pora przyjąć lek ${data.medicineName}"
    }

    private fun getMessageText(): String {
        return "${data.plannedTime.formatString} - ${data.doseSize} ${data.medicineUnit}"
    }

    private fun getMedicineTakenPendingIntent(plannedMedicineId: String): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action =
                NotificationActionReceiver.ACTION_SET_MEDICINE_TAKEN
            data = plannedMedicineId.toUri()
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_MEDICINE_TAKEN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getOpenAppPendingIntent(): PendingIntent {
        val intent = Intent(context, LauncherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            context,
            REQUEST_CODE_OPEN_APP,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}