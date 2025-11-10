package com.waytube.app.common.domain

data class ChannelItem(
    override val id: String,
    val name: String,
    val avatarUrl: String
) : Identifiable
