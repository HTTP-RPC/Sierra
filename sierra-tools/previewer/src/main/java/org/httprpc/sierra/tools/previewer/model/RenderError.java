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
package org.httprpc.sierra.tools.previewer.model;

/**
 * A record to hold detailed information about a rendering failure. Implements
 * the error object described in Section VI.
 *
 * @param message A user-friendly error message.
 * @param exception The underlying exception for debugging.
 */
public record RenderError(String message, Exception exception) {
    @Override
    public String toString() {
        return message;
    }
}
