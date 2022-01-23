package info.journeymap.tools.models

import info.journeymap.tools.constants.MapType
import tornadofx.isInt
import java.io.File

class Dimension(val directory: File) {
    var id: String = this.directory.name
    var mapTypes: List<MapType> = listOf()
    var layers: List<Int> = listOf()

    init {

        val layers: MutableList<Int> = mutableListOf()
        val mapTypes: MutableList<MapType> = mutableListOf()

        this.directory.listFiles()!!.filter { it.isDirectory && it.list()!!.isNotEmpty() }.forEach {
            if (it.name.isInt()) {
                layers.add(it.name.toInt())
            } else {
                when (it.name) {
                    "day"   -> mapTypes.add(MapType.SURFACE_DAY)
                    "night" -> mapTypes.add(MapType.SURFACE_NIGHT)
                    "topo"  -> mapTypes.add(MapType.SURFACE_TOPO)
                }
            }
        }

        if (layers.isNotEmpty()) {
            mapTypes.add(MapType.UNDERGROUND)
        }

        this.layers = layers.sorted()
        this.mapTypes = mapTypes.toList()
    }

    fun getLayerDirectory(layer: Int): File? {
        if (this.layers.contains(layer)) {
            return this.directory.resolve(layer.toString())
        }

        return null
    }

    fun getMapDirectory(type: MapType): File? {
        if (this.mapTypes.contains(type)) {
            return when (type) {
                MapType.SURFACE_DAY   -> this.directory.resolve("day")
                MapType.SURFACE_NIGHT -> this.directory.resolve("night")
                MapType.SURFACE_TOPO  -> this.directory.resolve("topo")

                else                  -> null
            }
        }

        return null
    }

    override fun toString(): String {
        return when (this.id) {
            "overworld"  -> "Dim ${this.id} (Overworld)"
            "the_end"    -> "Dim ${this.id} (The End)"
            "the_nether" -> "Dim ${this.id} (Nether)"

            else         -> "Dim ${this.id}"
        }
    }
}
