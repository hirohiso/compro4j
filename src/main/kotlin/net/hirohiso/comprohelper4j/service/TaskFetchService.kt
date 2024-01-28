package net.hirohiso.comprohelper4j.service

import net.hirohiso.comprohelper4j.type.SampleIO
import org.jsoup.Connection
import org.jsoup.Jsoup

class TaskFetchService {
    fun fetch(url:String): List<SampleIO> {
        println(url)
        val doc = Jsoup.connect(url).method(Connection.Method.POST).get()
        val elements = doc.select("span.lang-ja>div.io-style~div.part>section")
        val list = elements.stream().map { e -> e.select("pre") }.toList()
            //.forEach{e -> println(e.text())}

        return list.chunked(2).map{ pair ->
            val input = pair[0].text()
            val output = pair[1].text()
            val sampleIO = SampleIO(input,output)
            sampleIO
        }
    }
}