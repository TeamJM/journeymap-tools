package info.journeymap.tools.images

import ar.com.hjg.pngj.*
import ar.com.hjg.pngj.chunks.ChunkLoadBehaviour
import info.journeymap.tools.constants.GridType
import tornadofx.FXTask
import tornadofx.TaskStatus
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import javax.imageio.ImageIO

class ImageStitcher(val directory: File) {
    fun stitch(destination: File, gridType: GridType, task: FXTask<*>) {
        task.updateProgress(0, 1)
        task.updateTitle("Loading...")
        task.updateMessage("Loading...")

        val sourceFiles = this.directory.listFiles()!!.filter { it.extension == "png" }.toList()
        val tileCoordinates: MutableMap<Pair<Int, Int>, File> = mutableMapOf()

        var maxX: Int = Int.MIN_VALUE
        var minX: Int = Int.MAX_VALUE
        var maxZ: Int = Int.MIN_VALUE
        var minZ: Int = Int.MAX_VALUE

        sourceFiles.forEach { it ->
            val split = it.nameWithoutExtension.split(',')
            val (x, z) = split.map { element -> element.toInt() }.toList()

            maxX = max(maxX, x)
            minX = min(minX, x)

            maxZ = max(maxZ, z)
            minZ = min(minZ, z)

            tileCoordinates[Pair(x, z)] = it
        }

        val columns = (maxX - minX) + 1
        val rows = (maxZ - minZ) + 1
        val tiles: MutableList<File> = mutableListOf()

        for (z in minZ .. maxZ) {
            for (x in minX .. maxX) {
                val pair = Pair(x, z)

                if (tileCoordinates.contains(pair)) {
                    tiles.add(tileCoordinates[pair]!!)
                } else {
                    tiles.add(this.getBlankImageFile())
                }
            }
        }

        val readers: MutableList<PngReader> = mutableListOf()

        (0..columns).forEach { _ ->
            readers.add(PngReader(getBlankImageFile()))  // Populate the reader list
        }

        val targetImageInfo = ImageInfo(512 * columns, 512 * rows,8, true)
        val writer = PngWriter(destination.outputStream(), targetImageInfo)

        writer.metadata.setText("Author", "JourneyMap")
        writer.metadata.setText("Comment", "https://journeymap.info")

        val line = ImageLineInt(targetImageInfo)
        val lineLength = 512 * 4  // 4 bytes per pixel
        val gridColour = 135

        var destinationRow = 0
        val progressMax = (rows * columns).toLong()

        task.updateTitle("Processing...")

        for (row in 0 until rows) {
            // I have no idea what's going on here, porting is hard
            val xCursor = if (row < rows - 1) {
                columns
            } else {
                tiles.size - (rows - 1) * columns
            }

            Arrays.fill(line.scanline, 0)

            for (x in 0 until xCursor) {
                val sourceFile = tiles[x + row * columns]
                val reader = PngReader(sourceFile.inputStream())

                reader.setChunkLoadBehaviour(ChunkLoadBehaviour.LOAD_CHUNK_NEVER)

                readers[x] = reader
            }

            rowCopy@ for (sourceRow in 0 until 512) {
                for (column in 0 until xCursor) {
                    if (sourceRow == 0) {  // Update task progress
                        val progress = (row.toLong() * columns) + column

                        task.updateProgress(progress, progressMax)
                        task.updateMessage("Current tile: $progress / $progressMax")
                    }

                    val sourceLine = readers[column].readRow(sourceRow) as ImageLineInt
                    val source = sourceLine.scanline

                    // region: Grid
                    if (gridType == GridType.LINES) {
                        val skip = if (sourceRow % 16 == 0) {
                            4
                        } else {
                            64
                        }

                        for (i in 0..(source.size - skip) step skip) {
                            source[i] = ((source[i] * 2) + gridColour) / 3
                            source[i + 1] = ((source[i + 1] * 2) + gridColour) / 3
                            source[i + 2] = ((source[i + 2] * 2) + gridColour) / 3
                            source[i + 3] = 255
                        }
                    }
                    // endregion

                    val dest = line.scanline
                    val destPosition = lineLength * column

                    try {
                        System.arraycopy(source, 0, dest, destPosition, lineLength)
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        System.err.println("Bad image data. Source: ${source.size} / Dest: ${dest.size} / Dest pos: $destPosition")
                        break@rowCopy
                    }
                }

                writer.writeRow(line, destinationRow)
                destinationRow += 1
            }

            for (x in 0 until xCursor) {
                readers[x].end()
            }
        }

        task.updateProgress(progressMax, progressMax)
        task.updateTitle("Done!")
        task.updateMessage("Done!")
        writer.end()
    }

    fun getBlankImageFile(): File {
        val file = File(".", String.format("blank.png"))

        if (!file.exists()) {
            val image = BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB)
            image.createGraphics()

            try {
                ImageIO.write(image, "png", file)
                file.setReadOnly()
                file.deleteOnExit()
            } catch (e: IOException) {
                e.printStackTrace(System.err)
            }
        }

        return file
    }
}
