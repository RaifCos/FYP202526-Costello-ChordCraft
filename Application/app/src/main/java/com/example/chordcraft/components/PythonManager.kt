package com.example.chordcraft.components
import com.chaquo.python.Python
import org.json.JSONObject

fun callPythonReturn(module: String, parameterValue: String): JSONObject {
    val python = Python.getInstance()
    val module = python.getModule(module)
    val result = module.callAttr("main", parameterValue).toString()
    return JSONObject(result)
}