package gcu.production.qrcheck.AppConfiguration

data class Configuration(val platformType: PlatformType)

sealed class PlatformType
{
    data class IOS(
        val versionPlatform: String
        , val buildNumber: String): PlatformType()

    data class Android(
        val versionPlatform: String
        , val buildNumber: String): PlatformType()
}