import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.pom.Navigatable


class PopupDialogAction : AnAction() {

    // Using the event, evaluate the context, and enable or disable the action.
    override fun update(e: AnActionEvent) {
        // Set the availability based on whether a project is open
        // Set the availability based on whether a project is open
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }

    // Using the event, implement an action. For example, create and show a dialog.
    override fun actionPerformed(e: AnActionEvent) {
        // Using the event, create and show a dialog
        val currentProject: Project = e.project!!
        val dlgMsg = StringBuffer(e.presentation.text.toString() + " Selected!")
        val dlgTitle: String = e.presentation.description
        // If an element is selected in the editor, add info about it.
        val nav: Navigatable? = e.getData(CommonDataKeys.NAVIGATABLE)!!
        if (nav != null) {
            dlgMsg.append(java.lang.String.format("\nSelected Element: %s", nav.toString()))
        }
        Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon())
    }
}