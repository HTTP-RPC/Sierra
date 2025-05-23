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

package org.httprpc.sierra.test;

import org.httprpc.kilo.RequestMethod;
import org.httprpc.kilo.ResourcePath;
import org.httprpc.kilo.ServicePath;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@ServicePath("tiingo")
public interface TiingoServiceProxy {
    @RequestMethod("GET")
    @ResourcePath("daily/?")
    Asset getAsset(String ticker) throws IOException;

    @RequestMethod("GET")
    @ResourcePath("daily/?/prices")
    List<AssetPricing> getHistoricalPricing(String ticker, LocalDate startDate, LocalDate endDate) throws IOException;
}
