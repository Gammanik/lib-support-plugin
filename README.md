# lib-support-plugin
plugin for custom kotlin libraries support in IDE


### Add icon
Syntax for adding an icon on some library method call:
```kotlin
addLineMarkerProvider {
    fqName = "LibExampleClass.method1"
    message = "icon message"
    icon = "/plugins/icons/RetryIcon"
}
```
Plugin registers a LineMarker on every `method1()` invocation. \
The result:

![icon](https://user-images.githubusercontent.com/15042786/86406056-20000580-bcbb-11ea-9a30-5e758fec9c83.png)
![icon2](https://user-images.githubusercontent.com/15042786/86406130-3b6b1080-bcbb-11ea-9b1e-93835dd79e35.png)



### Add custom inspection
Exapmle of inspection: if a fuction' return type is Unit and it does not have the suspend keyword, then it's marked as an error. The suspend keyword is added if inspection is applied.
```kotlin
addApplicableInspection<KtNamedFunction> {
    defaultFixText = "try to fix it!"
    inspectionText = { f -> "add suspend keyword to: $f"}
    kClass = KtNamedFunction::class.java
    applyTo = { f: KtNamedFunction, project: Project, editor: Editor? ->
        f.addModifier(KtTokens.SUSPEND_KEYWORD)
    }

    isApplicable = { f: KtNamedFunction ->
        !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
                f.resolveToDescriptorIfAny()?.run {
                    !isSuspend && !isSuspendLambdaOrLocalFunction()
                    && returnType?.isUnit()!!
                } == true
    }
}
```
![insp1](https://user-images.githubusercontent.com/15042786/86418401-a545e300-bcd8-11ea-8e4a-a01e600cdcde.png)


### Full example of *settings.kts* file

Locate your script with settings in `META-INF/lib-support/settings.kts`. \
Example of `settings.kts` file:

```kotlin
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction

libSupport {
    addApplicableInspection<KtNamedFunction> {
        defaultFixText = "fix text"
        kClass = KtNamedFunction::class.java
        applyTo = { f: KtNamedFunction, project: Project, editor: Editor? ->
            f.addModifier(KtTokens.SUSPEND_KEYWORD)
        }

        isApplicable = { f: KtNamedFunction ->
            !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
                    f.resolveToDescriptorIfAny()?.run { !isSuspend && !isSuspendLambdaOrLocalFunction() }
        }
    }

    addLineMarkerProvider {
        fqName = "LibExampleClass.method1"
        message = "icon message"
        icon = "/plugins/icons/RetryIcon"
    }
}
```
