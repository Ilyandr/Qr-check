package gcu.production.qrcheck.RestAPI.Models.Point

import kotlinx.serialization.Serializable

@Serializable
data class DataPointInputEntity(
    var id: Long? = null
    , var creatorId: Long? = null
    , var x: Double? = null
    , var y: Double? = null
    , var radius: Int? = null
    , var createTime: String? = null
    , var actualTime: String? = null
)