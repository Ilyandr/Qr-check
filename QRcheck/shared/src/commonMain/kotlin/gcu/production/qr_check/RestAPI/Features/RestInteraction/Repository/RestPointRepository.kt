package gcu.production.qrcheck.RestAPI.Features.RestInteraction.Repository

import gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse.RestPointDataSource
import gcu.production.qrcheck.RestAPI.Models.Point.DataPointOutputEntity

class RestPointRepository(
    private val restPointDataSource: RestPointDataSource)
{
    suspend fun generatePoint(
        userLoginKey: String?
        , dataPointOutputEntity: DataPointOutputEntity
    ) =
        this.restPointDataSource.generatePoint(
            userLoginKey, dataPointOutputEntity)

    suspend fun generateToken(
        userLoginKey: String?, pointId: Long) =
        this.restPointDataSource.generateToken(
            userLoginKey, pointId)

    suspend fun getAllPoint(userLoginKey: String?) =
        this.restPointDataSource.getAllPoint(userLoginKey)
}