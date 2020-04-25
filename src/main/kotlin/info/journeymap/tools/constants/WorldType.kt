package info.journeymap.tools.constants

enum class WorldType(val type: String) {
    SINGLE_PLAYER("Single-Player"),
    MULTI_PLAYER("Multi-Player");

    override fun toString(): String {
        return this.type
    }
}
