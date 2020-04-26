package info.journeymap.tools.views

import info.journeymap.tools.constants.GridType
import info.journeymap.tools.constants.MainViewStyle
import info.journeymap.tools.constants.MapType
import info.journeymap.tools.constants.WorldType
import info.journeymap.tools.controllers.MainController
import info.journeymap.tools.images.ImageStitcher
import info.journeymap.tools.models.Dimension
import info.journeymap.tools.models.World
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

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

                setOnAction {
                    controller.minecraftDirectoryPath.set(this.text)
                }
                tooltipProperty().bind(controller.textInputTooltip)

                focusedProperty().addListener { _, _, newValue ->
                    if (!newValue) {
                        controller.minecraftDirectoryPath.set(this.text)
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
                combobox<WorldType>(controller.worldTypeProperty(), controller.validWorldTypes) {
                    useMaxWidth = true
                    enableWhen(controller.textInputValid)
                }

                label("World")
                combobox<World>(controller.world, controller.validWorlds) {
                    useMaxWidth = true
                    enableWhen(controller.textInputValid)
                }
            }
            row { // Row 2
                label("Dimension")
                combobox<Dimension>(controller.dimension, controller.validDimensions) {
                    useMaxWidth = true
                    enableWhen(controller.textInputValid)
                }

                label("Map Type")
                combobox<MapType>(controller.mapTypeProperty(), controller.validMapTypes) {
                    useMaxWidth = true
                    enableWhen(controller.textInputValid)
                }
            }
            row { // Row 3
                label("Surface Layer")
                spinner<Int>(items = controller.validLayers, property = controller.layer) {
                    useMaxWidth = true

                    enableWhen(controller.textInputValid.and(controller.mapTypeProperty().isEqualTo(MapType.UNDERGROUND)))
                }

                label("Grid")
                combobox<GridType>(controller.gridTypeProperty(), controller.gridTypes) {
                    useMaxWidth = true

                    // TODO: Investigate mechanism, add more grid types?

//                    enableWhen(controller.textInputValid)
                    // TODO: Enable when we figure this out
                    enableWhen { SimpleBooleanProperty(false) }
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

                enableWhen(controller.textInputValid)
                action {
                    val targetFiles = chooseFile(
                            "Save map as...",
                            filters = arrayOf(FileChooser.ExtensionFilter("PNG files", ".png")),
                            mode = FileChooserMode.Save
                    )

                    if (targetFiles.isNotEmpty()) {
                        val target = targetFiles[0]

                        val dimDirectory: File = (if (controller.mapType == MapType.UNDERGROUND) {
                            controller.dimension.value.getLayerDirectory(controller.layer.value)
                        } else {
                            controller.dimension.value.getMapDirectory(controller.mapType)
                        })
                                // TODO: Check on null value that
                                ?:return@action

                        val stitcher = ImageStitcher(dimDirectory)

                        // TODO: Async and progress
                        stitcher.stitch(target)
                    }
                }
            }
        }
    }

    init {
        setStageIcon(Image(MainView::class.java.classLoader.getResourceAsStream("jm.png")))
        currentStage?.isResizable = false

        this.setWindowMinSize(600, 230)
        this.title = "JourneyMap Tools"

        this.controller.validateMinecraftDirectory()
    }
}
