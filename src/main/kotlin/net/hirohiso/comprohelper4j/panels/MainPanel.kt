package net.hirohiso.comprohelper4j.panels

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import net.hirohiso.comprohelper4j.service.execute.ExecuteResult
import net.hirohiso.comprohelper4j.service.fetch.FetchResult
import net.hirohiso.comprohelper4j.service.execute.TaskExecuteService
import net.hirohiso.comprohelper4j.service.fetch.TaskFetchService
import net.hirohiso.comprohelper4j.type.SampleIO
import java.util.*
import javax.swing.*

class MainPanel : ToolWindowFactory, DumbAware {

    private val fetchService = TaskFetchService()
    private val executeService = TaskExecuteService()
    private var taskList: List<SampleIO> = LinkedList()
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val main = JPanel()

        //Main.javaの設定
        val settingMainFilePanel = JPanel()
        settingMainFilePanel.layout = BoxLayout(settingMainFilePanel, BoxLayout.LINE_AXIS)
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
        settingOutputPanel.layout = BoxLayout(settingOutputPanel, BoxLayout.X_AXIS)

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
        taskManagerPanel.layout = BoxLayout(taskManagerPanel, BoxLayout.Y_AXIS)
        // 1. 問題取得
        val taskFetchPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
        }
        val urlField = JBTextField("").apply {
            val fonth = this.getFontMetrics(this.font).height
            val dim = this.maximumSize
            dim.height = fonth + 10
            maximumSize = dim
            emptyText.text = "contest page url:ex)https://atcoder.jp/contests/abs/tasks/practice_1"
        }
        taskFetchPanel.add(urlField)
        val taskContentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }
        //taskContentPanel.add(JLabel("問題"))

        val fetchButton = JButton("取得").apply {
            addActionListener {
                //ここに取得処理を記載する
                urlField.text.let {
                    when (val result = fetchService.fetch(it)) {
                        is FetchResult.OK -> {
                            taskList = result.list
                            showSuccess("Success", "loaded contest test case")
                        }

                        is FetchResult.Error -> showError("ERROR", result.reason.message)
                    }

                }
            }
        }
        val execButton = JButton("実行").apply {
            addActionListener {
                if (taskList.isEmpty()) {
                    showError("No Test", "please load contest test case")
                    return@addActionListener
                }
                when (val result = executeService.execute(
                    mainpath.text,
                    outpath.text,
                    project,
                    taskList
                )) {
                    is ExecuteResult.Error -> showError(
                        "ERROR",
                        result.reason.message
                    )

                    is ExecuteResult.OK ->
                        if (result.list.all { it -> it.result }) {
                            showSuccess("AC", "All testcase passed")
                        } else {
                            result.list.forEach {
                                if (!it.result) {
                                    showError("WA", it.sample.input)
                                }
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
        val content = ContentFactory.getInstance().createContent(main, "", false)
        toolWindow.contentManager.addContent(content)

    }

    private fun showError(title: String, message: String) {
        println(message)
        val notice = Notification(
            "comprohelper4j",
            title,
            message,
            NotificationType.ERROR
        )
        Notifications.Bus.notify(notice)
    }

    private fun showSuccess(title: String, message: String) {
        println(message)
        val notice = Notification(
            "comprohelper4j",
            title,
            message,
            NotificationType.INFORMATION
        )
        Notifications.Bus.notify(notice)
    }
}