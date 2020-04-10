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
        @Suppress("UnstableApiUsage")
        val dotExpressions = elements.filterIsInstance<KtDotQualifiedExpression>()

        val s = ProjectManager.getInstance().defaultProject.service<MethodRegService>()
        val ms = s.getMarkedMethods()

        for (r in dotExpressions) {
            val refExpr: PsiElement? = r.firstChild?.lastChild
            val callExpr: PsiElement? = r.lastChild?.firstChild?.firstChild

            val methodToMark = ms.get(Pair(refExpr?.text, callExpr?.text))

            if (methodToMark != null && refExpr != null && refExpr is LeafPsiElement) {
                if (callExpr != null && callExpr is LeafPsiElement) {
                    val ic : Icon = AllIcons.Javaee.UpdateRunningApplication
                    val builder = NavigationGutterIconBuilder
                        .create(ic)
                        .setTarget(callExpr)
                        .setTooltipText(methodToMark.name!!)
                    result.add(builder.createLineMarkerInfo(callExpr))
                }
            }



        }
    }



}
