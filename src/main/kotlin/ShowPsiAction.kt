import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiFile

class ShowPsiAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val currentProject: Project = e.project!!
        // todo: PsiManager.getInstance(currentProject).addPsiTreeChangeListener()

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

