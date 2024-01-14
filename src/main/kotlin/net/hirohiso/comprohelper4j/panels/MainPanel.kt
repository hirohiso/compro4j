package net.hirohiso.comprohelper4j.panels

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.GridLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class MainPanel : ToolWindowFactory,DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val main = JPanel()
        val titleLabel = JLabel("settings")

        //Main.javaの設定
        val settingMainFilePanel = JPanel()
        settingMainFilePanel.add(JLabel("Main.java Path"))
        settingMainFilePanel.add(JTextField("src/Main.java"))
        //出力先設定
        val settingOutputPanel = JPanel()
        settingOutputPanel.add(JLabel("Output Path"))
        settingOutputPanel.add(JTextField("out/comprohelper/build"))
        //問題管理
        val taskManagerPanel = JPanel()
        BoxLayout(taskManagerPanel,BoxLayout.Y_AXIS)
        // 1. 問題取得
        val taskFetchPanel = JPanel()
        taskFetchPanel.add(JTextField("http://xxxxxx"))
        val fetchButton = JButton("取得").apply {
            addActionListener {
                val taskContentPanel = JPanel()
                taskContentPanel.add(JLabel("問題"))
                taskManagerPanel.add(taskContentPanel)
                taskManagerPanel.revalidate()
            }
        }
        taskFetchPanel.add(
            fetchButton
        )
        taskManagerPanel.add(taskFetchPanel)
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