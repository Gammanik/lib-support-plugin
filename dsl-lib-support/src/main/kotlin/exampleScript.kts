//import com.intellij.openapi.editor.Editor
//import com.intellij.openapi.project.Project
//import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
//import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
//import org.jetbrains.kotlin.lexer.KtTokens
//import org.jetbrains.kotlin.psi.KtNamedFunction
//
//libSupport {
//    addApplicableInspection<KtNamedFunction> {
//        defaultFixText = "fix text"
//        kClass = KtNamedFunction::class.java
//        applyTo = { f: KtNamedFunction, project: Project, editor: Editor? ->
//            f.addModifier(KtTokens.SUSPEND_KEYWORD)
//        }
//
//        isApplicable = { f: KtNamedFunction ->
//            !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
//                    f.resolveToDescriptorIfAny()?.run {
//                        !isSuspend && !isSuspendLambdaOrLocalFunction()
//                    } == true
//        }
//    }
//
//    addLineMarkerProvider {
//        fqName = "LibExampleClass.method1"
//        message = "icon message"
//        icon = "/plugins/icons/IconName"
//    }
//}
