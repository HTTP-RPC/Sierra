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

import javax.swing.JComponent

/**
 * The component's weight, or `null` for no weight.
 */
var JComponent.weight: Double?
    get() = getClientProperty("weight") as? Double
    set(value) {
        require(value == null || value >= 0.0)

        putClientProperty("weight", value)
    }

/**
 * Declares a row.
 *
 * @param spacing The row spacing.
 * @param alignToBaseline `true` to align to baseline; `false`, otherwise.
 * @param components The row's contents.
 */
fun row(spacing: Int = 0, alignToBaseline: Boolean = false, vararg components: JComponent): RowPanel {
    val rowPanel = RowPanel()

    rowPanel.spacing = spacing
    rowPanel.alignToBaseline = alignToBaseline

    for (component in components) {
        rowPanel.add(component, component.weight)
    }

    return rowPanel
}

/**
 * Declares a column.
 *
 * @param spacing The column spacing.
 * @param alignToGrid `true` to align to grid; `false`, otherwise.
 * @param components The column's contents.
 */
fun column(spacing: Int = 0, alignToGrid: Boolean = false, vararg components: JComponent): ColumnPanel {
    val columnPanel = ColumnPanel()

    columnPanel.spacing = spacing
    columnPanel.alignToGrid = alignToGrid

    for (component in components) {
        columnPanel.add(component, component.weight)
    }

    return columnPanel
}

/**
 * Declares a fixed-size spacer.
 *
 * @param size The spacer size.
 */
fun strut(size: Int) = Spacer(size)

/**
 * Declares a flexible spacer.
 *
 * @param weight The spacer weight.
 */
fun glue(weight: Double = 1.0): Spacer {
    val spacer = Spacer(0)

    spacer.weight = weight

    return spacer
}

/**
 * Declares a stack.
 *
 * @param components The stack's contents.
 */
fun stack(vararg components: JComponent): StackPanel {
    val stackPanel = StackPanel()

    for (component in components.reversed()) {
        stackPanel.add(component, component.weight)
    }

    return stackPanel
}
