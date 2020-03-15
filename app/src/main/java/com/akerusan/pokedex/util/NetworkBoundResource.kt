package com.akerusan.pokedex.util

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.akerusan.pokedex.AppExecutors
import com.akerusan.pokedex.requests.response.ApiEmptyResponse
import com.akerusan.pokedex.requests.response.ApiErrorResponse
import com.akerusan.pokedex.requests.response.ApiResponse
import com.akerusan.pokedex.requests.response.ApiSuccessResponse


abstract class NetworkBoundResource<CacheObject, RequestObject>(private val appExecutors: AppExecutors) {

    private var results: MediatorLiveData<Resource<CacheObject>> = MediatorLiveData()

    init {
        // update live data for loading status
        results.value = Resource.loading(null) as Resource<CacheObject>
        // observe live data from local DB
        @Suppress("LeakingThis")
        val dbSource: LiveData<CacheObject> = loadFromDb()

        results.addSource(dbSource, object: Observer<CacheObject>{
            override fun onChanged(data: CacheObject) {
                results.removeSource(dbSource)
                if (shouldFetch(data)) {
                    // get data from the network
                    fetchFromNetwork(dbSource)
                } else {
                    results.addSource(dbSource, object: Observer<CacheObject>{
                        override fun onChanged(newData: CacheObject) {
                            setValue(Resource.success(newData))
                        }
                    })
                }
            }
        })
    }

    private fun fetchFromNetwork(dbSource: LiveData<CacheObject>) {
        val apiResponse = createCall()
        results.addSource(dbSource, object: Observer<CacheObject>{
            override fun onChanged(t: CacheObject) {
                setValue(Resource.loading(t))
            }
        })
        results.addSource(apiResponse) { response ->
            results.removeSource(dbSource)
            results.removeSource(apiResponse)

            when (response) {
                is ApiSuccessResponse -> {
                    appExecutors.diskIO()!!.execute(object: Runnable{
                        override fun run() {
                            // save the response to the local db
                            saveCallResult(processResponse(response))
                            appExecutors.mainThread()!!.execute(object: Runnable{
                                override fun run() {
                                    results.addSource(loadFromDb(), object: Observer<CacheObject>{
                                        override fun onChanged(t: CacheObject) {
                                            setValue(Resource.success(t))
                                        }
                                    })
                                }
                            })
                        }
                    })
                }
                is ApiEmptyResponse -> {
                    appExecutors.mainThread()!!.execute(object: Runnable{
                        override fun run() {
                            results.addSource(loadFromDb(), object: Observer<CacheObject>{
                                override fun onChanged(t: CacheObject) {
                                    setValue(Resource.success(t))
                                }
                            })
                        }
                    })
                }
                is ApiErrorResponse -> {
                    results.addSource(dbSource, object: Observer<CacheObject>{
                        override fun onChanged(cacheObject: CacheObject) {
                            setValue(
                                Resource.error(response.errorMessage, cacheObject)
                            )
                        }
                    })
                }
            }
        }
    }

    private fun setValue(newValue: Resource<CacheObject>) {
        if (results.value != newValue) {
            results.value = newValue
        }
    }

    protected open fun processResponse(response: ApiSuccessResponse<RequestObject>) = response.body

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestObject)

    @MainThread
    protected abstract fun shouldFetch(data: CacheObject?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<CacheObject>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestObject>>

    fun asLiveData(): LiveData<Resource<CacheObject>> {
        return results
    }
}