package gcu.production.qrcheck.RestAPI.Models.Point

import kotlinx.serialization.Serializable

@Serializable
data class DataPointOutputEntity(
    val x: Double
    , val y: Double
    , val radius: Int
    , val actualTime: String)