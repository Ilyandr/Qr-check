package gcu.production.qr_check.Service.Base64

private const val BASE64_ALPHABET: String =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
private const val BASE64_MASK: Byte = 0x3f
private const val BASE64_PAD: Char = '='

private fun Int.toBase64(): Char = BASE64_ALPHABET[this]

actual object Base64Factory
{
    actual fun createEncoder():
            Base64Encoder = NativeBase64Encoder
}

object NativeBase64Encoder : Base64Encoder
{
    override fun encode(src: ByteArray): ByteArray
    {
        fun ByteArray.getOrZero(index: Int): Int =
            if (index >= size)
                0
            else
                get(index).toInt()

        val result =
            ArrayList<Byte>(
                4 * src.size / 3)
        var index = 0

        while (index < src.size)
        {
            val symbolsLeft =
                src.size - index
            val padSize =
                if (symbolsLeft >= 3) 0
                else (3 - symbolsLeft) * 8 / 6

            val chunk =
                (src.getOrZero(index) shl 16) or (
                        src.getOrZero(index + 1)
                                shl 8) or src.getOrZero(index + 2)
            index += 3

            for (i in 3 downTo padSize)
                result.add(
                    ((chunk shr (6 * i)) and BASE64_MASK.toInt())
                        .toBase64().code.toByte()
                )

            repeat(padSize) {
                result.add(BASE64_PAD.code.toByte())
            }
        }
        return result.toByteArray()
    }
}