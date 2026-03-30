package com.example.chordcraft.components

import com.chaquo.python.Python
import org.json.JSONObject

fun callPythonJSON(module: String, attr: String, parameterValue: String): JSONObject {
    try {
        val python = Python.getInstance()
        val module = python.getModule(module)
        val result = module.callAttr(attr, parameterValue).toString()
        return JSONObject(result)
    } catch(e: Exception) {
        throw Exception("The local Chord Extraction Model encountered an error.\nTry again shortly, or use an alternative model.\n${e.message}")
    }
}