package dsl

data class MethodToMark(
    var className: String? = null,
    var name: String? = null,
    var icon: String? = null
)

@DslMarker
annotation class LibSupportDslMarker

@LibSupportDslMarker
class MethodBuilder {
    var className: String? = null
    var name: String? = null
    fun build(): MethodToMark = MethodToMark(className, name)
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

    fun build(): Map<Pair<String, String>, MethodToMark> {
        val resMethodsMap = HashMap<Pair<String, String>, MethodToMark>()

        for (m in methods) {
            resMethodsMap.put(Pair(m.className!!, m.name!!), m)
        }

        return resMethodsMap
    }
}

fun methodsToMark(methods: MethodListBuilder.() -> Unit): Map<Pair<String, String>, MethodToMark> =
    MethodListBuilder().apply(methods).build()

