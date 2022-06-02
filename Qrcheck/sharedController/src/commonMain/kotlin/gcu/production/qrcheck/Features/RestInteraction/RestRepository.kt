package gcu.production.qrcheck.Features.RestInteraction

import gcu.production.qrcheck.Features.RestInteraction.DataSourse.RestDataSource

class RestRepository(
    private val restDataSource: RestDataSource)
{
    suspend fun fetchNews() =
        this.restDataSource.fetchNews()
}