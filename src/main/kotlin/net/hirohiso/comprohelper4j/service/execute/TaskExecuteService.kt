package net.hirohiso.comprohelper4j.service.execute

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import net.hirohiso.comprohelper4j.type.SampleIO
import net.hirohiso.comprohelper4j.type.TaskResult
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.util.LinkedList
import java.util.concurrent.TimeUnit

class TaskExecuteService {
    fun execute(mainClass : String, output: String, project: Project,tasks : List<SampleIO>): ExecuteResult{

        //SDKのパスを取得
        val sdk = ProjectRootManager.getInstance(project).projectSdk
        val homepath = sdk?.homePath
            ?:return ExecuteResult.Error(ExecuteError.SdkNotFound)

        //Mainクラスのパスを取得
        val mainPath = File(project.basePath, mainClass)
        if (!mainPath.exists()) {
            return ExecuteResult.Error(ExecuteError.MainClassNotFound)
        }

        //classファイルの出力場所を取得
        val outPath = File(project.basePath, output)
        outPath.mkdirs()

        //ビルド(javac Main.java　の実行)
        val javacPath = sdk.homePath + File.separator + "bin" + File.separator + "javac"
        runCommand(project, javacPath,mainPath.absolutePath, "-d", outPath.absolutePath)

        //Mainクラスの実行(java Mainの実行)
        val javaPath = sdk.homePath + File.separator + "bin" + File.separator + "java"

        val result =  tasks.map{
                (input, expect) ->
            val actual = execJava(project,input,javaPath,"-cp", outPath.absolutePath ,"Main")
            val t1 = expect.trim().split("\\s+".toRegex())
            val t2 = actual.trim().split("\\s+".toRegex())
            TaskResult(SampleIO(input,expect),t1 == t2)
        }.toList()
        return ExecuteResult.OK(result)
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
        //todo タイムアウト設定
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
        //5秒間待つ
        val exitCode = process.waitFor(5,TimeUnit.SECONDS)
        return output
    }
}