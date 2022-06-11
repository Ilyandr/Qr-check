package gcu.production.qrcheck.RestAPI.Models.User

import kotlinx.serialization.Serializable

@Serializable
data class UserInputEntity(
    var id: Long? = null
    , var name: String? = null
    , var phone: String? = null
    , var password: String? = null
    , var jobTitle: String? = null
    , var organization: String? = null
    , var roles: List<String>? = null
    , var time: String? = null)