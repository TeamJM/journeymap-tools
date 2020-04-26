package info.journeymap.tools.controllers

import info.journeymap.tools.constants.GridType
import info.journeymap.tools.constants.MainViewStyle
import info.journeymap.tools.constants.MapType
import info.journeymap.tools.constants.WorldType
import info.journeymap.tools.models.Dimension
import info.journeymap.tools.models.MinecraftDirectory
import info.journeymap.tools.models.World
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.scene.control.Tooltip
import tornadofx.*
import java.io.File
import java.util.concurrent.Callable

class MainController : Controller() {
    // region: Settings

    val minecraftDirectoryPath = SimpleStringProperty(getDefaultMinecraftPath())
    var minecraftDirectory: File?
        get() = File(minecraftDirectoryPath.value ?: "")
        set(value) = minecraftDirectoryPath.set(value?.path)

    var mapType: MapType by property<MapType>(null)
    fun mapTypeProperty() = getProperty(MainController::mapType)

    var gridType: GridType by property<GridType>(GridType.NONE)
    fun gridTypeProperty() = getProperty(MainController::gridType)

    var worldType: WorldType by property<WorldType>(null)
    fun worldTypeProperty() = getProperty(MainController::worldType)

    val dimension = SimpleObjectProperty<Dimension>(null)
    val layer = SimpleObjectProperty<Int>(0)
    val world = SimpleObjectProperty<World>(null)

    val validDimensions = SimpleListProperty<Dimension>(FXCollections.observableArrayList())
    val validMapTypes = SimpleListProperty<MapType>(FXCollections.observableArrayList())
    val validLayers = SimpleListProperty<Int>(FXCollections.observableArrayList())
    val validWorldTypes = SimpleListProperty(FXCollections.observableArrayList(WorldType.values().toList()))
    val validWorlds = SimpleListProperty<World>(FXCollections.observableArrayList())

    // endregion
    // region: Contained data

    val directoryModel = MinecraftDirectory()

    val gridTypes = SimpleListProperty(FXCollections.observableArrayList(GridType.values().toList()))

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

    init {
        this.minecraftDirectoryPath.onChange {
            this.updateMinecraftDirectory()
            this.validateMinecraftDirectory()
        }

        this.worldTypeProperty().onChange {
            this.validWorlds.set(FXCollections.observableArrayList(this.directoryModel.worlds[this.worldType]  ?: listOf()))

            this.validDimensions.set(FXCollections.observableArrayList())
            this.validLayers.set(FXCollections.observableArrayList())
            this.validMapTypes.set(FXCollections.observableArrayList())

            this.world.set(null)
            this.dimension.set(null)
            this.mapTypeProperty().set(null)
            this.layer.set(0)
        }

        this.world.onChange {
            var dimension: List<Dimension> = listOf()

            val mapTypes: List<MapType> = listOf()
            val layers: List<Int> = listOf()

            this.dimension.set(null)
            this.mapTypeProperty().set(null)

            if (this.world.value != null) {
                dimension = this.world.value!!.dimensions
            }

            this.validDimensions.set(FXCollections.observableArrayList(dimension))
            this.validLayers.set(FXCollections.observableArrayList(layers))
            this.validMapTypes.set(FXCollections.observableArrayList(mapTypes))
        }

        this.dimension.onChange {
            var layers: List<Int> = listOf()
            var mapTypes: List<MapType> = listOf()

            if (this.dimension.value != null) {
                layers = this.dimension.value!!.layers
                mapTypes = this.dimension.value!!.mapTypes
            }

            this.mapTypeProperty().set(null)

            this.validLayers.set(FXCollections.observableArrayList(layers))
            this.validMapTypes.set(FXCollections.observableArrayList(mapTypes))
        }

        this.updateMinecraftDirectory()
        this.validateMinecraftDirectory()
    }

    fun getDefaultMinecraftPath(): String {
        val operatingSystem = System.getProperty("os.name").toUpperCase()

        val baseDirectory = if (operatingSystem.contains("WIN")) {
            System.getenv("APPDATA")
        } else {
            System.getProperty("user.home")
        }

        return "${baseDirectory}${File.separator}.minecraft"
    }

    fun validateMinecraftDirectory() {
        if (this.directoryModel.validateDirectory()) {
            this.textInputValid.set(true)
            this.textInputTooltip.set(null)
        } else {
            this.textInputValid.set(false)
            this.textInputTooltip.set(Tooltip("Unable to find a valid JourneyMap data directory"))
        }
    }

    fun updateMinecraftDirectory() {
        this.directoryModel.directoryProperty().set(this.minecraftDirectory)

        this.validWorldTypes.set(FXCollections.observableArrayList(this.directoryModel.worldTypes))
        this.validWorlds.set(FXCollections.observableArrayList(this.directoryModel.worlds[this.worldType] ?: listOf()))
    }
}
