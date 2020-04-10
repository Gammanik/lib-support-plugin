import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import org.apache.log4j.Level
import org.jetbrains.kotlin.jsr223.KotlinJsr223StandardScriptEngineFactory4Idea
import java.io.File
import java.io.FileReader
import java.io.Reader
import javax.script.ScriptEngine


class ShowPsiAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val lg: Logger = Logger.getInstance(ShowPsiAction::class.java)
        lg.setLevel(Level.ALL)

        @Suppress("UnstableApiUsage")
        val engine: ScriptEngine = KotlinJsr223StandardScriptEngineFactory4Idea().scriptEngine

        val p: Project = e.project!!

        ModuleRootManager.getInstance(ModuleManager.getInstance(p).modules[0]).sourceRoots
        val script = File(p.basePath + "/src/main/resources/support/s1.kts")
        val reader: Reader = FileReader(script)
        val res = engine.eval(reader)
        Messages.showMessageDialog(res.toString(), "title", Messages.getInformationIcon())
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }

}

