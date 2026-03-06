package com.example.chordcraft.components
import com.chaquo.python.Python
import org.json.JSONObject

fun callPythonJSON(module: String, attr: String): JSONObject {
    val python = Python.getInstance()
    val module = python.getModule(module)
    val result = module.callAttr(attr).toString()
    return JSONObject(result)
}

fun callPythonJSON(module: String, attr: String, parameterValue: String): JSONObject {
    val python = Python.getInstance()
    val module = python.getModule(module)
    val result = module.callAttr(attr, parameterValue).toString()
    return JSONObject(result)
}