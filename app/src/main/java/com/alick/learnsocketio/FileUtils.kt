package com.alick.learnsocketio

import android.content.Context
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/**
 * @author 崔兴旺
 * @description
 * @date 2022/7/6 23:26
 */
class FileUtils {
    companion object {
        fun writeLog(context: Context, fileName: String, log: String) {
            val file = File(context.getExternalFilesDir("log"), fileName)
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                file.createNewFile()
            }

            val bf = BufferedWriter(OutputStreamWriter(FileOutputStream(file, true), "utf-8"))
            bf.write("${TimeUtils.getCurrentTime()}-----" + log + "\n")
            // 此处不添加关闭流，在文件中打开是看不到内容的
            bf.close()
        }
    }
}