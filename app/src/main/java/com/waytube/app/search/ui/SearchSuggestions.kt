package com.waytube.app.search.ui

data class SearchSuggestions(
    val items: List<String>,
    val type: Type
) {
    enum class Type {
        HISTORY,
        REMOTE
    }
}
