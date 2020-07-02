import com.intellij.jsp.impl.FunctionDescriptor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.j2k.ast.UnitType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.types.typeUtil.isUnit

class MyInspection
    : AbstractApplicabilityBasedInspection<KtNamedFunction>(KtNamedFunction::class.java) {
    override val defaultFixText: String
        get() = "try to fix it!"

    override fun applyTo(element: KtNamedFunction, project: Project, editor: Editor?) {
        element.addModifier(KtTokens.SUSPEND_KEYWORD)
    }

    override fun inspectionText(element: KtNamedFunction): String {
        return "fix function ${element.toString()}"
    }

    override fun isApplicable(element: KtNamedFunction): Boolean {
        return !element.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
                element.resolveToDescriptorIfAny()?.run {
                    !isSuspend && !isSuspendLambdaOrLocalFunction()
                    && returnType?.isUnit()!!
                } == true
    }
}
