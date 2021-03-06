package com.maruchin.medihelper.domain.utils

import com.google.common.truth.Truth
import com.maruchin.medihelper.domain.entities.AppExpireDate
import org.junit.Before
import org.junit.Test

class MedicineValidatorTest {

    private lateinit var validator: MedicineValidator

    @Before
    fun before() {
        validator = MedicineValidator()
    }

    @Test
    fun validate_AllCorrect() {
        val params = MedicineValidator.Params(
            name = "Hitaxa",
            unit = "tabletki",
            packageSize = 100f,
            currState = 80f
        )

        val errors = validator.validate(params)

        Truth.assertThat(errors.noErrors).isTrue()
        arrayOf(
            errors.emptyName,
            errors.emptyUnit,
            errors.currStateBiggerThanPackageSize
        ).forEach {
            Truth.assertThat(it).isFalse()
        }
    }

    @Test
    fun validate_EmptyName() {
        val params = MedicineValidator.Params(
            name = null,
            unit = "tabletki",
            packageSize = 100f,
            currState = 80f
        )

        val errors = validator.validate(params)

        Truth.assertThat(errors.noErrors).isFalse()
        Truth.assertThat(errors.emptyName).isTrue()
        arrayOf(
            errors.emptyUnit,
            errors.currStateBiggerThanPackageSize
        ).forEach {
            Truth.assertThat(it).isFalse()
        }
    }

    @Test
    fun validate_EmptyUnit() {
        val params = MedicineValidator.Params(
            name = "Hitaxa",
            unit = null,
            packageSize = 100f,
            currState = 80f
        )

        val errors = validator.validate(params)

        Truth.assertThat(errors.noErrors).isFalse()
        Truth.assertThat(errors.emptyUnit).isTrue()
        arrayOf(
            errors.emptyName,
            errors.currStateBiggerThanPackageSize
        ).forEach {
            Truth.assertThat(it).isFalse()
        }
    }

    @Test
    fun validate_CurrStateBiggerThanPackageSize() {
        val params = MedicineValidator.Params(
            name = "Hitaxa",
            unit = "tabletki",
            packageSize = 100f,
            currState = 101f
        )

        val errors = validator.validate(params)

        Truth.assertThat(errors.noErrors).isFalse()
        Truth.assertThat(errors.currStateBiggerThanPackageSize).isTrue()
        arrayOf(
            errors.emptyName,
            errors.emptyUnit
        ).forEach {
            Truth.assertThat(it).isFalse()
        }
    }
}