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
 * The component's weight, or `null` if a weight value has not been applied.
 */
val JComponent.weight: Double?
    get() = getClientProperty("weight") as? Double

/**
 * Applies a weight value to a component.
 *
 * @param weight The weight value.
 *
 * @return The component instance.
 */
fun <C: JComponent> C.weightBy(weight: Int) = weightBy(weight.toDouble())

/**
 * Applies a weight value to a component.
 *
 * @param weight The weight value.
 *
 * @return The component instance.
 */
fun <C: JComponent> C.weightBy(weight: Double): C {
    require(weight >= 0.0)

    putClientProperty("weight", weight)

    return this
}

/**
 * Declares a row.
 *
 * @param components The row's contents.
 *
 * @return The row panel.
 */
fun row(vararg components: JComponent) = row(0, false, *components)

/**
 * Declares a row.
 *
 * @param spacing The row spacing.
 * @param components The row's contents.
 *
 * @return The row panel.
 */
fun row(spacing: Int, vararg components: JComponent) = row(spacing, false, *components)

/**
 * Declares a row.
 *
 * @param alignToBaseline `true` to align to baseline; `false`, otherwise.
 * @param components The row's contents.
 *
 * @return The row panel.
 */
fun row(alignToBaseline: Boolean, vararg components: JComponent) = row(0, alignToBaseline, *components)

/**
 * Declares a row.
 *
 * @param spacing The row spacing.
 * @param alignToBaseline `true` to align to baseline; `false`, otherwise.
 * @param components The row's contents.
 *
 * @return The row panel.
 */
fun row(spacing: Int, alignToBaseline: Boolean, vararg components: JComponent): RowPanel {
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
 * @param components The column's contents.
 *
 * @return The column panel.
 */
fun column(vararg components: JComponent) = column(0, false, *components)

/**
 * Declares a column.
 *
 * @param spacing The column spacing.
 * @param components The column's contents.
 *
 * @return The column panel.
 */
fun column(spacing: Int, vararg components: JComponent) = column(spacing, false, *components)

/**
 * Declares a column.
 *
 * @param alignToGrid `true` to align to grid; `false`, otherwise.
 * @param components The column's contents.
 *
 * @return The column panel.
 */
fun column(alignToGrid: Boolean, vararg components: JComponent) = column(0, alignToGrid, *components)

/**
 * Declares a column.
 *
 * @param spacing The column spacing.
 * @param alignToGrid `true` to align to grid; `false`, otherwise.
 * @param components The column's contents.
 *
 * @return The column panel.
 */
fun column(spacing: Int, alignToGrid: Boolean, vararg components: JComponent): ColumnPanel {
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
 *
 * @return The spacer component.
 */
fun strut(size: Int) = Spacer(size)

/**
 * Declares a flexible spacer.
 *
 * @param weight The spacer weight.
 *
 * @return The spacer component.
 */
fun glue(weight: Double = 1.0) = Spacer(0).weightBy(weight)

/**
 * Declares a stack.
 *
 * @param components The stack's contents.
 *
 * @return The stack panel.
 */
fun stack(vararg components: JComponent): StackPanel {
    val stackPanel = StackPanel()

    for (component in components.reversed()) {
        stackPanel.add(component, component.weight)
    }

    return stackPanel
}
