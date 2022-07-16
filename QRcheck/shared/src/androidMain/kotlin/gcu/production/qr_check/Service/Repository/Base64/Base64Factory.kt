package gcu.production.qr_check.Service.Repository.Base64

import java.util.*

actual object Base64Factory
{
    actual fun createEncoder():
            Base64Encoder = JvmBase64Encoder
}

object JvmBase64Encoder : Base64Encoder
{
    override fun encode(src: ByteArray): ByteArray =
        Base64.getEncoder().encode(src)
}