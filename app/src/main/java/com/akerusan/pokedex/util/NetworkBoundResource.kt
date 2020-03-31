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

// everything done generically so can be used for other projects
abstract class NetworkBoundResource<CacheObject, RequestObject>(private val appExecutors: AppExecutors) {

    // data observed in the UI
    // mediatorLivedData => can add multiple sources
    private var results: MediatorLiveData<Resource<CacheObject>> = MediatorLiveData()

    init {
        // update live data for loading status
        results.value = Resource.loading(null) as Resource<CacheObject>
        // observe live data from local DB
        @Suppress("LeakingThis")
        val dbSource: LiveData<CacheObject> = loadFromDb()

        results.addSource(dbSource, object: Observer<CacheObject>{
            override fun onChanged(data: CacheObject) {
                // remove so it stops listening, goal was to trigger onChanged
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

    /**
     * 1. Observe local db
     * 2. if <condition> query the network
     * 2. stop observing the local db
     * 4. insert new data into local db
     * 5. begin observing local db again to see the refreshed data from network
     */
    private fun fetchFromNetwork(dbSource: LiveData<CacheObject>) {
        val apiResponse = createCall()
        // update liveData for loading status
        results.addSource(dbSource, object: Observer<CacheObject>{
            override fun onChanged(t: CacheObject) {
                setValue(Resource.loading(t))
            }
        })
        results.addSource(apiResponse) { response ->
            results.removeSource(dbSource)
            results.removeSource(apiResponse)

            when (response) {
                // is = "instance of"
                is ApiSuccessResponse -> {
                    appExecutors.diskIO()!!.execute(object: Runnable{
                        override fun run() {
                            // save the response to the local db
                            // by default room needs all operations done to db on background threads
                            saveCallResult(processResponse(response))
                            // set new value (setValue method) to result list, need to be done on main thread
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

    // setValue on main thread, if postValue then background thread
    // setValue set immediately, postValue post when it has resources to do so
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

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class
    fun asLiveData(): LiveData<Resource<CacheObject>> {
        return results
    }
}