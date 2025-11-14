package com.waytube.app.navigation.data

import com.waytube.app.navigation.domain.DeepLinkResult
import com.waytube.app.navigation.domain.NavigationRepository
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeChannelLinkHandlerFactory
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubePlaylistLinkHandlerFactory
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeStreamLinkHandlerFactory

class NewPipeNavigationRepository : NavigationRepository {
    override fun resolveDeepLink(url: String): DeepLinkResult? =
        when (ServiceList.YouTube.getLinkTypeByUrl(url)) {
            StreamingService.LinkType.STREAM -> DeepLinkResult.Video(
                YoutubeStreamLinkHandlerFactory.getInstance().getId(url)
            )

            StreamingService.LinkType.CHANNEL -> DeepLinkResult.Channel(
                YoutubeChannelLinkHandlerFactory.getInstance().getId(url)
            )

            StreamingService.LinkType.PLAYLIST -> DeepLinkResult.Playlist(
                YoutubePlaylistLinkHandlerFactory.getInstance().getId(url)
            )

            StreamingService.LinkType.NONE -> null
        }
}
