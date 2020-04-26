package info.journeymap.tools.models

import info.journeymap.tools.constants.MapType
import javafx.collections.transformation.SortedList
import tornadofx.isInt
import java.io.File

class Dimension(val directory: File) {
    var id: Int = 0
    var mapTypes: List<MapType> = listOf()
    var layers: List<Int> = listOf()

    init {
        this.id = this.directory.name.substring(3).toInt()

        val layers: MutableList<Int> = mutableListOf()
        val mapTypes: MutableList<MapType> = mutableListOf()

        this.directory.list()!!.forEach {
            if (it.isInt()) {
                layers.add(it.toInt())
            } else {
                when (it) {
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
            0    -> "Dim ${this.id} (Overworld)"
            1    -> "Dim ${this.id} (The End)"
            -1   -> "Dim ${this.id} (Nether)"

            else -> "Dim ${this.id}"
        }
    }
}
