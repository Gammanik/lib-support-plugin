import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import javax.swing.Icon


class LibMethodLineMarker: LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return null
    }

    override fun collectSlowLineMarkers(
        elements: MutableList<PsiElement>,
        result: MutableCollection<LineMarkerInfo<PsiElement>>
    ) {
        // todo: do not use defaultProject
        val s = ProjectManager.getInstance().defaultProject.service<MethodRegService>()
        val ms = s.getMarkedMethods()

        for (el in elements) {
            val ref = el.reference?.resolve()
            val name = ref?.getKotlinFqName()?.asString()
            val method = ms.get(name)

            if (method != null) {
                val ic : Icon = AllIcons.Javaee.UpdateRunningApplication
                    val builder = NavigationGutterIconBuilder
                        .create(ic)
                        .setTarget(el)
                        .setTooltipText(name!!)
                    result.add(builder.createLineMarkerInfo(el))
            }
        }

    }



}
