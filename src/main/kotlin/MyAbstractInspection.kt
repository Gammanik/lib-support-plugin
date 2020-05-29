import com.intellij.codeInspection.*
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.idea.inspections.findExistingEditor
import org.jetbrains.kotlin.psi.KtPsiFactory

class MyAbstractInspection : AbstractKotlinInspection()  {

    companion object {
        private val service = ProjectManager.getInstance().defaultProject.service<services.CommandsRegService>()
        private val inspections: Set<Inspection<in KtElement>> = service.getInspections()
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession) =
        object : KtVisitorVoid() {
            override fun visitKtElement(element: KtElement) {
                super.visitKtElement(element)

                for(ins in inspections) {
                    if (!ins.kClass.isInstance(element) || element.textLength == 0) continue

                    visitTargetElement(element, holder, isOnTheFly, ins)
                }
            }
        }


    // This function should be called from visitor built by a derived inspection
    private fun visitTargetElement(element: KtElement, holder: ProblemsHolder, isOnTheFly: Boolean, ins: Inspection<in KtElement>) {
        if (!ins.isApplicable(element)) return

        holder.registerProblemWithoutOfflineInformation(
            element,
            ins.defaultFixText ?: "",
            isOnTheFly,
            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
            element.textRange, // todo: inspectionHighlightRangeInElement(element),
            LocalFix(ins.defaultFixText ?: "", ins) // todo: fixText(element)
        )
    }


    private inner class LocalFix(val text: String,val ins: Inspection<in KtElement>) : LocalQuickFix {
        override fun startInWriteAction() = true

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            @Suppress("UNCHECKED_CAST")
            val element = descriptor.psiElement as KtElement
            ins.applyTo(KtPsiFactory(project), element, project, element.findExistingEditor())
        }

        override fun getFamilyName() = ins.defaultFixText ?: ""

        override fun getName() = text
    }

}