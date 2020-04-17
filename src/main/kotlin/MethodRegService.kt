import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

class MethodRegService(project: Project?) {
    companion object {
        fun getInstance(project: Project): MethodRegService {
            return ServiceManager.getService(project, MethodRegService::class.java)
        }
    }

    // полностью хранить квалифицированное имя java.lang.
    private var methodsToMark: Map<Pair<String, String>, MethodToMark> = mapOf()

    fun updateMarkedMethods(methods: Map<Pair<String, String>, MethodToMark>) {
        methodsToMark = methods
    }

    fun getMarkedMethods(): Map<Pair<String, String>, MethodToMark> {
        return methodsToMark
    }

}