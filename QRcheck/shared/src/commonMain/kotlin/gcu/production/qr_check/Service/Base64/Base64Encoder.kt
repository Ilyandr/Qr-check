package gcu.production.qr_check.Service.Base64

interface Base64Encoder
{
    fun encode(src: ByteArray): ByteArray

    fun encodeToString(src: ByteArray): String
    {
        val encoded = encode(src)

        return buildString(encoded.size) {
            encoded.forEach { append(it.toInt().toChar()) }
        }
    }
}