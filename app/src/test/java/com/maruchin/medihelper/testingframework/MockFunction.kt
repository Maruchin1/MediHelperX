package com.maruchin.medihelper.testingframework

import org.mockito.Mockito

inline fun <reified T>mock(): T = Mockito.mock(T::class.java)

fun <T>anyObject(type: Class<T>): T = Mockito.any<T>(type)