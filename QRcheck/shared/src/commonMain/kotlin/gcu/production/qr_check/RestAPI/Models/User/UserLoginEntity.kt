package gcu.production.qrcheck.RestAPI.Models.User

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginEntity(
    val login: String, val password: String)
