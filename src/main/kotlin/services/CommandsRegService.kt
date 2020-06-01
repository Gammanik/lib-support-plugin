package services

import CommandTypes
import Inspection
import LineMarker
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.JarFileSystem
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.jsr223.KotlinJsr223JvmScriptEngine4Idea
import org.jetbrains.kotlin.jsr223.KotlinJsr223StandardScriptEngineFactory4Idea
import org.jetbrains.kotlin.psi.KtElement
import java.io.File
import java.io.InputStreamReader
import java.io.Reader
import javax.script.ScriptContext
import kotlin.script.experimental.jvm.util.KotlinJars

class CommandsRegService(project: Project?) {
    companion object {
        fun getInstance(project: Project): CommandsRegService {
            return ServiceManager.getService(project, CommandsRegService::class.java)
        }
    }

    private var engine: KotlinJsr223JvmScriptEngine4Idea? = null
    private var commands: Map<String, Any> = mapOf()

    fun getInspections(): Set<Inspection<in KtElement>> {
        @Suppress("UNCHECKED_CAST")

        (return if (this.commands["inspections"] != null) {
            this.commands["inspections"] as Set<Inspection<in KtElement>>
        } else {
            emptySet()
        })
    }

    fun getMarkedMethods(): Map<String, LineMarker>? {
        return commands[CommandTypes.LINE_MARKERS.toString()] as Map<String, LineMarker>?
    }

    private fun updateFromScript(scriptFile: Reader): Map<String, Any> {
        if (engine == null) { engine = getEngine() }

        val res: Map<String, Any> = withCorrectClassLoader { engine!!.eval(scriptFile) as Map<String, Any> }
        for (commandType in CommandTypes.values()) {
            val newCommandsOfType = res[commandType.toString()]

            if (newCommandsOfType != null) {
                commands[commandType.toString()]
            }
        }

        return res
    }

    private fun <T>withCorrectClassLoader(action: () -> T) : T {
        val res: T
        val oldClassLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = this.javaClass.classLoader
        try {
            res = action()
        } finally {
            Thread.currentThread().contextClassLoader = oldClassLoader
        }
        return res
    }

    private fun getEngine(): KotlinJsr223JvmScriptEngine4Idea {
        val dslJarPath =PathManager.getPluginsPath() + "/lib-support/lib/dsl-lib-support.jar"
        val kotlinPluginJarPath = System.getProperty("user.home") +
                "/.gradle/caches/modules-2/files-2.1/com.jetbrains.intellij.idea/ideaIC/2019.3.3/4c54deba9ff34a615b3072cd2def3558ff462987/ideaIC-2019.3.3/plugins/Kotlin/lib/kotlin-plugin.jar"
        val ideaApiJarPath = System.getProperty("user.home") +
                "/.gradle/caches/modules-2/files-2.1/com.jetbrains.intellij.idea/ideaIC/2019.3.3/4c54deba9ff34a615b3072cd2def3558ff462987/ideaIC-2019.3.3/lib/platform-api.jar"


        val scriptDeps: List<File> = listOf(kotlinPluginJarPath, dslJarPath, ideaApiJarPath)
            .map { File(FileUtil.toSystemIndependentName(it)) }
        val jarNames: List<File> = KotlinJars.kotlinScriptStandardJars + scriptDeps

        val factory = KotlinJsr223StandardScriptEngineFactory4Idea()
        return KotlinJsr223JvmScriptEngine4Idea(
            factory,
            jarNames,
            "kotlin.script.templates.standard.ScriptTemplateWithBindings",
            { ctx, argTypes -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), argTypes ?: emptyArray()) },
            arrayOf(Map::class)
        )
    }

    fun findAndRunKtsConfig(libRootVfsPath: String): Map<String, Any> {
        val libRoot = JarFileSystem.getInstance().findFileByPath(libRootVfsPath)
        val script = libRoot?.findChild("META-INF")
            ?.findChild("lib-support")?.findChild("settings.kts")

        if (script != null) {
            val res = updateFromScript(InputStreamReader(script.inputStream))
            Messages.showMessageDialog("$res", "title", Messages.getInformationIcon())
            return res
        }

        return emptyMap()
    }

}