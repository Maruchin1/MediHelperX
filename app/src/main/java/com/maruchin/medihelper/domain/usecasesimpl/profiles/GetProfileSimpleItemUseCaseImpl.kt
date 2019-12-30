package com.maruchin.medihelper.domain.usecasesimpl.profiles

import com.maruchin.medihelper.domain.model.ProfileSimpleItem
import com.maruchin.medihelper.domain.repositories.ProfileRepo
import com.maruchin.medihelper.domain.usecases.profile.GetProfileSimpleItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetProfileSimpleItemUseCaseImpl(
    private val profileRepo: ProfileRepo
) : GetProfileSimpleItemUseCase {

    override suspend fun execute(profileId: String): ProfileSimpleItem? = withContext(Dispatchers.Default) {
        return@withContext profileRepo.getById(profileId)?.let { profile ->
            ProfileSimpleItem(profile)
        }
    }
}