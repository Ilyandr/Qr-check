package gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse

import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity

interface RestRecordDataSource
{
    suspend fun setRecord(
        userAuthKey: String?
        , dataToken: String?
        , userLocation: Pair<Double, Double>): Long?

    suspend fun getAllRecord(
        userAuthKey: String?, pointId: Long): MutableList<UserInputEntity>?
}