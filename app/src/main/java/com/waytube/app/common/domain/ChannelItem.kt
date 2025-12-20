package com.waytube.app.common.domain

data class ChannelItem(
    override val id: String,
    val url: String,
    val name: String,
    val avatarUrl: String,
    val subscriberCount: Long?
) : Identifiable
