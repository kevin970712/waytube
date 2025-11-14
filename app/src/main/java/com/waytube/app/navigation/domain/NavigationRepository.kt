package com.waytube.app.navigation.domain

interface NavigationRepository {
    fun resolveDeepLink(url: String): DeepLinkResult?
}
