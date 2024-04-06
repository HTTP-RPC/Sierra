/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.httprpc.sierra

import com.formdev.flatlaf.FlatLightLaf
import java.awt.Color
import java.awt.Image
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

class KotlinTest: JFrame("Kotlin Test"), Runnable {
    init {
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    override fun run() {
        val image: Image? = try {
            ImageIO.read(javaClass.getResource("world.png"))
        } catch (exception: IOException) {
            null
        }

        val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."

        contentPane = column(spacing = 8,
            row(
                glue(),
                JLabel("Hello, Kotlin!", SwingConstants.CENTER).weightBy(1.0),
                glue()
            ).apply {
                border = CompoundBorder(LineBorder(Color.LIGHT_GRAY), EmptyBorder(4, 4, 4, 4))
            },
            stack(
                ImagePane(image).apply {
                    scaleMode = ImagePane.ScaleMode.FILL_HEIGHT
                },
                TextPane(text).apply {
                    foreground = Color(0x44000000)
                    horizontalAlignment = HorizontalAlignment.CENTER
                    verticalAlignment = VerticalAlignment.CENTER
                    wrapText = true
                }
            ).weightBy(1.0).apply {
                border = LineBorder(Color.LIGHT_GRAY)
            }
        ).apply {
            background = Color.WHITE
            isOpaque = true
            border = EmptyBorder(8, 8, 8, 8)
        }

        setSize(360, 320)

        isVisible = true
    }
}

fun main() {
    FlatLightLaf.setup()

    SwingUtilities.invokeLater(KotlinTest())
}
