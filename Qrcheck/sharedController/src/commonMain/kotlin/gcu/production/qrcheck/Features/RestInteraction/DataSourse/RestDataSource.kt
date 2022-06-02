package gcu.production.qrcheck.Features.RestInteraction.DataSourse

interface RestDataSource
{
    suspend fun fetchNews(): String
}