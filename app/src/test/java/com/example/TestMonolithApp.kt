package com.example

import java.security.Security

/**
 * Issue #36 (ruling 2): the test Application. Installs the
 * test-only AndroidKeyStore double ([FakeAndroidKeyStoreProvider])
 * at the head of the process, before any production code can ask
 * the platform for its keystore — then runs MonolithApp's own
 * startup untouched. Registered for every Robolectric test via
 * src/test/resources/robolectric.properties; tests that name their
 * own Application in @Config keep theirs.
 */
class TestMonolithApp : MonolithApp() {
    override fun onCreate() {
        installTestKeyStore()
        super.onCreate()
    }

    companion object {
        /** Idempotent: safe from the Application and from any
         *  test's own setup alike. */
        fun installTestKeyStore() {
            if (Security.getProvider("AndroidKeyStore") !is FakeAndroidKeyStoreProvider) {
                Security.insertProviderAt(FakeAndroidKeyStoreProvider(), 1)
            }
        }
    }
}
