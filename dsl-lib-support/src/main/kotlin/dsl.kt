import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiFactory

@DslMarker
annotation class LibSupportDslMarker

data class ScriptResult(
    val inspections: List<Inspection<in KtElement>>,
    val lineMarkers: Map<String, LineMarker>
)

@LibSupportDslMarker
class DslBuilder {
    val commands = mutableListOf<Any>()

    fun build(): ScriptResult {
        val inspections =  mutableListOf<Inspection<in KtElement>>()
        val lineMarkers = mutableMapOf<String, LineMarker>()
        commands.forEach{
            when (it) {
                is Inspection<*> -> inspections.add(it as Inspection<in KtElement>)
                is LineMarker -> lineMarkers[it.fqName] = it
            }
        }
        return ScriptResult(inspections, lineMarkers)
    }
}

data class Inspection<K : KtElement>(
    var defaultFixText: String? = null,
    var applyTo: KtPsiFactory.(element: K, project: Project, editor: Editor?) -> Unit,
    var isApplicable: ((element: K) -> Boolean),
    var inspectionHighlightType: (element: K) -> ProblemHighlightType,
    var kClass: Class<K>
)

class InspectionBuilder<K : KtElement> {
    var defaultFixText: String? = null
    lateinit var applyTo: KtPsiFactory.(element: K, project: Project, editor: Editor?) -> Unit
    lateinit var isApplicable: ((K) -> Boolean)
    lateinit var kClass: Class<K>
    var inspectionText : ((K) -> String)? = null
    val inspectionHighlightType: (element: K) -> ProblemHighlightType = { _: K -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING }
    fun build() = Inspection<K>(defaultFixText, applyTo, isApplicable, inspectionHighlightType, kClass)
}

fun <K : KtElement> DslBuilder.addApplicableInspection(inspection: InspectionBuilder<K>.() -> Unit) {
    commands.add(InspectionBuilder<K>().apply(inspection).build())
}

data class LineMarker(
    val fqName: String,
    var message: String? = null,
    var icon: String? = null
)

class LineMarkerBuilder {
    lateinit var fqName: String
    var message: String? = null
    var icon: String? = null
    fun build() = LineMarker(fqName, message, icon)
}

fun DslBuilder.addLineMarkerProvider(marker: LineMarkerBuilder.() -> Unit) {
    commands.add(LineMarkerBuilder().apply(marker).build())
}

fun libSupport(commands: DslBuilder.() -> Unit): ScriptResult =
    DslBuilder().apply(commands).build()
