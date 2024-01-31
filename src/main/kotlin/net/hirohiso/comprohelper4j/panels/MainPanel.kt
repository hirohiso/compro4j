package net.hirohiso.comprohelper4j.panels

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import net.hirohiso.comprohelper4j.service.TaskExecuteService
import net.hirohiso.comprohelper4j.service.TaskFetchService
import net.hirohiso.comprohelper4j.type.SampleIO
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.util.*
import javax.swing.*

class MainPanel : ToolWindowFactory,DumbAware {

    val fetchService = TaskFetchService()
    val executeService = TaskExecuteService()
    var taskList :List<SampleIO> = LinkedList()
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val main = JPanel()

        //Main.javaの設定
        val settingMainFilePanel = JPanel()
        settingMainFilePanel.layout = BoxLayout(settingMainFilePanel,BoxLayout.LINE_AXIS)
        var labelMain = JLabel("Main.java Path")
        val mainpath = JTextField("src/Main.java").apply {
            val fonth = this.getFontMetrics(this.font).height
            val dim = this.maximumSize
            dim.height = fonth + 10
            maximumSize = dim
        }
        settingMainFilePanel.add(labelMain)
        settingMainFilePanel.add(mainpath)

        //出力先設定
        val settingOutputPanel = JPanel()
        settingOutputPanel.layout = BoxLayout(settingOutputPanel,BoxLayout.X_AXIS)

        val outpath = JTextField("out/comprohelper/build").apply {
            val fonth = this.getFontMetrics(this.font).height
            val dim = this.maximumSize
            dim.height = fonth + 10
            maximumSize = dim
        }

        settingOutputPanel.add(JLabel("Output Path"))
        settingOutputPanel.add(outpath)
        //問題管理
        val taskManagerPanel = JPanel()
        taskManagerPanel.layout = BoxLayout(taskManagerPanel,BoxLayout.Y_AXIS)
        // 1. 問題取得
        val taskFetchPanel = JPanel().apply {
            layout = BoxLayout(this,BoxLayout.X_AXIS)
        }
        val urlField = JTextField("https://atcoder.jp/contests/abs/tasks/practice_1").apply {
            val fonth = this.getFontMetrics(this.font).height
            val dim = this.maximumSize
            dim.height = fonth + 10
            maximumSize = dim
        }
        taskFetchPanel.add(urlField)
        val taskContentPanel = JPanel().apply {
            layout = BoxLayout(this,BoxLayout.Y_AXIS)
        }
        //taskContentPanel.add(JLabel("問題"))

        val fetchButton = JButton("取得").apply {
            addActionListener {
                //ここに取得処理を記載する
                urlField.text.let{
                    taskList = fetchService.fetch(it)
                    val notice = Notification(
                        "Success",
                        "Success",
                        "loaded contest test case",
                        NotificationType.INFORMATION
                    )
                    Notifications.Bus.notify(notice)
                }
            }
        }
        val execButton = JButton("実行").apply {
            addActionListener {
                if(taskList.isEmpty()){
                    val notice = Notification(
                        "WARNIG",
                        "No Test",
                        "please load contest test case",
                        NotificationType.WARNING
                    )
                    Notifications.Bus.notify(notice)
                    return@addActionListener
                }
                val result = executeService.execute(
                    mainpath.text,
                    outpath.text,
                    project,
                    taskList
                )

                if(result.all { it -> it.result }){
                    val notice = Notification(
                        "Result",
                        "AC",
                        "All testcase passed",
                        NotificationType.INFORMATION
                    )
                    Notifications.Bus.notify(notice)
                }else{
                    result.forEach {
                        if(!it.result){
                            val notice = Notification(
                                "Result",
                                "WA",
                                it.sample.input,
                                NotificationType.ERROR
                            )
                            Notifications.Bus.notify(notice)
                        }
                    }
                }
            }
        }
        taskFetchPanel.add(
            fetchButton
        )
        taskContentPanel.add(execButton)
        taskManagerPanel.add(taskFetchPanel)
        taskManagerPanel.add(taskContentPanel)
        //2.問題

        main.layout = BoxLayout(main, BoxLayout.Y_AXIS)
        main.add(settingMainFilePanel)
        main.add(Box.createVerticalStrut(10))
        main.add(settingOutputPanel)
        main.add(Box.createVerticalStrut(10))
        main.add(taskManagerPanel)
        val content = ContentFactory.getInstance().createContent(main,"",false)
        toolWindow.contentManager.addContent(content)

    }
}