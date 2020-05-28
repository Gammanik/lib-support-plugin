@DslMarker
annotation class LibSupportDslMarker

@LibSupportDslMarker
class DslBuilder {
    val commands = mutableListOf<Any>()

    fun build(): Map<String, Any> {
        val inspections =  mutableSetOf<Inspection>()
        val lineMarkers = mutableMapOf<String, LineMarker>()

        val resMap = HashMap<String, Any>()
        commands.forEach{
            when (it) {
                is Inspection -> inspections.add(it)
                is LineMarker -> lineMarkers[it.fqName!!] = it
            }
        }

        resMap["inspections"] = inspections
        resMap["lineMarkers"] = lineMarkers
        return resMap
    }
}

data class Inspection(
    var defaultFixText: String? = null,
    var applyTo: Any? = null,
    var isApplicable: Any?,
    var kClass: Class<*>
)

class InspectionBuilder {
    var defaultFixText: String? = null
    var applyTo: Any? = null // KtPsiFactory.(element: K, project: Project, editor: Editor?) -> Unit
    var isApplicable: Any? = null // (element: K) -> Boolean
    lateinit var kClass: Class<*> // Class<K : KtElement>
    fun build() = Inspection(defaultFixText, applyTo, isApplicable, kClass)
}

fun DslBuilder.addApplicableInspection(inspection: InspectionBuilder.() -> Unit) {
    commands.add(InspectionBuilder().apply(inspection).build())
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
