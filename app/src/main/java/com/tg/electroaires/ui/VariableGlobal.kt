package com.tg.electroaires.ui

class VariableGlobal private constructor() {
    var nombreUsuario: String? = null

    companion object {
        private var instance: VariableGlobal? = null

        fun getInstance(): VariableGlobal {
            if (instance == null) {
                instance = VariableGlobal()
            }
            return instance as VariableGlobal
        }
    }
}