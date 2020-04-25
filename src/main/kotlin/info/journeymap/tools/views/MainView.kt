package info.journeymap.tools.views

import javafx.beans.property.StringProperty
import javafx.scene.layout.Priority
import tornadofx.*

class MainView : View() {
    override val root = vbox {
        this.paddingAll = 10
        this.spacing = 10.0

        hbox {
            textfield("Field") {
                this.hgrow = Priority.ALWAYS
            }
            button("Browse") {
                this.hgrow = Priority.ALWAYS
                this.minWidth = 50.0
            }

            this.spacing = 10.0
        }
        button("Press me")
        label("Waiting")
    }

    init {
        this.setWindowMinSize(300, 100)
        this.title = "JourneyMap Tools"
    }
}
