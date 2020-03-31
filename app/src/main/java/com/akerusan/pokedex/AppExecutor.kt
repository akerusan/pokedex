package com.akerusan.pokedex

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// returning from the cache on the a background thread
open class AppExecutors(private val diskIO: Executor,
                        private val networkIO: Executor,
                        private val mainThread: Executor) {

    // network useless because I'm converting retrofit call objects to liveData objects
    // so background thread useless, because liveData does them asynchronously

    private var instance: AppExecutors? = null

    constructor() : this(
        Executors.newSingleThreadExecutor(),
        Executors.newFixedThreadPool(3),
        MainThreadExecutor()
    )

    fun getInstance(): AppExecutors? {
        if (instance == null) {
            instance = AppExecutors()
        }
        return instance
    }

    // DB operations for the cache
    fun diskIO(): Executor? {
        return diskIO
    }


    fun mainThread(): Executor? {
        return mainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}