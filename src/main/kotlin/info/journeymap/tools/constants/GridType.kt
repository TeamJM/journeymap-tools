package info.journeymap.tools.constants

enum class GridType(val type: String) {
    NONE("None"),
    LINES("Lines");

    override fun toString(): String {
        return this.type
    }
}
