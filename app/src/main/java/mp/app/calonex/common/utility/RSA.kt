package mp.app.calonex.common.utility

import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object RSA {
    private const val KEY_ALGORITHM = "RSA"
    private const val ENC_KEY = "K1pmr0ENw4jc7p2dneq7xPbxJJTKAvMZ"
    private fun decryptBASE64(key: String): ByteArray {
        return Base64.decode(key, Base64.NO_WRAP)
    }

    @Throws(Exception::class)
    fun encryptByPublicKey(data: ByteArray?, key: String): ByteArray {
        val keyBytes = decryptBASE64(key)
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicKey: Key = keyFactory.generatePublic(x509KeySpec)
        val cipher = Cipher.getInstance(keyFactory.algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    fun encrypt(clearText: String): String? {
        try {
            val keySize = 256
            val ivSize = 128

            // Create empty key and iv
            val key = ByteArray(keySize / 8)
            val iv = ByteArray(ivSize / 8)

            // Create random salt
            val saltBytes = generateSalt(8)

            // Derive key and iv from passphrase and salt
            evpKDF(ENC_KEY.toByteArray(StandardCharsets.UTF_8), keySize, ivSize, saltBytes, key, iv)

            // Actual encrypt
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
            val cipherBytes = cipher.doFinal(clearText.toByteArray(StandardCharsets.UTF_8))

            /**
             * Create CryptoJS-like encrypted string from encrypted data
             * This is how CryptoJS do:
             * 1. Create new byte array to hold ecrypted string (b)
             * 2. Concatenate 8 bytes to b
             * 3. Concatenate salt to b
             * 4. Concatenate encrypted data to b
             * 5. Encode b using Base64
             */
            val sBytes = "Salted__".toByteArray(StandardCharsets.UTF_8)
            val b = ByteArray(sBytes.size + saltBytes.size + cipherBytes.size)
            System.arraycopy(sBytes, 0, b, 0, sBytes.size)
            System.arraycopy(saltBytes, 0, b, sBytes.size, saltBytes.size)
            System.arraycopy(cipherBytes, 0, b, sBytes.size + saltBytes.size, cipherBytes.size)
            val base64b = Base64.encode(b, Base64.NO_WRAP)
            return String(base64b)/*.replace("/".toRegex(), ".").replace("\\+".toRegex(), ",")
                .replace("\n".toRegex(), "")*/
        } catch (e: Exception) {
            Log.e("Encrypt", e.message!!)
        }
        return null
    }

    private fun generateSalt(length: Int): ByteArray {
        val r: Random = SecureRandom()
        val salt = ByteArray(length)
        r.nextBytes(salt)
        return salt
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun evpKDF(
        password: ByteArray,
        keySize: Int,
        ivSize: Int,
        salt: ByteArray,
        resultKey: ByteArray,
        resultIv: ByteArray
    ): ByteArray {
        return evpKDF(password, keySize, ivSize, salt, 1, resultKey, resultIv)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun evpKDF(
        password: ByteArray,
        keySize: Int,
        ivSize: Int,
        salt: ByteArray,
        iterations: Int,
        resultKey: ByteArray,
        resultIv: ByteArray
    ): ByteArray {
        var keySize = keySize
        var ivSize = ivSize
        keySize = keySize / 32
        ivSize = ivSize / 32
        val targetKeySize = keySize + ivSize
        val derivedBytes = ByteArray(targetKeySize * 4)
        var numberOfDerivedWords = 0
        var block: ByteArray? = null
        val hasher = MessageDigest.getInstance("MD5")
        while (numberOfDerivedWords < targetKeySize) {
            if (block != null) {
                hasher.update(block)
            }
            hasher.update(password)
            block = hasher.digest(salt)
            hasher.reset()
            for (i in 1 until iterations) {
                block = hasher.digest(block)
                hasher.reset()
            }
            System.arraycopy(
                block, 0, derivedBytes, numberOfDerivedWords * 4,
                Math.min(block!!.size, (targetKeySize - numberOfDerivedWords) * 4)
            )
            numberOfDerivedWords += block.size / 4
        }
        System.arraycopy(derivedBytes, 0, resultKey, 0, keySize * 4)
        System.arraycopy(derivedBytes, keySize * 4, resultIv, 0, ivSize * 4)
        return derivedBytes // key + iv
    }

    fun decrypt(ciphertext: String): String? {
        try {
            val keySize = 256
            val ivSize = 128

            // Decode from base64 text
            val ctBytes = Base64.decode(ciphertext.toByteArray(charset("UTF-8")), Base64.NO_WRAP)

            // Get salt
            val saltBytes = Arrays.copyOfRange(ctBytes, 8, 16)

            // Get ciphertext
            val ciphertextBytes = Arrays.copyOfRange(ctBytes, 16, ctBytes.size)

            // Get key and iv from passphrase and salt
            val key = ByteArray(keySize / 8)
            val iv = ByteArray(ivSize / 8)
            evpKDF(ENC_KEY.toByteArray(charset("UTF-8")), keySize, ivSize, saltBytes, key, iv)

            // Actual decrypt
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
            val recoveredPlaintextBytes = cipher.doFinal(ciphertextBytes)
            return String(recoveredPlaintextBytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}