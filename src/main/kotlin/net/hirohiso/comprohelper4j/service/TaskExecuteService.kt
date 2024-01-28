package net.hirohiso.comprohelper4j.service

import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import net.hirohiso.comprohelper4j.type.SampleIO
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter

class TaskExecuteService {
    fun execute(mainClass : String, output: String, project: Project,tasks : List<SampleIO>){

        //SDKのパスを取得
        val sdk = ProjectRootManager.getInstance(project).projectSdk
        val homepath = sdk?.homePath?:return;

        //Mainクラスのパスを取得
        val mainPath = File(project.basePath, mainClass)
        if (!mainPath.exists()) {
            return  showError("Mainクラス not Found")
        }

        //classファイルの出力場所を取得
        val outPath = File(project.basePath, output)
        outPath.mkdirs()

        //ビルド(javac Main.java　の実行)
        val javacPath = sdk.homePath + File.separator + "bin" + File.separator + "javac"
        runCommand(project, javacPath,mainPath.absolutePath, "-d", outPath.absolutePath)

        //Mainクラスの実行(java Mainの実行)
        val javaPath = sdk.homePath + File.separator + "bin" + File.separator + "java"

        tasks.forEach{
                (input, expect) ->
            val actual = execJava(project,input,javaPath,"-cp", outPath.absolutePath ,"Main")
            //todo 空白でsplitして各要素を比較する
            println("---actual---")
            println(actual)
            println("---expect---")
            println(expect)
        }
    }



    private fun showError(message:String){
        println(message)
    }

    private fun runCommand(project: Project, vararg args: String) {
        val pb = ProcessBuilder(*args)
        val process = pb.directory(File(project.basePath))
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        val exitCode = process.waitFor()
    }

    private fun execJava(project: Project, input: String, vararg args: String): String{
        val pb = ProcessBuilder(*args)
        val process = pb.directory(File(project.basePath))
            .start()
        PrintWriter(process.outputStream).use{
            it.println(input)
            it.flush()
        }
        val output = BufferedReader(InputStreamReader(process.inputStream)).use {
            it.readText()
        }
        val exitCode = process.waitFor()
        return output
    }
}