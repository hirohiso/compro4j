package net.hirohiso.comprohelper4j.service.fetch

import net.hirohiso.comprohelper4j.type.SampleIO

sealed class FetchResult {
    data class OK(val list: List<SampleIO>) : FetchResult()
    data class Error(val reason: FetchError): FetchResult()
}

enum class FetchError(val message : String) {
    ConnectFailed("接続に失敗しました"),
    UnsupportedContestPage("指定されたコンテストページは対応していません");
}
