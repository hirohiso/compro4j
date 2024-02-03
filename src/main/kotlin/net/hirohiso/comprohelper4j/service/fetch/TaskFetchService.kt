package net.hirohiso.comprohelper4j.service.fetch

import net.hirohiso.comprohelper4j.type.SampleIO
import org.jsoup.Connection
import org.jsoup.Jsoup

class TaskFetchService {
    fun fetch(url: String): FetchResult {
        println(url)
        val doc = Jsoup.connect(url).method(Connection.Method.POST).get()
            ?:return FetchResult.Error(FetchError.ConnectFailed)
        val elements = doc.select("span.lang-ja>div.io-style~div.part>section")
            ?: return FetchResult.Error(FetchError.UnsupportedContestPage)

        val list = elements.stream().map { e -> e.select("pre") }.toList()
        //.forEach{e -> println(e.text())}

        return FetchResult.OK(
            list.chunked(2).map { pair ->
                val input = pair[0].text()
                val output = pair[1].text()
                val sampleIO = SampleIO(input, output)
                sampleIO
            }
        )
    }
}