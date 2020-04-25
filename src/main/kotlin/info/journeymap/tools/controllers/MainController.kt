package info.journeymap.tools.controllers

import info.journeymap.tools.constants.GridType
import info.journeymap.tools.constants.MainViewStyle
import info.journeymap.tools.constants.MapType
import info.journeymap.tools.constants.WorldType
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.scene.control.Tooltip
import tornadofx.Controller
import tornadofx.CssRule
import tornadofx.getProperty
import tornadofx.property
import java.io.File
import java.util.concurrent.Callable

class MainController : Controller() {
    // region: Settings

    val minecraftDirectoryPath = SimpleStringProperty(getDefaultMinecraftPath())
    var minecraftDirectory: File?
        get() = File(minecraftDirectoryPath.value ?: "")
        set(value) = minecraftDirectoryPath.set(value?.path)

    var mapType: MapType by property<MapType>(MapType.SURFACE_DAY)
    fun mapTypeProperty() = getProperty(MainController::mapType)

    var gridType: GridType by property<GridType>(GridType.NONE)
    fun gridTypeProperty() = getProperty(MainController::gridType)

    var worldType: WorldType by property<WorldType>(WorldType.SINGLE_PLAYER)
    fun worldTypeProperty() = getProperty(MainController::worldType)

    val dimension = SimpleIntegerProperty(0)
    val layer = SimpleIntegerProperty(0)
    val world = SimpleStringProperty()

    // endregion
    // region: Contained data

    val gridTypes = SimpleListProperty(FXCollections.observableArrayList(GridType.values().toList()))
    val mapTypes = SimpleListProperty(FXCollections.observableArrayList(MapType.values().toList()))
    val worldTypes = SimpleListProperty(FXCollections.observableArrayList(WorldType.values().toList()))

    // endregion
    // region: Component bindings

    val textInputTooltip = SimpleObjectProperty<Tooltip>(null)
    val textInputValid = SimpleBooleanProperty(true)

    val textInputClass: ObjectBinding<CssRule> = Bindings.createObjectBinding(
            Callable {
                when (textInputValid.value) {
                    false -> MainViewStyle.red
                    else  -> MainViewStyle.reset
                }
            },
            textInputValid
    )

    // endregion

    fun getDefaultMinecraftPath(): String {
        val operatingSystem = System.getProperty("os.name").toUpperCase()

        val baseDirectory = if (operatingSystem.contains("WIN")) {
            System.getenv("APPDATA")
        } else {
            System.getProperty("user.home")
        }

        return "${baseDirectory}${File.separator}.minecraft"
    }

    fun isValidMinecraftDirectory(): Boolean {
        val minecraftDirectory = this.minecraftDirectory
        val journeyMapDirectory = minecraftDirectory?.resolve("journeymap")
        val dataDirectory = journeyMapDirectory?.resolve("data")

        return when {
            minecraftDirectory == null       -> false
            !minecraftDirectory.exists()     -> false
            !minecraftDirectory.isDirectory  -> false

            journeyMapDirectory == null      -> false
            !journeyMapDirectory.exists()    -> false
            !journeyMapDirectory.isDirectory -> false

            dataDirectory == null            -> false
            !dataDirectory.exists()          -> false
            !dataDirectory.isDirectory       -> false

            else                             -> true
        }
    }
}
