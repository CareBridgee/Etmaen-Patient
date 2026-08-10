package com.carenest.designsystem.components.textfield


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable

@Composable
fun CustomTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    title: String? = null,
    tipText: String? = null,
    titleColor: Color = Theme.colors.primaryFont,
    textColor: Color = Theme.colors.primaryFont,
    borderColor: Color = Theme.colors.onPrimary,
    containerColor: Color = Theme.colors.surfaceVariant,
    onFocusBorderColor: Color = Theme.colors.primary,
    errorBorderColor: Color = Theme.colors.error,
    errorMessage: String? = null,
    errorIcon: Painter? = null,
    isError: Boolean = false,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
    leadingIconColor: Color = Theme.colors.primaryFont.copy(alpha = 0.7f),
    trailingIconColor: Color = Theme.colors.primaryFont.copy(alpha = 0.7f),
    onClickLeadingIcon: (() -> Unit)? = null,
    onClickTrailingIcon: (() -> Unit)? = null,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    fieldHeight: Dp = 56.dp,
    fieldVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    shape: Shape = RoundedCornerShape(14.dp),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(Theme.colors.primary),
    focusRequester: FocusRequester? = null,
    reserveErrorSpace: Boolean = false,
    errorSpaceHeight: Dp = 20.dp,
    errorMaxLines: Int = 2,
    inputFormatter: ((previousValue: String, newValue: String) -> String)? = null,
) {
    var isFocused: Boolean by remember { mutableStateOf(false) }
    var formattedFieldValue by remember {
        mutableStateOf(TextFieldValue(text, selection = TextRange(text.length)))
    }
    val internalFocusRequester = remember { FocusRequester() }
    val resolvedFocusRequester = focusRequester ?: internalFocusRequester
    val fieldInteractionSource = remember { MutableInteractionSource() }
    val currentBorderColor = when {
        isError -> errorBorderColor
        isFocused -> onFocusBorderColor
        else -> borderColor
    }

    LaunchedEffect(text) {
        if (inputFormatter != null && formattedFieldValue.text != text) {
            formattedFieldValue = TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            title?.let {
                BasicText(
                    text = it,
                    style = Theme.typography.body.medium.copy(
                        color = titleColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                )
            }
            tipText?.let {
                BasicText(
                    text = it,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.hint
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(fieldHeight)
                .clip(shape)
                .background(containerColor)
                .border(
                    width = 1.dp,
                    shape = shape,
                    color = currentBorderColor
                )
                .clickable(
                    enabled = enabled && !readOnly,
                    interactionSource = fieldInteractionSource,
                    indication = null,
                    onClick = resolvedFocusRequester::requestFocus,
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = fieldVerticalAlignment,
        ) {
            leadingIcon?.let {
                Image(
                    painter = it,
                    colorFilter = ColorFilter.tint(color = leadingIconColor),
                    contentDescription = stringResource(com.carenest.designsystem.R.string.common_leading_icon),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(22.dp)
                        .noRippleClickable(onClick = { onClickLeadingIcon?.invoke() }),
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isBlank()) {
                    hint?.let {
                        BasicText(
                            text = it,
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.hint.copy(alpha = 0.8f),
                                fontSize = 16.sp
                            )
                        )
                    }
                }
                val inputModifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(resolvedFocusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    }
                val inputTextStyle = Theme.typography.body.medium.copy(
                    color = if (!enabled) Theme.colors.onDisable else textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                if (inputFormatter == null) {
                    BasicTextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = inputModifier,
                        textStyle = inputTextStyle,
                        enabled = enabled,
                        readOnly = readOnly,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        minLines = minLines,
                        visualTransformation = visualTransformation,
                        onTextLayout = onTextLayout,
                        interactionSource = interactionSource,
                        cursorBrush = cursorBrush,
                    )
                } else {
                    BasicTextField(
                        value = formattedFieldValue,
                        onValueChange = { incomingValue ->
                            if (incomingValue.text == formattedFieldValue.text) {
                                formattedFieldValue = incomingValue
                            } else {
                                val formattedText = inputFormatter(
                                    formattedFieldValue.text,
                                    incomingValue.text
                                )
                                formattedFieldValue = if (formattedText == incomingValue.text) {
                                    incomingValue
                                } else {
                                    TextFieldValue(
                                        text = formattedText,
                                        selection = TextRange(formattedText.length)
                                    )
                                }
                                onTextChange(formattedText)
                            }
                        },
                        modifier = inputModifier,
                        textStyle = inputTextStyle,
                        enabled = enabled,
                        readOnly = readOnly,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        minLines = minLines,
                        visualTransformation = visualTransformation,
                        onTextLayout = onTextLayout,
                        interactionSource = interactionSource,
                        cursorBrush = cursorBrush,
                    )
                }
            }
            trailingIcon?.let {
                Image(
                    painter = it,
                    colorFilter = ColorFilter.tint(color = trailingIconColor),
                    contentDescription = stringResource(com.carenest.designsystem.R.string.common_trailing_icon),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(22.dp)
                        .noRippleClickable(onClick = { onClickTrailingIcon?.invoke() }),
                )
            }
        }
        if (isError || reserveErrorSpace) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(errorSpaceHeight),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                if (isError) errorIcon?.let {
                    Image(
                        painter = it,
                        colorFilter = ColorFilter.tint(color = Theme.colors.error),
                        contentDescription = stringResource(com.carenest.designsystem.R.string.common_error_icon),
                        modifier = Modifier
                            .padding(start = Theme.spacing.small)
                            .size(Theme.size.iconSmall)
                    )
                }
                if (isError) errorMessage?.let {
                    BasicText(
                        text = it,
                        modifier = Modifier.weight(1f),
                        style = Theme.typography.body.small.copy(color = Theme.colors.error),
                        maxLines = errorMaxLines,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
