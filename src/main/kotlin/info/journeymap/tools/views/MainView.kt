package info.journeymap.tools.views

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import java.io.File

class MainView : View() {
    val minecraftPath = SimpleStringProperty(getDefaultMinecraftPath())
    var lastDirectory = File(minecraftPath.value)

    lateinit var minecraftPathInput: TextField
    lateinit var minecraftPathTooltip: Tooltip

    override val root = vbox {
        this.paddingAll = 10
        this.spacing = 10.0

        hbox {
            this.alignment = Pos.CENTER_LEFT
            this.spacing = 10.0

            label("Minecraft Directory")

            minecraftPathInput = textfield(minecraftPath) {
                minecraftPathTooltip = tooltip()

                this.hgrow = Priority.ALWAYS
            }

            button("Browse") {
                this.hgrow = Priority.ALWAYS
                this.minWidth = 50.0
            }.action {
                val directory = chooseDirectory("Select Minecraft Directory", lastDirectory)

                if (directory == null) {
                    minecraftPath.value = null
                    lastDirectory = File(getDefaultMinecraftPath())
                } else {
                    minecraftPath.value = directory.path
                    lastDirectory = directory
                }

                validatePath()
            }
        }
    }

    init {
        this.setWindowMinSize(600, 500)
        this.title = "JourneyMap Tools"

        this.validatePath()
    }

    fun checkMinecraftPath(): Boolean {
        val pathFile = this.lastDirectory
        val jmFile = pathFile.resolve("journeymap")

        if (! (pathFile.exists() && pathFile.isDirectory && jmFile.exists() && jmFile.isDirectory)) {
            return false
        }

        val dataFile = jmFile.resolve("data")

        if (! (dataFile.exists() && dataFile.isDirectory())) {
            return false
        }

        return true
    }

    fun validatePath() {
        if (! this.checkMinecraftPath()) {
            this.minecraftPathInput.style() {
                this.baseColor = Color.valueOf("#FF6666")
            }

            this.minecraftPathTooltip.text = "Unable to find a valid JourneyMap data directory"
        } else {
            this.minecraftPathInput.style = null
            this.minecraftPathTooltip.text = null
        }
    }

    fun getDefaultMinecraftPath(): String {
        val OS = System.getProperty("os.name").toUpperCase()

        val baseDirectory = if (OS.contains("WIN")) {
            System.getenv("APPDATA")
        } else {
            System.getProperty("user.home")
        }

        return "${baseDirectory}${File.separator}.minecraft"
    }
}
