package net.hirohiso.comprohelper4j.service.execute

import net.hirohiso.comprohelper4j.type.TaskResult

sealed class ExecuteResult {
    data class OK(val list: List<TaskResult>) : ExecuteResult()
    data class Error(val reason: ExecuteError) : ExecuteResult()
}


enum class ExecuteError(val message: String){
    SdkNotFound("Sdkのパスが取得できませんでした"),
    MainClassNotFound("Main.javaが取得できませんでした")
}