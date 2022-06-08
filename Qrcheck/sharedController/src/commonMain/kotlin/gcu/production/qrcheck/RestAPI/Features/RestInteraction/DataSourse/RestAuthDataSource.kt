package gcu.production.qrcheck.RestAPI.Features.RestInteraction.DataSourse

import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import gcu.production.qrcheck.RestAPI.Models.User.UserOutputEntity

interface RestAuthDataSource
{
    suspend fun registration(
        userOutputEntity: UserOutputEntity
    ): UserInputEntity?

    suspend fun login(
        userLoginKey: String?): UserInputEntity?

    suspend fun existUser(
        userLoginData: String?): Boolean
}