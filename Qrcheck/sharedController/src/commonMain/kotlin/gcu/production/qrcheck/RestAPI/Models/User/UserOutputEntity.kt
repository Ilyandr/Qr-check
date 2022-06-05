package gcu.production.qrcheck.RestAPI.Models.User

import kotlinx.serialization.Serializable

@Serializable
data class UserOutputEntity(
    val name: String
    , val password: String
    , val phone: String
    , val jobTitle: String
    , val organization: String
    , val roles: List<String>)