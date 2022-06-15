package gcu.production.qrcheck.RestAPI.Models.Point

import kotlinx.serialization.Serializable

@Serializable
data class DataPointOutputEntity(
    val x: Double? = null
    , val y: Double? = null
    , val radius: Int? = null
    , val actualTime: String? = null)