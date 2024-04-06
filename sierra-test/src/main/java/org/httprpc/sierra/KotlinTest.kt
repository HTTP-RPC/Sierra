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
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

class KotlinTest: JFrame("Kotlin Test"), Runnable {
    init {
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    override fun run() {
        contentPane = column(
            glue(1.0),
            JLabel("Hello, World!", SwingConstants.CENTER).weightBy(1.0).apply {
                border = LineBorder(Color.LIGHT_GRAY)
            },
            glue(3.0)
        ).apply {
            border = EmptyBorder(8, 8, 8, 8)
        }

        setSize(480, 320)

        isVisible = true
    }
}

fun main() {
    FlatLightLaf.setup()

    SwingUtilities.invokeLater(KotlinTest())
}
