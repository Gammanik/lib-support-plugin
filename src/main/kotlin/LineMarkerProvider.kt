import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiKeyword.WHILE
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiVariable
import com.intellij.psi.impl.PsiElementBase
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import java.util.*
import javax.swing.Icon


class LineMarkerProvider : RelatedItemLineMarkerProvider() {
    private fun findProperties(project: Project): List<PsiElement> {
        val result: MutableList<PsiElement> = mutableListOf()
        val virtualFiles: Collection<VirtualFile> = FilenameIndex.getAllFilesByExt(project, "kt")

        for (virtualFile: VirtualFile in virtualFiles) {
            val file: KtFile = PsiManager.getInstance(project).findFile(virtualFile) as KtFile
            val properties: Array<PsiElement> =
                PsiTreeUtil.getChildrenOfType(file, PsiElementBase::class.java) as Array<PsiElement>
            Collections.addAll(result, *properties)
        }
        return result
    }

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>) {

        if (element.toString() != "PsiElement(while)") {
            return
        }

        val ic : Icon = AllIcons.Icons.Ide.NextStepInverted
        val builder = NavigationGutterIconBuilder
            .create(ic)
            .setTarget(element)
        result.add(builder.createLineMarkerInfo(element))
    }
}