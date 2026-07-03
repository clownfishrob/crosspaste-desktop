package com.crosspaste.secure

import com.crosspaste.app.AppFileType
import com.crosspaste.app.AppInfo
import com.crosspaste.app.DesktopAppIdentity
import com.crosspaste.db.secure.SecureIO
import com.crosspaste.path.AppPathProvider
import com.crosspaste.presist.FilePersist
import com.crosspaste.utils.CryptographyUtils
import com.crosspaste.utils.EncryptUtils
import com.crosspaste.utils.getAppEnvUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking

class MacosSecureStoreFactory(
    private val appInfo: AppInfo,
    appPathProvider: AppPathProvider,
    private val secureKeyPairSerializer: SecureKeyPairSerializer,
    private val secureIO: SecureIO,
) : SecureStoreFactory {

    private val logger = KotlinLogging.logger {}

    private val appEnvUtils = getAppEnvUtils()

    private val filePersist =
        FilePersist.createOneFilePersist(
            appPathProvider.resolve("secure.data", AppFileType.ENCRYPT),
        )

    private val keyPersist =
        FilePersist.createOneFilePersist(
            appPathProvider.resolve("secure.key", AppFileType.ENCRYPT),
        )

    override fun createSecureStore(): SecureStore =
        runBlocking {
            val service =
                "${DesktopAppIdentity.packageName}-${appEnvUtils.getCurrentAppEnv().name}-${appInfo.appInstanceId}"
            val file = filePersist.path.toFile()
            if (file.exists()) {
                logger.info { "Found secureKeyPair encrypt file" }
                val bytes = file.readBytes()
                val secretKey = getOrCreateLocalSecretKey(service)
                runCatching {
                    val decryptData = EncryptUtils.decryptData(secretKey, bytes)
                    val secureKeyPair = secureKeyPairSerializer.decodeSecureKeyPair(decryptData)
                    return@runBlocking GeneralSecureStore(secureKeyPair, secureKeyPairSerializer, secureIO)
                }.onFailure { e ->
                    logger.error(e) { "Decrypt secureKeyPair error" }
                }

                if (file.delete()) {
                    logger.info { "Delete secureKeyPair encrypt file" }
                }
            } else {
                logger.info { "Not found secureKeyPair encrypt file" }
            }

            logger.info { "Generate secureKeyPair" }
            val secureKeyPair = CryptographyUtils.generateSecureKeyPair()
            val data = secureKeyPairSerializer.encodeSecureKeyPair(secureKeyPair)
            val secretKey = getOrCreateLocalSecretKey(service)
            val encryptData = EncryptUtils.encryptData(secretKey, data)
            filePersist.saveBytes(encryptData)
            GeneralSecureStore(secureKeyPair, secureKeyPairSerializer, secureIO)
        }

    private fun getOrCreateLocalSecretKey(service: String) =
        keyPersist.readBytes()?.decodeToString()?.let {
            logger.info { "Found local secure-store key for $service" }
            EncryptUtils.stringToSecretKey(it)
        } ?: run {
            logger.info { "Generate local secure-store key for $service" }
            EncryptUtils.generateAESKey().also {
                keyPersist.saveBytes(EncryptUtils.secretKeyToString(it).encodeToByteArray())
            }
        }
}
