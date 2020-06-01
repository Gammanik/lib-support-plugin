package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.PathManager.getPluginsPath
import com.intellij.openapi.components.service
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.impl.libraries.LibraryEx
import com.intellij.openapi.roots.impl.libraries.LibraryImpl
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtil.toSystemIndependentName
import com.intellij.openapi.vfs.JarFileSystem
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.idea.util.projectStructure.allModules
import org.jetbrains.kotlin.jsr223.KotlinJsr223JvmScriptEngine4Idea
import org.jetbrains.kotlin.jsr223.KotlinJsr223StandardScriptEngineFactory4Idea
import java.io.File
import java.io.InputStreamReader
import java.io.Reader
import javax.script.ScriptContext
import kotlin.script.experimental.jvm.util.KotlinJars

class UpdateAction : AnAction() {
    private val service = ProjectManager.getInstance().defaultProject.service<services.CommandsRegService>()
    private val listener = MyLibChangeListener()

    override fun actionPerformed(e: AnActionEvent) { //todo: use  val s = p.service<MethodRegService>()
        val module = ModuleRootManager.getInstance(e.project?.allModules()!![2])
        val orderEntries: Array<OrderEntry> = module.orderEntries
        registerLibraries(orderEntries)
    }

    private fun registerLibraries(orderEntries: Array<OrderEntry>) {
        orderEntries.filterIsInstance<LibraryOrderEntry>()
            .forEach {
                if (it.library is LibraryImpl) {
                    (it.library as LibraryImpl).addRootSetChangedListener(listener)
                }

                val libJars = it.library!!.getFiles(OrderRootType.CLASSES)
                if (libJars.isNotEmpty()) {
                    service.findAndRunKtsConfig(libJars[0].path)
                }
            }
    }

    inner class MyLibChangeListener: RootProvider.RootSetChangedListener {
        override fun rootSetChanged(wrapper: RootProvider) {
            val libJars = wrapper.getFiles(OrderRootType.CLASSES)
            println("root changed: ${wrapper.javaClass} ${libJars}")
            val libRootPath = libJars[0].path
            if (libJars.isNotEmpty()) {
                service.findAndRunKtsConfig(libRootPath)
            }
        }

    }
}

