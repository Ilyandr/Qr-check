package gcu.production.qrcheck.RestAPI.Models.Point

import kotlinx.serialization.Serializable

@Serializable
data class DataPointInputEntity(
    val id: Long
    , val creatorId: Long
    , val x: Double
    , val y: Double
    , val radius: Int
    , val createTime: String
    , val actualTime: String)