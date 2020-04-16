import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import dsl.MethodToMark
import org.apache.log4j.Level
import org.jetbrains.kotlin.jsr223.KotlinJsr223JvmScriptEngine4Idea
import org.jetbrains.kotlin.jsr223.KotlinJsr223StandardScriptEngineFactory4Idea
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.util.jar.Attributes
import java.util.jar.JarFile
import javax.script.Invocable
import javax.script.ScriptEngine
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.toScriptSource

class ShowPsiAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        //todo: use  val s = p.service<MethodRegService>()
        val s = ProjectManager.getInstance().defaultProject.service<MethodRegService>()
        s.updateMarkedMethods(mapOf(Pair("LibClassOne", "sMethodOne")
                to MethodToMark("LibClassOne", "sMethodOne")))
        Messages.showMessageDialog("sup", "title", Messages.getInformationIcon())
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }

}

