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
package org.httprpc.sierra.previewer.model;

import javax.swing.JComponent;

/**
 * A sealed interface representing the outcome of the rendering process.
 * It will be either a Success or an Error.
 */
public sealed interface RenderResult {
    /**
     * Represents a successful render.
     *
     * @param component The root JComponent to display.
     */
    record Success(JComponent component) implements RenderResult {}

    /**
     * Represents a failed render.
     *
     * @param details The RenderError object.
     */
    record Error(RenderError details) implements RenderResult {}
}