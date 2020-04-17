import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.jsr223.KotlinJsr223JvmScriptEngine4Idea
import org.jetbrains.kotlin.jsr223.KotlinJsr223StandardScriptEngineFactory4Idea
import org.jetbrains.kotlin.script.util.scriptCompilationClasspathFromContextOrStlib
import java.io.File
import java.io.FileReader
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.script.experimental.jvm.util.KotlinJars

class ShowPsiAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) { //todo: use  val s = p.service<MethodRegService>()
        val jarNames: List<File> = listOf(File("/Users/Nikita/Documents/lib-support/dsl-lib-support/build/libs/dsl-lib-support.jar")) + scriptCompilationClasspathFromContextOrStlib(wholeClasspath = true) + KotlinJars.kotlinScriptStandardJars
        val factory = KotlinJsr223StandardScriptEngineFactory4Idea()
        val engine: ScriptEngine = KotlinJsr223JvmScriptEngine4Idea(
            factory,
            jarNames,
            "kotlin.script.templates.standard.ScriptTemplateWithBindings",
            { ctx, argTypes -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), argTypes ?: emptyArray()) },
            arrayOf(Map::class)
        )
        val script = FileReader(File(e.project!!.basePath + "/src/main/resources/support/example.kts"))
//        val res = engine.eval(script)

        val s = ProjectManager.getInstance().defaultProject.service<MethodRegService>()
        s.updateMarkedMethods(mapOf("LibExample.someMethod"
                to MethodToMark("LibClassOne", "sMethodOne")
        ))

//        if (res is Map<*, *>) {
//            val methods: MutableMap<Pair<String, String>, MethodToMark> = mutableMapOf()
//            for (m in res){
//                methods.put(m.key as Pair<String, String>, m.value as MethodToMark)
//            }
//
//            Messages.showMessageDialog(res.toString(), "title", Messages.getInformationIcon())
//        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }

}

