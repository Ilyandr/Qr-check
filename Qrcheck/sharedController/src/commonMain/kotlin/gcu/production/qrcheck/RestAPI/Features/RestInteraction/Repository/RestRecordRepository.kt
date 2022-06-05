package gcu.production.qrcheck.RestAPI.Features.RestInteraction.Repository

import gcu.production.qrcheck.RestAPI.Features.RestInteraction.KtorAPI.KtorRestRecordDataSource

class RestRecordRepository(
    private val restRecordDataSource: KtorRestRecordDataSource)
{
    suspend fun setRecord(
        userAuthKey: String?
        , dataToken: String?
        , userLocation: Pair<Double, Double>) =
        this.restRecordDataSource.setRecord(
            userAuthKey, dataToken, userLocation)

    suspend fun getAllRecord(
        userAuthKey: String?, pointId: Long) =
        this.restRecordDataSource.getAllRecord(
            userAuthKey, pointId)
}