package gcu.production.qr_check.Service.Repository.Base64

expect object Base64Factory
{
    fun createEncoder(): Base64Encoder
}