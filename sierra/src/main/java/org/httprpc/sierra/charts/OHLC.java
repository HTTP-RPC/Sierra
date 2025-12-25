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

package org.httprpc.sierra.charts;

/**
 * Represents OHLC (open, high, low, close) data.
 *
 * @param open
 * The opening value.
 *
 * @param high
 * The high value.
 *
 * @param low
 * The low value.
 *
 * @param close
 * The closing value.
 */
public record OHLC(
    double open,
    double high,
    double low,
    double close
) {
}
