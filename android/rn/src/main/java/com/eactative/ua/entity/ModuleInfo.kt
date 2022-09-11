package com.eactative.ua.entity

data class ModuleInfo(
    var appID: String,
    var moduleID: String,
    var version: Int,
    /**
     * "Android"
     */
    var os: String,
    /**
     * "RN"
     */
    var agent: String,
    var script: Array<Source>,
    var ttf: Array<Source>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModuleInfo

        if (appID != other.appID) return false
        if (moduleID != other.moduleID) return false
        if (version != other.version) return false
        if (os != other.os) return false
        if (agent != other.agent) return false
        if (!script.contentEquals(other.script)) return false
        if (!ttf.contentEquals(other.ttf)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = appID.hashCode()
        result = 31 * result + moduleID.hashCode()
        result = 31 * result + version
        result = 31 * result + os.hashCode()
        result = 31 * result + agent.hashCode()
        result = 31 * result + script.contentHashCode()
        result = 31 * result + ttf.contentHashCode()
        return result
    }
}
