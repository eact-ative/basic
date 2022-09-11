package com.eactative.resource

import android.content.Context
import android.os.Environment
import android.util.Log
import com.eactative.common.FileUtil
import com.eactative.resource.entity.ResoureEntity
import com.eactative.resource.entity.ResoureEntity_
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ResourceManager {
    companion object {
        val CacheDir = "resource"
        suspend fun getResource(context: Context, url: String): String? {
            val resourceFolder = mkdirp(context, CacheDir)
            val resourceBox = ObjectBox.store.boxFor(ResoureEntity::class.java)
            val query = resourceBox.query(ResoureEntity_.url.equal(url)).build()
            val results = query.find()
            query.close()
            if(results.isNotEmpty()) {
                val resource = results[0]
                if(resource != null) {
                    if(File(resource.path).exists()) {
                        val hash = FileUtil.getFileMD5(File(resource.path))
                        if(hash == resource.hashCode) {
                            return resource.path
                        }
                    }
                    // hash code not match
                    // or resource.path not exist
                    // clean record
                    resourceBox.remove(resource.id)
                }
            }
            // download
            val fileName = download(url, resourceFolder)
            if(fileName.isEmpty()) {
                return null
            }
            val resourcePath = resourceFolder + File.separator + fileName
            val hashCode = FileUtil.getFileMD5(File(resourcePath))
            // 0 (zero): Objects with an ID of zero (and null if the ID is of type Long) are considered new (not persisted before).
            // Putting such an object will always insert a new object and assign an unused ID to it
            val resource = ResoureEntity(0, url, resourcePath, hashCode, "") // todo: cacheCtrl policy
            resourceBox.put(resource)
            return resourcePath

        }

        private fun mkdirp(context: Context, path: String): String {
            // /storage/emulated/0/Android/data/包名/files/test
            var resourceFolder = context.filesDir.path + File.separator + path
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val file = context.getExternalFilesDir(path)
                if (file != null) {
                    resourceFolder = file.path
                }
            }
            return resourceFolder
        }

        private suspend fun download(url: String, parentPath: String) = suspendCoroutine<String> {
            val filename = UUID.randomUUID().toString()
            val task = DownloadTask.Builder(url, File(parentPath))
                .setFilename(filename) // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(30) // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build()

            task.enqueue(object: DownloadListener {
                override fun taskStart(task: DownloadTask) {

                }

                override fun connectTrialStart(
                    task: DownloadTask,
                    requestHeaderFields: MutableMap<String, MutableList<String>>
                ) {
                }

                override fun connectTrialEnd(
                    task: DownloadTask,
                    responseCode: Int,
                    responseHeaderFields: MutableMap<String, MutableList<String>>
                ) {
                }

                override fun downloadFromBeginning(
                    task: DownloadTask,
                    info: BreakpointInfo,
                    cause: ResumeFailedCause
                ) {
                }

                override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
                }

                override fun connectStart(
                    task: DownloadTask,
                    blockIndex: Int,
                    requestHeaderFields: MutableMap<String, MutableList<String>>
                ) {
                }

                override fun connectEnd(
                    task: DownloadTask,
                    blockIndex: Int,
                    responseCode: Int,
                    responseHeaderFields: MutableMap<String, MutableList<String>>
                ) {
                }

                override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
                }

                override fun fetchProgress(
                    task: DownloadTask,
                    blockIndex: Int,
                    increaseBytes: Long
                ) {
                }

                override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
                }

                override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
                    when (cause) {
                        EndCause.COMPLETED -> it.resume(filename)
                        else -> {
                            Log.e(Constants.TAG, "download fail: $cause $realCause")
                            it.resume("")
                        }
                    }
                }
            })
        }
    }
}