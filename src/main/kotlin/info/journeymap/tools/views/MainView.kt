package info.journeymap.tools.views

import tornadofx.*

class MainView : View() {
    override val root = vbox {
        button("Press me")
        label("Waiting")
    }
}
