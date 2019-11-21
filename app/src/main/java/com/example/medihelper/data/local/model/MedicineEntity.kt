package com.example.medihelper.data.local.model

import androidx.room.*
import com.example.medihelper.data.local.ImagesFiles
import com.example.medihelper.domain.entities.AppExpireDate
import com.example.medihelper.domain.entities.Medicine

@Entity(tableName = "medicines")
data class MedicineEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "medicine_id")
    val medicineId: Int,

    @ColumnInfo(name = "medicine_remote_id")
    var medicineRemoteId: Long?,

    @ColumnInfo(name = "medicine_name")
    var medicineName: String,

    @ColumnInfo(name = "medicine_unit")
    var medicineUnit: String,

    @ColumnInfo(name = "expire_date")
    var expireDate: AppExpireDate,

    @ColumnInfo(name = "package_size")
    var packageSize: Float?,

    @ColumnInfo(name = "curr_state")
    var currState: Float?,

    @ColumnInfo(name = "additional_info")
    var additionalInfo: String?,

    @ColumnInfo(name = "image_name")
    var imageName: String?,

    @ColumnInfo(name = "medicine_synchronized")
    var medicineSynchronized: Boolean
) {
    constructor(medicine: Medicine) : this(
        medicineId = 0,
        medicineRemoteId = null,
        medicineName = medicine.name,
        medicineUnit = medicine.unit,
        expireDate = medicine.expireDate,
        packageSize = medicine.packageSize,
        currState = medicine.currState,
        additionalInfo = medicine.additionalInfo,
        imageName = medicine.image?.name,
        medicineSynchronized = false
    )

    fun update(medicine: Medicine) {
        medicineName = medicine.name
        medicineUnit = medicine.unit
        expireDate = medicine.expireDate
        packageSize = medicine.packageSize
        currState = medicine.currState
        additionalInfo = medicine.additionalInfo
        imageName = medicine.image?.name
        medicineSynchronized = false
    }

    fun toMedicine(imagesFiles: ImagesFiles) = Medicine(
        medicineId = medicineId,
        name = medicineName,
        unit = medicineUnit,
        expireDate = expireDate,
        packageSize = packageSize,
        currState = currState,
        additionalInfo = additionalInfo,
        image = imageName?.let { imagesFiles.getImageFile(it) }
    )
}