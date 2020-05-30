package com.example.todozzz.ui.masterList

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.todozzz.database.MasterListDao
import com.example.todozzz.database.MasterListEntity
import com.example.todozzz.formatTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MasterListViewModel(
    val database: MasterListDao,
    application: Application) : AndroidViewModel(application) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val allTasks = database.getAllTasks()

    val taskString = Transformations.map(allTasks) { allTasks ->
        formatTasks(allTasks, application.resources)
    }

    fun addTask(task: String) {
        uiScope.launch {
            var newTask = MasterListEntity(taskInfo = task)
            insert(newTask)
        }
    }
    private suspend fun insert(task: MasterListEntity) {
        withContext(Dispatchers.IO) {
            database.insert(task)
        }
    }
    fun clearTasks() {
        uiScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}