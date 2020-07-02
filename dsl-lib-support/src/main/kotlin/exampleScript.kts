//import com.intellij.openapi.editor.Editor
//import com.intellij.openapi.project.Project
//import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
//import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
//import org.jetbrains.kotlin.lexer.KtTokens
//import org.jetbrains.kotlin.psi.KtNamedFunction
//import org.jetbrains.kotlin.types.typeUtil.isUnit
//
//libSupport {
//    addLineMarkerProvider {
//        fqName = "LibExampleClass.method1"
//        message = "icon message"
//        icon = "/plugins/icons/IconName"
//    }
//
//    addApplicableInspection<KtNamedFunction> {
//        defaultFixText = "try to fix it!"
//        inspectionText = { f -> "add suspend keyword to: $f"}
//        kClass = KtNamedFunction::class.java
//        applyTo = { f: KtNamedFunction, project: Project, editor: Editor? ->
//            f.addModifier(KtTokens.SUSPEND_KEYWORD)
//        }
//
//        isApplicable = { f: KtNamedFunction ->
//            !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
//                    f.resolveToDescriptorIfAny()?.run {
//                        !isSuspend && !isSuspendLambdaOrLocalFunction()
//                        && returnType?.isUnit()!!
//                    } == true
//        }
//    }
//
//
//}
