package com.example.medihelper.device.di

import com.example.medihelper.device.deviceapi.CalendarApiImpl
import com.example.medihelper.device.deviceapi.CameraApiImpl
import com.example.medihelper.domain.deviceapi.CalendarApi
import com.example.medihelper.domain.deviceapi.CameraApi
import org.koin.dsl.module

val deviceApiModule = module {
    single {
        CalendarApiImpl() as CalendarApi
    }
    single {
        CameraApiImpl(
            context = get(),
            cameraPermission = get()
        ) as CameraApi
    }
}