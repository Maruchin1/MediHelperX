package com.maruchin.medihelper.data.di

import com.maruchin.medihelper.data.repositories.*
import com.maruchin.medihelper.domain.repositories.*
import org.koin.dsl.module

val repositoryModule = module {
    single {
        UserRepoImpl(
            db = get()
        ) as UserRepo
    }
    single {
        MedicineRepoImpl(
            db = get(),
            auth = get(),
            sharedPref = get()
        ) as MedicineRepo
    }
    single {
        ProfileRepoImpl(
            db = get(),
            auth = get()
        ) as ProfileRepo
    }
    single {
        MedicinePlanRepoImpl(

        ) as MedicinePlanRepo
    }
    single {
        PlannedMedicineRepoImpl(

        ) as PlannedMedicineRepo
    }
}