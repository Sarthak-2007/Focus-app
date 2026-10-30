package com.example

import android.content.Context
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.LiveRepositoryServicePort

/**
 * ISSUE-67 (Audit R1 · architecture, finding 3) — THE SINGLE
 * CONSTRUCTION POINT.
 *
 * Before this file there was no DI and no single source of truth:
 * `MonolithDatabase.getInstance(...)` + `MonolithRepository(...)`
 * were hand-assembled at every entry point (MainActivity, the
 * shield service, the routine heartbeat, the seal-transition
 * provider), each site free to drift from the others, and the
 * repository itself re-fetched the database behind its injected
 * DAO's back.
 *
 * Every production construction now routes through here:
 *  - [database] is the process's one Room instance (the database's
 *    own getInstance singleton remains the storage of that fact);
 *  - [repository] is built with its DAO, its LIVE service port
 *    (issue-67's RepositoryServicePort) and the SAME database
 *    instance injected, so its transaction sites use the injected
 *    database instead of re-fetching it;
 *  The screen's sound engine is NOT built here: issue #27 moved
 *  its birth into the ViewModel Factory's create() (so a retained
 *  ViewModel never orphans an engine) — that design landed on main
 *  while this branch was in flight and stands.
 *
 * Unit tests keep constructing the repository directly with a
 * test DAO — that is a test seam, not a second production truth.
 */
object AppContainer {

    fun database(context: Context): MonolithDatabase =
        MonolithDatabase.getInstance(context.applicationContext)

    fun dao(context: Context): MonolithDao =
        database(context).monolithDao()

    fun repository(context: Context): MonolithRepository {
        val appContext = context.applicationContext
        val db = database(appContext)
        return MonolithRepository(
            context = appContext,
            dao = db.monolithDao(),
            servicePort = LiveRepositoryServicePort(),
            database = db
        )
    }
}
