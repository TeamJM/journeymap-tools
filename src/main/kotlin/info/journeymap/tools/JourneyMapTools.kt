package info.journeymap.tools

import org.fusesource.jansi.AnsiConsole
import picocli.CommandLine
import tornadofx.launch
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "JMTools",
    subcommands = [ ]
)
class JourneyMapTools: Callable<Int> {
    override fun call(): Int {
        launch<JMToolsApp>()
        return 0
    }
}

fun main(args: Array<String>) {
    AnsiConsole.systemInstall()
    val returnValue = CommandLine(JourneyMapTools()).execute(*args)
    AnsiConsole.systemUninstall()

    exitProcess(returnValue)
}