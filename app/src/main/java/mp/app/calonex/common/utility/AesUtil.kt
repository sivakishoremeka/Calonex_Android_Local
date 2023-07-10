package mp.app.calonex.common.utility

import android.util.Base64
import com.google.android.gms.common.util.Hex
import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AesUtil {
    private val keySize: Int
    private val iterationCount: Int
    private var cipher: Cipher? = null
    private var ENCRYPTION_KEY:String="K1pmr0ENw4jc7p2dneq7xPbxJJTKAvMZ"



    constructor() : super() {
        keySize = 128
        iterationCount = 1000
        cipher = try {
            Cipher.getInstance("AES/CBC/PKCS5Padding")
        } catch (e: NoSuchAlgorithmException) {
            throw fail(e)
        } catch (e: NoSuchPaddingException) {
            throw fail(e)
        }
    }

    fun encryptString(plaintext: String): String {
        return encrypt(
            "dc0da04af8fee58593442bf834b30739", "dc0da04af8fee58593442bf834b30739",
            ENCRYPTION_KEY, plaintext
        )
    }

    fun encrypt(salt: String, iv: String, passphrase: String, plaintext: String): String {
        return try {
            val key = generateKey(salt, passphrase)
            val encrypted =
                doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.toByteArray(charset("UTF-8")))
            base64(encrypted)
        } catch (e: UnsupportedEncodingException) {
            throw fail(e)
        }
    }

    fun decryptString(ciphertext: String?): String {
        return decrypt(
            "dc0da04af8fee58593442bf834b30739", "dc0da04af8fee58593442bf834b30739",
            ENCRYPTION_KEY, ciphertext
        )
    }

    fun decrypt(salt: String, iv: String, passphrase: String, ciphertext: String?): String {
        return try {
            val key = generateKey(salt, passphrase)
            val decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, base64(ciphertext))
            String(decrypted, charset("UTF8"))
        } catch (e: UnsupportedEncodingException) {
            throw fail(e)
        }
    }

    private fun doFinal(encryptMode: Int, key: SecretKey, iv: String, bytes: ByteArray): ByteArray {
        return try {
            cipher!!.init(encryptMode, key, IvParameterSpec(hex(iv)))
            cipher!!.doFinal(bytes)
        } catch (e: InvalidKeyException) {
            throw fail(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw fail(e)
        } catch (e: IllegalBlockSizeException) {
            throw fail(e)
        } catch (e: BadPaddingException) {
            throw fail(e)
        }
    }

    private fun generateKey(salt: String, passphrase: String): SecretKey {
        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec: KeySpec =
                PBEKeySpec(passphrase.toCharArray(), hex(salt), iterationCount, keySize)
            SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
        } catch (e: Exception) {
            e.printStackTrace()
            throw fail(e)
        }
    }

    private fun fail(e: Exception): IllegalStateException {
        return IllegalStateException(e)
    }

    companion object {
        fun random(length: Int): String {
            val salt = ByteArray(length)
            SecureRandom().nextBytes(salt)
            return hex(salt)
        }

        fun base64(bytes: ByteArray?): String {
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }

        fun base64(str: String?): ByteArray {
            return Base64.decode(str, Base64.NO_WRAP)
        }

        fun hex(bytes: ByteArray?): String {
            return Hex.bytesToStringLowercase(bytes!!)
        }

        fun hex(str: String?): ByteArray {
            return try {
                Hex.stringToBytes(str!!)
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }
    }
}