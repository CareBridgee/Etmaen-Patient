package com.carenest.data.source.local.profile

import com.carenest.domain.model.profile.ProfileLocalDraft

interface LocalProfileDraftDataSource {
    suspend fun load(userId: String): ProfileLocalDraft
    suspend fun save(userId: String, draft: ProfileLocalDraft)
    suspend fun markOnboardingHandled(userId: String)
}
