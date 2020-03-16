// IGNORE_BACKEND: JVM_IR, JS, JS_IR, NATIVE
// IGNORE_BACKEND_FIR: JVM_IR
// FILE: test.kt

fun checkEqual(x: Any, y: Any) {
    if (x != y || y != x) throw AssertionError("$x and $y should be equal")
    if (x.hashCode() != y.hashCode()) throw AssertionError("$x and $y should have the same hash code")
}

fun checkNotEqual(x: Any, y: Any) {
    if (x == y || y == x) throw AssertionError("$x and $y should NOT be equal")
}

class V {
    fun target(vararg x: String) {}
}

private fun captureTwoParams(f: (V, String, String) -> Unit): Any = f
private fun captureOneParam(f: (V, String) -> Unit): Any = f
private fun captureNoParams(f: (V) -> Unit): Any = f
private fun captureVarargAsArray(f: (V, Array<String>) -> Unit): Any = f

private fun captureTwoParamsBound(f: (String, String) -> Unit): Any = f
private fun captureOneParamBound(f: (String) -> Unit): Any = f
private fun captureNoParamsBound(f: () -> Unit): Any = f
private fun captureVarargAsArrayBound(f: (Array<String>) -> Unit): Any = f

fun box(): String {
    val v0 = V()

    checkEqual(captureTwoParams(V::target), captureTwoParams(V::target))
    checkEqual(captureOneParam(V::target), captureOneParam(V::target))
    checkEqual(captureNoParams(V::target), captureNoParams(V::target))
    checkEqual(captureNoParams(V::target), captureNoParamsFromOtherFile())
    checkEqual(captureVarargAsArray(V::target), captureVarargAsArrayFromOtherFile())

    checkEqual(captureTwoParamsBound(v0::target), captureTwoParamsBound(v0::target))
    checkEqual(captureOneParamBound(v0::target), captureOneParamBound(v0::target))
    checkEqual(captureNoParamsBound(v0::target), captureNoParamsBound(v0::target))
    checkEqual(captureNoParamsBound(v0::target), captureNoParamsBoundFromOtherFile(v0))
    checkEqual(captureVarargAsArrayBound(v0::target), captureVarargAsArrayBoundFromOtherFile(v0))


    checkNotEqual(captureTwoParams(V::target), captureNoParams(V::target))
    checkNotEqual(captureTwoParamsBound(v0::target), captureNoParamsBound(v0::target))

    checkNotEqual(captureTwoParams(V::target), captureVarargAsArray(V::target))
    checkNotEqual(captureOneParam(V::target), captureVarargAsArray(V::target))
    checkNotEqual(captureNoParams(V::target), captureVarargAsArray(V::target))
    checkNotEqual(captureOneParamBound(v0::target), captureVarargAsArrayBound(v0::target))
    checkNotEqual(captureOneParamBound(v0::target), captureVarargAsArrayBoundFromOtherFile(v0))

    return "OK"
}

// FILE: fromOtherFile.kt

private fun captureNoParams(f: (V) -> Unit): Any = f
private fun captureNoParamsBound(f: () -> Unit): Any = f
private fun captureVarargAsArray(f: (V, Array<String>) -> Unit): Any = f
private fun captureVarargAsArrayBound(f: (Array<String>) -> Unit): Any = f

fun captureNoParamsFromOtherFile(): Any = captureNoParams(V::target)
fun captureNoParamsBoundFromOtherFile(v0: V): Any = captureNoParamsBound(v0::target)
fun captureVarargAsArrayFromOtherFile(): Any = captureVarargAsArray(V::target)
fun captureVarargAsArrayBoundFromOtherFile(v0: V): Any = captureVarargAsArrayBound(v0::target)
