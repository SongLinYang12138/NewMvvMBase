package com.bondex.photo.test

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import com.bondex.library.app.PhotoApplication
import com.bondex.library.base.BaseModel
import com.bondex.library.base.BaseViewMode
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation


/**
 * date: 2020/4/26
 * @Author: ysl
 * description:
 */
class TestViewModel : BaseViewMode<TestModel>() {
    val tv = ObservableField<String>()

    val et2 = ObservableField<String>()
    val et3 = ObservableField<String>()
    val et4 = ObservableField<String>()
    val editObserver = EditObserver()

    fun onSaveClick(view: View?) {

//        et2.set("et2")
//        et3.set("et3")
//        et4.set("et4")
        Log.i("aaa", " et2 " + et2.get() + "  et3 " + et3.get() + " et4 " + et4.get())
//        netWork()
//testBlock()
//        Toast.makeText(PhotoApplication.getContext(), "点击", Toast.LENGTH_SHORT).show()
    }

    fun testBlock() = runBlocking {


        Log.i("aaa", "1")
        launch(Dispatchers.Default) {
            Log.i("aaa", "2")

        }
        Log.i("aaa", "3")

    }

    fun netWork() {
        GlobalScope.launch {
            doAnotier()
        }
//        Log.i("aaa", "4")
//        GlobalScope.launch(Dispatchers.IO) {
//            Log.i("aaa", "do netWork1 " + Thread.currentThread().name)
//
////            launch(Dispatchers.Main) {
////                Log.i("aaa", "do netWork2 " + Thread.currentThread().name)
////
////            }
//
//
////            val deferred = async {
////                Log.i("aaa", "do netWork async  " + Thread.currentThread().name)
////
////                return@async 2222
////            }
////            Log.i("aaa","do async ")
////
////            deferred.await()
//
//        }


    }

    suspend fun doAnotier() {
//        val defered: Deferred<String> = GlobalScope.async {
//
//            Log.i("aaa", "do saync")
//
//            return@async "23"
//        }

//        Log.i("aaa", "deferd " + defered.await())
        val result = withContext(Dispatchers.Main) {
            Log.i("aaa", "withcontent " + Thread.currentThread().name)
            return@withContext 1
        }


    }


    override fun onResume() {

    }

    override fun onStop() {
    }

    override fun onCreate() {

        tv.set("mytv1")
    }

    override fun attachUi() {
    }

    override fun detachUi() {
    }

    override fun onStart() {
    }

    override fun onDestroy() {
    }

    override fun setMyModel() {

    }
}