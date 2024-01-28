package net.hirohiso.comprohelper4j.panels

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import net.hirohiso.comprohelper4j.service.TaskExecuteService
import net.hirohiso.comprohelper4j.service.TaskFetchService
import net.hirohiso.comprohelper4j.type.SampleIO
import java.awt.GridLayout
import java.util.*
import javax.swing.*

class MainPanel : ToolWindowFactory,DumbAware {

    val fetchService = TaskFetchService()
    val executeService = TaskExecuteService()
    var taskList :List<SampleIO> = LinkedList()
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {


        val main = JPanel()
        val titleLabel = JLabel("settings")

        //Main.javaの設定
        val settingMainFilePanel = JPanel()
        val mainpath = JTextField("src/Main.java")
        settingMainFilePanel.add(JLabel("Main.java Path"))
        settingMainFilePanel.add(mainpath)
        //出力先設定
        val settingOutputPanel = JPanel()
        val outpath = JTextField("out/comprohelper/build")
        settingOutputPanel.add(JLabel("Output Path"))
        settingOutputPanel.add(outpath)
        //問題管理
        val taskManagerPanel = JPanel()
        BoxLayout(taskManagerPanel,BoxLayout.Y_AXIS)
        // 1. 問題取得
        val taskFetchPanel = JPanel()
        val urlField = JTextField("https://atcoder.jp/contests/abs/tasks/practice_1")
        taskFetchPanel.add(urlField)
        val taskContentPanel = JPanel()
        taskContentPanel.add(JLabel("問題"))

        val fetchButton = JButton("実行").apply {
            addActionListener {
                //ここに取得処理を記載する
                urlField.text.let{
                    taskList = fetchService.fetch(it)
                    taskList.forEach { task -> println(task) }
                    executeService.execute(
                        mainpath.text,
                        outpath.text,
                        project,
                        taskList
                    )
                }
            }
        }
        taskFetchPanel.add(
            fetchButton
        )
        taskManagerPanel.add(taskFetchPanel)
        taskManagerPanel.add(taskContentPanel)
        //2.問題

        main.layout = GridLayout(4, 1)
        main.add(titleLabel)
        main.add(settingMainFilePanel)
        main.add(settingOutputPanel)
        main.add(taskManagerPanel)

        val content = ContentFactory.getInstance().createContent(main,"",false)
        toolWindow.contentManager.addContent(content)

    }
}