package com.waytube.app.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.waytube.app.R
import com.waytube.app.common.domain.Identifiable

fun <T : Identifiable> LazyListScope.pagingItems(
    items: LazyPagingItems<T>,
    itemContent: @Composable (T) -> Unit
) {
    when (items.loadState.refresh) {
        is LoadState.Loading -> {
            item {
                PagingLoadingItem()
            }
        }

        is LoadState.Error -> {
            item {
                PagingStateMessageItem(
                    text = stringResource(R.string.message_paging_load_error),
                    onRetry = items::retry
                )
            }
        }

        is LoadState.NotLoading -> {
            items(
                count = items.itemCount,
                key = items.itemKey { it.id }
            ) { index ->
                items[index]?.let { itemContent(it) }
            }

            when (val state = items.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        PagingLoadingItem()
                    }
                }

                is LoadState.Error -> {
                    item {
                        PagingStateMessageItem(
                            text = stringResource(R.string.message_paging_load_error),
                            onRetry = items::retry
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    if (items.itemCount == 0 && state.endOfPaginationReached) {
                        item {
                            PagingStateMessageItem(
                                text = stringResource(R.string.message_paging_empty)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PagingLoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PagingStateMessageItem(
    text: String,
    onRetry: (() -> Unit)? = null
) {
    StateMessage(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        onRetry = onRetry
    )
}
