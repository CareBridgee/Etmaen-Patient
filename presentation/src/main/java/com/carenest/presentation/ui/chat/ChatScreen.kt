package com.carenest.presentation.ui.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.core.util.dayKey
import com.carenest.presentation.core.util.dialPhoneNumber
import com.carenest.presentation.core.util.formatDateSeparator
import com.carenest.presentation.ui.chat.components.ChatInputBar
import com.carenest.presentation.ui.chat.components.ChatTopBar
import com.carenest.presentation.ui.chat.components.DateSeparatorPill
import com.carenest.presentation.ui.chat.components.MessageBubble
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
                if (state.messages.isNotEmpty()) {
                    listState.animateScrollToItem(state.messages.lastIndex)
                }
            }

            is ChatEffect.ShowError -> showSnackbar(effect.message)
        }
    }

    Scaffold(
        containerColor = Theme.colors.backGround,
        topBar = {
            ChatTopBar(
                nurseName = state.participant?.name.orEmpty(),
                isOnline = state.participant?.isOnline == true,
                onBackClick = { viewModel.handleIntent(ChatIntent.OnBackClicked) },
                onCallClick = { viewModel.handleIntent(ChatIntent.OnCallClicked) },
            )
        },
        bottomBar = {
            ChatInputBar(
                value = state.inputText,
                onValueChange = { viewModel.handleIntent(ChatIntent.OnMessageInputChanged(it)) },
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ChatScreenContent(
    state: ChatState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier,
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Theme.colors.primary,
            )
        } else {
            val groupedMessages = remember(state.messages) {
                state.messages.groupBy { it.dayKey() }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                groupedMessages.forEach { (_, messagesForDay) ->
                    val firstMessage = messagesForDay.first()
                    item(key = "date_${firstMessage.id}") {
                        DateSeparatorPill(label = formatDateSeparator(firstMessage.sentAtEpochMillis))
                    }
                    itemsIndexed(messagesForDay, key = { _, m -> m.id }) { _, message ->
                        MessageBubble(message = message)
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