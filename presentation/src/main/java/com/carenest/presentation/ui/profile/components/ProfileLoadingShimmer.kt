package com.carenest.presentation.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.shimmer.shimmerEffect
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.profile.ProfileStep

/**
 * Full-screen skeleton used while a Complete Profile step is loading remote data.
 * Submission loading remains on the action button so entered values stay visible.
 */
@Composable
fun ProfileLoadingShimmer(step: ProfileStep) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            cornerRadius = 0.dp
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ProgressShimmer()

            when (step) {
                ProfileStep.Welcome -> WelcomeShimmer()
                ProfileStep.BasicHealthInfo -> BasicHealthShimmer()
                ProfileStep.MedicalConditions -> CatalogSelectionShimmer(sectionCount = 2)
                ProfileStep.Allergies -> CatalogSelectionShimmer(sectionCount = 3)
                ProfileStep.CurrentMedications -> MedicationsShimmer()
                ProfileStep.MedicalHistory -> MedicalHistoryShimmer()
                ProfileStep.MobilityStatus -> MobilityShimmer()
                ProfileStep.EmergencyContact -> EmergencyContactShimmer()
                ProfileStep.FinalStep -> FinalStepShimmer()
            }
        }

        NavigationShimmer(singleButton = step == ProfileStep.Welcome)
    }
}

@Composable
private fun ProgressShimmer() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerBox(Modifier.size(width = 92.dp, height = 14.dp))
            ShimmerBox(Modifier.size(width = 42.dp, height = 14.dp))
        }
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            cornerRadius = 8.dp
        )
    }
}

@Composable
private fun WelcomeShimmer() {
    ShimmerBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        cornerRadius = 28.dp
    )
    TextHeaderShimmer()
    ShimmerBox(Modifier.fillMaxWidth().height(84.dp), 18.dp)
}

@Composable
private fun BasicHealthShimmer() {
    ShimmerBox(Modifier.fillMaxWidth().height(118.dp), 22.dp)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ShimmerBox(Modifier.weight(1f).height(132.dp), 20.dp)
        ShimmerBox(Modifier.weight(1f).height(132.dp), 20.dp)
    }
    ShimmerBox(Modifier.fillMaxWidth().height(108.dp), 20.dp)
}

@Composable
private fun CatalogSelectionShimmer(sectionCount: Int) {
    TextHeaderShimmer()
    ShimmerBox(Modifier.fillMaxWidth().height(82.dp), 16.dp)
    repeat(sectionCount) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(Modifier.size(width = 150.dp, height = 20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBox(Modifier.size(width = 92.dp, height = 42.dp), 21.dp)
                ShimmerBox(Modifier.size(width = 112.dp, height = 42.dp), 21.dp)
                ShimmerBox(Modifier.size(width = 82.dp, height = 42.dp), 21.dp)
            }
        }
    }
    ShimmerBox(Modifier.fillMaxWidth().height(96.dp), 16.dp)
}

@Composable
private fun MedicationsShimmer() {
    TextHeaderShimmer()
    ShimmerBox(Modifier.fillMaxWidth().height(84.dp), 16.dp)
    ShimmerBox(Modifier.fillMaxWidth().height(92.dp), 16.dp)
    ShimmerBox(Modifier.fillMaxWidth().height(48.dp), 14.dp)
}

@Composable
private fun MedicalHistoryShimmer() {
    TextHeaderShimmer()
    repeat(2) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerBox(Modifier.size(width = 180.dp, height = 16.dp))
            ShimmerBox(Modifier.fillMaxWidth().height(118.dp), 16.dp)
        }
    }
}

@Composable
private fun MobilityShimmer() {
    TextHeaderShimmer()
    repeat(5) {
        ShimmerBox(Modifier.fillMaxWidth().height(66.dp), 16.dp)
    }
    ShimmerBox(Modifier.fillMaxWidth().height(96.dp), 16.dp)
}

@Composable
private fun EmergencyContactShimmer() {
    TextHeaderShimmer()
    repeat(3) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerBox(Modifier.size(width = 124.dp, height = 15.dp))
            ShimmerBox(Modifier.fillMaxWidth().height(56.dp), 14.dp)
        }
    }
}

@Composable
private fun FinalStepShimmer() {
    Spacer(Modifier.height(32.dp))
    ShimmerBox(Modifier.fillMaxWidth().height(240.dp), 28.dp)
    TextHeaderShimmer()
}

@Composable
private fun TextHeaderShimmer() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ShimmerBox(Modifier.fillMaxWidth(0.72f).height(26.dp))
        ShimmerBox(Modifier.fillMaxWidth().height(16.dp))
        ShimmerBox(Modifier.fillMaxWidth(0.84f).height(16.dp))
    }
}

@Composable
private fun NavigationShimmer(singleButton: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.surface)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!singleButton) {
            ShimmerBox(Modifier.weight(1f).height(52.dp), 14.dp)
        }
        ShimmerBox(Modifier.weight(1f).height(52.dp), 14.dp)
    }
}

@Composable
private fun ShimmerBox(
    modifier: Modifier,
    cornerRadius: Dp = 10.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .shimmerEffect()
    )
}
