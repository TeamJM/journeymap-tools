package info.journeymap.tools.views

import info.journeymap.tools.constants.GridType
import info.journeymap.tools.constants.MainViewStyle
import info.journeymap.tools.constants.MapType
import info.journeymap.tools.constants.WorldType
import info.journeymap.tools.controllers.MainController
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import tornadofx.*

class MainView : View() {
    val controller: MainController by inject()

    override val root = vbox {
        this.paddingAll = 10
        this.spacing = 10.0

        // Minecraft directory input
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = 10.0

            label("Minecraft Directory")

            textfield(controller.minecraftDirectoryPath) {
                hgrow = Priority.ALWAYS

                bindClass(controller.textInputClass)

                setOnAction { validatePath() }
                tooltipProperty().bind(controller.textInputTooltip)

                focusedProperty().addListener { _, _, newValue ->
                    if (!newValue) {
                        validatePath()
                    }
                }
            }

            button("Browse") {
                hgrow = Priority.ALWAYS
                minWidth = 50.0
            }.action {
                var openingDirectory = controller.minecraftDirectory

                if (openingDirectory != null && (!openingDirectory.exists() || !openingDirectory.isDirectory)) {
                    openingDirectory = null
                }

                val directory = chooseDirectory("Select Minecraft Directory", openingDirectory)
                controller.minecraftDirectory = directory

                validatePath()
            }
        }

        separator {  }

        // Map settings
        gridpane {
            alignment = Pos.CENTER_LEFT
            hgap = 10.0
            vgap = 10.0
            useMaxWidth = true

            (0..3).forEach {
                constraintsForColumn(it).percentWidth = when {
                    (it % 2) == 0 -> 20.0
                    else -> 30.0
                }
            }

            row { // Row 1
                label("World Type")
                combobox<WorldType>(controller.worldTypeProperty(), controller.worldTypes) {
                    useMaxWidth = true

                    enableWhen(controller.textInputValid)
                }

                label("World")
                combobox<String>() {
                    useMaxWidth = true

                    enableWhen(controller.textInputValid)
                }
            }
            row { // Row 2
                label("Map Type")
                combobox<MapType>(controller.mapTypeProperty(), controller.mapTypes) {
                    useMaxWidth = true

                    enableWhen(controller.textInputValid)

                }

                label("Dimension")
                combobox<String>() {
                    useMaxWidth = true

                    enableWhen(controller.textInputValid)
                }
            }
            row { // Row 3
                label("Surface Layer")
                spinner<Number>(property = controller.layer, min = 0, max = 15) {
                    useMaxWidth = true

                    enableWhen(controller.textInputValid.and(controller.mapTypeProperty().isEqualTo(MapType.UNDERGROUND)))
                }

                label("Grid")
                combobox<GridType>(controller.gridTypeProperty(), controller.gridTypes) {
                    useMaxWidth = true

                    enableWhen(controller.textInputValid)
                }
            }
        }

        separator {  }

        // Progress/action button
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = 10.0

            progressbar(0.0) {
                hgrow = Priority.ALWAYS
                useMaxWidth = true
            }
            button("Export") {
                hgrow = Priority.ALWAYS
            }
        }
    }

    init {
        setStageIcon(Image(MainView::class.java.classLoader.getResourceAsStream("jm.png")))
        currentStage?.isResizable = false

        this.setWindowMinSize(600, 230)
        this.title = "JourneyMap Tools"

        this.validatePath()
    }

    fun validatePath() {
        if (!this.controller.isValidMinecraftDirectory()) {
            this.controller.textInputValid.set(false)
            this.controller.textInputTooltip.set(Tooltip("Unable to find a valid JourneyMap data directory"))
        } else {
            this.controller.textInputValid.set(true)
            this.controller.textInputTooltip.set(null)
        }
    }
}
