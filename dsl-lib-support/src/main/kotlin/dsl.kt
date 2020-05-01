data class MethodToMark(
    var name: String? = null,
    var icon: String? = null
)

@DslMarker
annotation class LibSupportDslMarker

@LibSupportDslMarker
class MethodBuilder {
    var name: String? = null
    var icon: String? = null
    fun build(): MethodToMark = MethodToMark(name, icon)
}

@LibSupportDslMarker
class Methods: ArrayList<MethodToMark>() {
    fun method(methodBuilder: MethodBuilder.() -> Unit) {
        add(MethodBuilder().apply(methodBuilder).build())
    }
}

@LibSupportDslMarker
class MethodListBuilder {
    private val methods = mutableListOf<MethodToMark>()

    fun methods(methodsList: Methods.() -> Unit) {
        methods.addAll(Methods().apply(methodsList))
    }

    fun build(): Map<String, MethodToMark> {
        val resMethodsMap = HashMap<String, MethodToMark>()
        methods.forEach{ resMethodsMap.put(it.name!!, it) }
        return resMethodsMap
    }
}

fun methodsToMark(methods: MethodListBuilder.() -> Unit): Map<String, MethodToMark> =
    MethodListBuilder().apply(methods).build()
