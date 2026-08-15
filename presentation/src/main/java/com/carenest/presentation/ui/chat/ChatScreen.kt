package com.carenest.presentation.ui.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.core.util.dayKey
import com.carenest.presentation.core.util.dialPhoneNumber
import com.carenest.presentation.core.util.formatDateSeparator
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.ui.chat.components.ChatInputBar
import com.carenest.presentation.ui.chat.components.ChatTopBar
import com.carenest.presentation.ui.chat.components.DateSeparatorPill
import com.carenest.presentation.ui.chat.components.MessageBubble
import com.carenest.presentation.ui.tracking.components.CancelVisitConfirmationDialog
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    requestId: String,
    onNavigateBack: () -> Unit,
    showSnackbar: (String) -> Unit ,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(requestId) {
        viewModel.handleIntent(ChatIntent.LoadChat(requestId))
    }

    ObserveEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            is ChatEffect.InitiateCall -> dialPhoneNumber(context, effect.phoneNumber)
            ChatEffect.NavigateBack -> onNavigateBack()
            ChatEffect.ScrollToBottom -> coroutineScope.launch {
                if (listState.layoutInfo.totalItemsCount > 0) {
                        listState.animateScrollToItem(0)
                }
            }

            is ChatEffect.ShowError -> showSnackbar(effect.message)
        }
    }

    HideTopBar()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = Theme.colors.backGround,
        topBar = {
            ChatTopBar(
                nurseName = state.participant?.name.orEmpty(),
                isOnline = state.participant?.isOnline == true,
                photoUrl = state.participant?.photoUrl,
                onBackClick = { viewModel.handleIntent(ChatIntent.OnBackClicked) },
                onCallClick = { viewModel.handleIntent(ChatIntent.OnCallClicked) },
            )
        },
        bottomBar = {
            ChatInputBar(
                value = state.inputText,
                onValueChange = { value -> viewModel.handleIntent(ChatIntent.OnMessageInputChanged(value)) },
                onSendClick = { viewModel.handleIntent(ChatIntent.OnSendMessageClicked) },
            )
        },
    ) { paddingValues ->
        ChatScreenContent(
            state = state,
            listState = listState,
            modifier = Modifier.padding(paddingValues),
        )
    }

    if (state.showNurseCancelledDialog) {
        CancelVisitConfirmationDialog(
            title = stringResource(R.string.nurse_on_the_way_nurse_cancelled_title),
            message = stringResource(R.string.nurse_on_the_way_nurse_cancelled_message),
            confirmText = stringResource(R.string.ok),
            dismissText = "",
            onConfirm = { viewModel.handleIntent(ChatIntent.OnNurseCancelledDismissed) },
            onDismiss = { viewModel.handleIntent(ChatIntent.OnNurseCancelledDismissed) }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ChatScreenContent(
    state: ChatState,
    listState: LazyListState,
    modifier: Modifier = Modifier,
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        if (state.isLoading) {
            ChatLoadingShimmer()
        } else {
            val groupedMessages = remember(state.messages) {
                state.messages
                    .groupBy { message -> message.dayKey() }
                    .toList()
                    .asReversed()
            }

            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                groupedMessages.forEach { (_, messagesForDay) ->
                    items(
                        items = messagesForDay.asReversed(),
                        key = { message -> message.id },
                    ) { message ->
                        MessageBubble(message = message)
                    }

                    val firstMessage = messagesForDay.first()

                    item(key = "date_${firstMessage.id}") {
                        DateSeparatorPill(
                            label = formatDateSeparator(
                                firstMessage.sentAtEpochMillis
                            )
                        )
                    }
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        ChatScreenContent(
            state = ChatState(),
            listState = rememberLazyListState(),

        )
    }
}

@Composable
private fun ChatLoadingShimmer() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .width(92.dp)
                        .height(28.dp),
                    shape = CircleShape,
                )
            }
        }

        items(6) { index ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (index % 2 == 0) {
                    androidx.compose.ui.Alignment.Start
                } else {
                    androidx.compose.ui.Alignment.End
                },
            ) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(if (index % 3 == 0) 0.72f else 0.58f)
                        .height(if (index % 2 == 0) 72.dp else 54.dp),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (index % 2 == 0) 4.dp else 20.dp,
                        bottomEnd = if (index % 2 == 0) 20.dp else 4.dp,
                    ),
                )
                Spacer(modifier = Modifier.height(6.dp))
                ShimmerPlaceholder(
                    modifier = Modifier
                        .width(52.dp)
                        .height(10.dp),
                    shape = CircleShape,
                )
            }
        }
    }
}
