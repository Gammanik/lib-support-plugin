import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiFactory

@DslMarker
annotation class LibSupportDslMarker

@LibSupportDslMarker
class DslBuilder {
    val commands = mutableListOf<Any>()

    fun build(): Map<String, Any> {
        val inspections =  mutableSetOf<Inspection<out KtElement>>() // todo: in or out?
        val lineMarkers = mutableMapOf<String, LineMarker>()

        val resMap = HashMap<String, Any>()
        commands.forEach{
            when (it) {
                is Inspection<*> -> inspections.add(it)
                is LineMarker -> lineMarkers[it.fqName!!] = it
            }
        }

        resMap["inspections"] = inspections
        resMap["lineMarkers"] = lineMarkers
        return resMap
    }
}

data class Inspection<K : KtElement>(
    var defaultFixText: String? = null,
    var applyTo: KtPsiFactory.(element: K, project: Project, editor: Editor?) -> Unit,
    var isApplicable: ((element: K) -> Boolean),
    var kClass: Class<K>
)

class InspectionBuilder<K : KtElement> {
    var defaultFixText: String? = null
    lateinit var applyTo: KtPsiFactory.(element: K, project: Project, editor: Editor?) -> Unit
    lateinit var isApplicable: ((K) -> Boolean)
    lateinit var kClass: Class<K> // Class<K : KtElement>
    var inspectionText : ((K) -> String)? = null // todo
    val inspectionHighlightType: (element: K) -> ProblemHighlightType = { _: K -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING }
    fun build() = Inspection<K>(defaultFixText, applyTo, isApplicable, kClass)
}

fun <K : KtElement> DslBuilder.addApplicableInspection(inspection: InspectionBuilder<K>.() -> Unit) {
    commands.add(InspectionBuilder<K>().apply(inspection).build())
}

data class LineMarker(
    var fqName: String? = null,
    var message: String? = null,
    var icon: String? = null
)

class LineMarkerBuilder {
    var fqName: String? = null
    var message: String? = null
    var icon: String? = null
    fun build() = LineMarker(fqName, message, icon)
}

fun DslBuilder.addLineMarkerProvider(marker: LineMarkerBuilder.() -> Unit) {
    commands.add(LineMarkerBuilder().apply(marker).build())
}

fun libSupport(commands: DslBuilder.() -> Unit): Map<String, Any> =
    DslBuilder().apply(commands).build()
