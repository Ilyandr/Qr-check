package gcu.production.qrcheck.RestAPI.Features.RestInteraction.Repository

import gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse.RestAuthDataSource
import gcu.production.qrcheck.RestAPI.Models.User.UserOutputEntity

class RestAuthRepository(
    private val restAuthDataSource: RestAuthDataSource)
{
    suspend fun registration(userOutputEntity: UserOutputEntity) =
        this.restAuthDataSource.registration(userOutputEntity)

    suspend fun login(userLoginKey: String?) =
        this.restAuthDataSource.login(userLoginKey)

    suspend fun existUser(
        userLoginData: String?) =
        this.restAuthDataSource.existUser(userLoginData)
}