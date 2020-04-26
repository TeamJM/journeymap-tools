package info.journeymap.tools.models

import info.journeymap.tools.constants.WorldType
import javafx.beans.property.SimpleObjectProperty
import tornadofx.getProperty
import tornadofx.onChange
import tornadofx.property
import java.io.File

class MinecraftDirectory {
    var directory: File? by property<File?>(null)
    fun directoryProperty() = getProperty(MinecraftDirectory::directory)

    val jmDirectory: File?
        get() = directory?.resolve("journeymap")
    val dataDirectory: File?
        get() = jmDirectory?.resolve("data")

    var worldTypes: List<WorldType> = listOf()
    var worlds: Map<WorldType, List<World>> = mapOf()

    fun validateDirectory(): Boolean {
        return when {
            this.directory == null            -> false
            !this.directory!!.exists()        -> false
            !this.directory!!.isDirectory     -> false

            this.jmDirectory == null          -> false
            !this.jmDirectory!!.exists()      -> false
            !this.jmDirectory!!.isDirectory   -> false

            this.dataDirectory == null        -> false
            !this.dataDirectory!!.exists()    -> false
            !this.dataDirectory!!.isDirectory -> false

            else                              -> true
        }
    }

    init {
        this.directoryProperty().onChange {
            this.loadData()
        }
    }

    fun loadData() {
        if (!this.validateDirectory()) {
            return
        }

        // Check given world types and list their worlds

        val worldTypes: MutableList<WorldType> = mutableListOf()
        val worlds: MutableMap<WorldType, List<World>> = mutableMapOf()

        WorldType.values().forEach {
            if (this.checkWorldDir(it)) {
                worldTypes.add(it)
                worlds[it] = this.getWorlds(it)
            }
        }

        this.worldTypes = worldTypes.toList()
        this.worlds = worlds.toMap()
    }

    fun getWorldDir(type: WorldType): File? {
        return when (type) {
            WorldType.SINGLE_PLAYER        -> this.dataDirectory?.resolve("sp")
            WorldType.MULTI_PLAYER_ONLINE  -> this.dataDirectory?.resolve("mp")
            WorldType.MULTI_PLAYER_OFFLINE -> this.dataDirectory?.resolve("mp/offline")
        }
    }

    fun getWorlds(type: WorldType): List<World> {
        val worldList: MutableList<World> = mutableListOf()

        if (this.checkWorldDir(type)) {
            val dir = this.getWorldDir(type)!!
            val files: List<File> = when (type) {
                WorldType.MULTI_PLAYER_ONLINE -> dir.listFiles()!!.filter { it.name != "offline" }
                else                           -> dir.listFiles()!!.asList()
            }

            files.forEach { worldList.add(World(it)) }
        }

        return worldList
    }

    fun checkWorldDir(type: WorldType): Boolean {
        val dir = this.getWorldDir(type)
        val count: Int

        if (dir == null || !dir.exists() || !dir.isDirectory) {
            return false
        }

        count = if (type == WorldType.MULTI_PLAYER_ONLINE) {
            dir.list()!!.filter { it != "offline" }.count()
        } else {
            dir.list()!!.count()
        }

        return count > 0
    }
}