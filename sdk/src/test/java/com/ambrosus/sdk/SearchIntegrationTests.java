/*
 * Copyright: Ambrosus Inc.
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ambrosus.sdk;

import com.ambrosus.sdk.model.AMBAssetInfo;
import com.ambrosus.sdk.model.AMBEvent;
import com.ambrosus.sdk.model.AMBEventQueryBuilder;
import com.ambrosus.sdk.model.AMBNetwork;
import com.ambrosus.sdk.model.AssetInfoQueryBuilder;
import com.ambrosus.sdk.model.Identifier;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchIntegrationTests {

    private static AMBNetwork network;

    @BeforeClass
    public static void setUpNetwork(){
        network = new AMBNetwork();
    }
    
    @Test
    public void searchIntegrationTest() throws Throwable {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 12, 1);
        Date lastDate = calendar.getTime();

        Map<Query<? extends Entity>, Class<? extends Entity>> queriesMap = new LinkedHashMap<>();

        queriesMap.put(new AssetQueryBuilder().to(lastDate).build(), Asset.class);
        queriesMap.put(new EventQueryBuilder().to(lastDate).build(), Event.class);
        queriesMap.put(new AMBEventQueryBuilder().to(lastDate).byDataObjectType("ambrosus.asset.manufactured").build(), AMBEvent.class);
        queriesMap.put(new AssetInfoQueryBuilder().byIdentifier(new Identifier("EAN13", "3451080000324")).build(), AMBAssetInfo.class);

        for (Query<? extends Entity> query : queriesMap.keySet()) {
            //requesting first page and checking items type
            SearchResult<? extends Entity> firstPage = network.find(query).execute();

            Entity firstPageItem = firstPage.getItems().get(0);
            assertTrue(queriesMap.get(query).isInstance(firstPageItem));

            ArrayList<Entity> bigPageItemsList = new ArrayList<>(firstPage.getItems());
            SearchResult<? extends Entity> secondPage = network.find(new PageQueryBuilder(firstPage).getQueryForPage(1)).execute();
            bigPageItemsList.addAll(secondPage.getItems());

            //querying same items using small pages
            int smallPageSize = firstPage.getPageSize() / 3;
            int smallPagesCount = (firstPage.getItems().size() + secondPage.getItems().size()) / smallPageSize;

            Query smallPageQuery = new QueryBuilder(query).perPage(smallPageSize).build();
            SearchResult<? extends Entity> smallPage = network.find(smallPageQuery).execute();

            List<Entity> smallPageItemsList = new ArrayList<>(smallPage.getItems());

            PageQueryBuilder pageQueryBuilder = new PageQueryBuilder(smallPage);
            for(int pageIndex = 1; pageIndex < smallPagesCount; pageIndex++) {
                SearchResult<? extends Entity> nextPage = network.find(pageQueryBuilder.getQueryForPage(pageIndex)).execute();
                smallPageItemsList.addAll(nextPage.getItems());
            }

            //comparing items which we get with small and big pages
            //it should be same items in the same order
            //only items count may vary a little bit depending on the test environment
            int commonItemsCount = Math.min(bigPageItemsList.size(), smallPageItemsList.size());
            assertEquals(bigPageItemsList.subList(0, commonItemsCount), smallPageItemsList.subList(0, commonItemsCount));
        }
    }

    @Test
    public void getSearchResultOfTypeBySubClassQuery() throws Throwable {
        AMBNetwork network = new AMBNetwork();
        SearchResult<AMBEvent> res = network.findAMBEvents(new AssetInfoQueryBuilder().build()).execute();
        System.out.println(res);
    }


}
