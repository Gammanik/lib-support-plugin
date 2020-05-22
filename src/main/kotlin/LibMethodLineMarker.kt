import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import javax.swing.Icon


class LibMethodLineMarker: LineMarkerProvider {
    override fun collectSlowLineMarkers(
        elements: MutableList<PsiElement>,
        result: MutableCollection<LineMarkerInfo<PsiElement>>
    ) {
        // todo: do not use .defaultProject
        val service = ProjectManager.getInstance().defaultProject.service<CommandsRegService>()
        val markedMethodsTable = service.getMarkedMethods()["lineMarkers"] as Map<String, LineMarker>

        elements.filterIsInstance<KtQualifiedExpression>()
            .filter { it.lastChild?.firstChild != null
                    && it.lastChild?.firstChild is KtReferenceExpression}
            .forEach {
                val methodCall: KtReferenceExpression = it.lastChild?.firstChild as KtReferenceExpression
                val methodRefs = methodCall.references.filterIsInstance<KtSimpleNameReference>()

                for (ref: PsiReference in methodRefs) {
                    val fqMethodName = ref.resolve()?.getKotlinFqName().toString()
                    val methodToMark: LineMarker? = markedMethodsTable[fqMethodName]

                    if (methodToMark != null) {
                        val ic: Icon = AllIcons.Javaee.UpdateRunningApplication
                        val builder = NavigationGutterIconBuilder
                            .create(ic)
                            .setTarget(it)
                            .setTooltipText(methodToMark.message!!)
                        result.add(builder.createLineMarkerInfo(it))
                    }
                }
            }

    }


    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return null
    }
}
