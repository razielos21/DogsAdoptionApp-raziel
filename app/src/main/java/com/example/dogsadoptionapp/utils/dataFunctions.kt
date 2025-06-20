package com.example.dogsadoptionapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers

fun <T, A> performFetchingAndSaving(dbFetch: () -> LiveData<T>,
                                    remoteDBFetch: suspend () -> Resource<A>,
                                    dbSave: suspend (A) -> Unit): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())

        val source = dbFetch().map{ Resource.success(it) }
        emitSource(source)

        val fetchResource = remoteDBFetch()

        if (fetchResource.status is Success)
            dbSave(fetchResource.status.data!!)

        else if (fetchResource.status is Error) {
            emit(Resource.error(fetchResource.status.message!!))
            emitSource(source)

        }
    }