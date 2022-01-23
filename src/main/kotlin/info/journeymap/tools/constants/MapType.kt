package info.journeymap.tools.constants

enum class MapType(val type: String) {
    SURFACE_DAY("Surface (Day)"),
    SURFACE_NIGHT("Surface (Night)"),
    SURFACE_TOPO("Surface (Topographic)"),
    BIOME("Biome"),
    UNDERGROUND("Underground");

    override fun toString(): String {
        return this.type
    }
}
