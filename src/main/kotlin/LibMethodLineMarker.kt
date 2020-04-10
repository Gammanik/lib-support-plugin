import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
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
        val dotExpressions = elements.filterIsInstance<KtDotQualifiedExpression>()

        for (r in dotExpressions) {
            val refExpr: PsiElement? = r.firstChild?.lastChild
            val callExpr: PsiElement? = r.lastChild?.firstChild?.firstChild

            if (refExpr != null && refExpr is LeafPsiElement && refExpr.text == "LibClassOne") {
                if (callExpr != null && callExpr is LeafPsiElement && callExpr.text == "sMethodOne") {
                    val ic : Icon = AllIcons.Javaee.UpdateRunningApplication
                    val builder = NavigationGutterIconBuilder
                        .create(ic)
                        .setTarget(callExpr)
                        .setTooltipText("text from DSL")
                    result.add(builder.createLineMarkerInfo(callExpr))
                }
            }



        }
    }



}
