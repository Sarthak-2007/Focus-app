package com.example

import android.security.keystore.KeyGenParameterSpec
import java.io.InputStream
import java.io.OutputStream
import java.security.Key
import java.security.KeyStore
import java.security.KeyStoreSpi
import java.security.Provider
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.spec.AlgorithmParameterSpec
import java.util.Collections
import java.util.Date
import java.util.Enumeration
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.KeyGeneratorSpi
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Issue #36 (ruling 2): the test environment's AndroidKeyStore.
 *
 * Production law is now absolute — the ONLY signing key is the
 * AndroidKeyStore key; no fallback key exists. Robolectric ships
 * no working AndroidKeyStore, so without a platform double the
 * entire suite would run in a world where signing is impossible
 * and every session is born rejected — the suite would pin a
 * fiction. This provider is that double: an honest, in-memory
 * stand-in for the platform service (the same role Robolectric's
 * own shadows play for SQLite, SharedPreferences and the clock),
 * installed ONLY in tests (see [TestMonolithApp] and
 * robolectric.properties). It lives in the test source set; no
 * production code path can reach it, and production behaviour on
 * a keystore-less runtime stays fail-closed.
 *
 * Fidelity contract: keys are real HmacSHA256 keys, generated on
 * KeyGenerator.generateKey() under the alias the production
 * KeyGenParameterSpec names, persisted in the registry across
 * getInstance calls within the test sandbox, deletable, and
 * lookup-only reads behave exactly like the real store's. What it
 * does NOT fake: hardware custody — there is deliberately no
 * KeyFactory/KeyInfo service here, so
 * CryptoIntegrityManager.hardwareBackingVerified() reads false in
 * tests, exactly as ruling 4 demands wherever verification is
 * impossible.
 */
class FakeAndroidKeyStoreProvider :
    Provider("AndroidKeyStore", 1.0, "Test-only in-memory AndroidKeyStore double (issue #36)") {
    init {
        put("KeyStore.AndroidKeyStore", FakeAndroidKeyStoreSpi::class.java.name)
        put("KeyGenerator.HmacSHA256", FakeHmacKeyGeneratorSpi::class.java.name)
    }
}

/** The double's key table — the coupling point between the
 *  KeyGenerator (which mints under an alias) and the KeyStore
 *  (which serves by alias), mirroring how the real platform
 *  couples them through the keystore service. Static within the
 *  sandbox classloader, so it resets with the sandbox like every
 *  other test-environment state. */
internal object FakeAndroidKeyStoreRegistry {
    val keys = ConcurrentHashMap<String, SecretKey>()
}

class FakeAndroidKeyStoreSpi : KeyStoreSpi() {

    override fun engineGetKey(alias: String, password: CharArray?): Key? =
        FakeAndroidKeyStoreRegistry.keys[alias]

    override fun engineGetEntry(
        alias: String,
        protParam: KeyStore.ProtectionParameter?
    ): KeyStore.Entry? =
        FakeAndroidKeyStoreRegistry.keys[alias]?.let { KeyStore.SecretKeyEntry(it) }

    override fun engineGetCertificate(alias: String): Certificate? = null

    override fun engineGetCertificateChain(alias: String): Array<Certificate>? = null

    override fun engineGetCreationDate(alias: String): Date = Date(0L)

    override fun engineSetKeyEntry(
        alias: String,
        key: Key,
        password: CharArray?,
        chain: Array<Certificate>?
    ) {
        require(key is SecretKey) { "Only secret keys live in this double" }
        FakeAndroidKeyStoreRegistry.keys[alias] = key
    }

    override fun engineSetKeyEntry(alias: String, key: ByteArray, chain: Array<Certificate>?) {
        FakeAndroidKeyStoreRegistry.keys[alias] = SecretKeySpec(key, "HmacSHA256")
    }

    override fun engineSetCertificateEntry(alias: String, cert: Certificate) {
        throw UnsupportedOperationException("The double holds no certificates")
    }

    override fun engineDeleteEntry(alias: String) {
        FakeAndroidKeyStoreRegistry.keys.remove(alias)
    }

    override fun engineAliases(): Enumeration<String> =
        Collections.enumeration(FakeAndroidKeyStoreRegistry.keys.keys)

    override fun engineContainsAlias(alias: String): Boolean =
        FakeAndroidKeyStoreRegistry.keys.containsKey(alias)

    override fun engineSize(): Int = FakeAndroidKeyStoreRegistry.keys.size

    override fun engineIsKeyEntry(alias: String): Boolean =
        FakeAndroidKeyStoreRegistry.keys.containsKey(alias)

    override fun engineIsCertificateEntry(alias: String): Boolean = false

    override fun engineGetCertificateAlias(cert: Certificate): String? = null

    override fun engineStore(stream: OutputStream?, password: CharArray?) {
        // In-memory by construction: nothing to persist.
    }

    override fun engineLoad(stream: InputStream?, password: CharArray?) {
        // In-memory by construction: nothing to load.
    }
}

class FakeHmacKeyGeneratorSpi : KeyGeneratorSpi() {

    private var alias: String? = null

    override fun engineInit(random: SecureRandom) {
        // Defaults stand (256-bit HMAC key, no alias until a spec names one).
    }

    override fun engineInit(keysize: Int, random: SecureRandom) {
        // The double always mints 256-bit keys; the requested size
        // is advisory here exactly as a provider may choose.
    }

    override fun engineInit(params: AlgorithmParameterSpec, random: SecureRandom) {
        if (params is KeyGenParameterSpec) {
            alias = params.keystoreAlias
        }
    }

    override fun engineGenerateKey(): SecretKey {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        val key = SecretKeySpec(bytes, "HmacSHA256")
        alias?.let { FakeAndroidKeyStoreRegistry.keys[it] = key }
        return key
    }
}
