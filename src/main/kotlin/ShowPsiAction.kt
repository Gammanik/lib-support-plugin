import com.esotericsoftware.minlog.Log
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiFile
import org.apache.log4j.Level
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngineManager

class ShowPsiAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        setIdeaIoUseFallback()
        val lg: Logger = Logger.getInstance(ShowPsiAction::class.java)
        val engine: ScriptEngine = ScriptEngineManager().getEngineByExtension("kts")!!

//        val engineFactory: ScriptEngineFactory = KotlinJsr223JvmLocalScriptEngineFactory()
//        val engine: ScriptEngine = engineFactory.scriptEngine
        print("eng: $engine")
        val res = engine.eval("2 + 2")
        print("eval: $res")
        lg.trace("ee: $res $engine")


        val currentProject: Project = e.project!!
        val dlgMsg = StringBuffer(e.presentation.text.toString() + " Selected!")
        val dlgTitle = "description" // e.presentation.description

        // If an element is selected in the editor, add info about it.
        val nav: Navigatable? = e.getData(CommonDataKeys.NAVIGATABLE)!!
        if (nav != null) {
            dlgMsg.append(java.lang.String.format("\nSelected Element: %s", nav.toString()))
        }
        dlgMsg.append("\n\n")
        val psiFile : PsiFile? = e.getData(LangDataKeys.PSI_FILE)
        if (psiFile != null) {
            dlgMsg.append(psiFile.toString())
        }
        dlgMsg.append(psiFile)
        Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon())
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }

}

