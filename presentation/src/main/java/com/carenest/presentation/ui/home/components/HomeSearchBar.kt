package com.carenest.presentation.ui.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.R as RD
import com.carenest.presentation.R
import androidx.compose.ui.res.stringResource

@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomTextField(
        text = query,
        onTextChange = onQueryChange,
        hint = stringResource(R.string.home_search_hint),
        leadingIcon = painterResource(id = RD.drawable.ic_search),
        leadingIconColor = Theme.colors.hint,
        containerColor = Theme.colors.surface,
        borderColor = Theme.colors.surfaceVariant,
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
