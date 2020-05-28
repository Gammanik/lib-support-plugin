import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.PathManager.getPluginsPath
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderEntry
import com.intellij.openapi.roots.OrderRootType
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

class ShowPsiAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) { //todo: use  val s = p.service<MethodRegService>()
        val module = ModuleRootManager.getInstance(e.project?.allModules()!![2])
        val orderEntries: Array<OrderEntry> = module.orderEntries

        orderEntries.filterIsInstance<LibraryOrderEntry>()
            .forEach {
                val libJars = it.library!!.getFiles(OrderRootType.CLASSES)
                val libRoot = JarFileSystem.getInstance().findFileByPath(libJars[0].path)
                val script = libRoot?.findChild("META-INF")
                    ?.findChild("lib-support")?.findChild("settings.kts")

                if (script != null) {
                    updateMarkedMethods(InputStreamReader(script.inputStream))
                }
            }
    }

    private fun updateMarkedMethods(scriptFile: Reader) {
        val engine = getEngine()
        val res: Map<String, Any> = withCorrectClassLoader { engine.eval(scriptFile) as Map<String, Any> }
//        val service = ProjectManager.getInstance().defaultProject.service<CommandsRegService>()
//        service.updateMarkedMethods(res)
        Messages.showMessageDialog("$res", "title", Messages.getInformationIcon())
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
        val dslPath = toSystemIndependentName(getPluginsPath() + "/lib-support/lib/dsl-lib-support.jar")
        val ktJar = toSystemIndependentName(System.getProperty("user.home") +
                "/.gradle/caches/modules-2/files-2.1/com.jetbrains.intellij.idea/ideaIC/2019.3.3/4c54deba9ff34a615b3072cd2def3558ff462987/ideaIC-2019.3.3/plugins/Kotlin/lib/kotlin-plugin.jar")
        val scriptDeps = mutableListOf(File(ktJar), File(dslPath))
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
}

