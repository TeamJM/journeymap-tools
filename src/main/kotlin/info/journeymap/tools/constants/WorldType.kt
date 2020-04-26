package info.journeymap.tools.constants

enum class WorldType(val type: String) {
    SINGLE_PLAYER("Single-Player"),
    MULTI_PLAYER_ONLINE("Multi-Player (Online)"),
    MULTI_PLAYER_OFFLINE("Multi-Player (Offline)");

    override fun toString(): String {
        return this.type
    }
}
