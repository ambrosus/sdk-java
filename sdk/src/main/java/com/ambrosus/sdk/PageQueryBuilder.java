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

import com.ambrosus.sdk.utils.Assert;

import java.util.Locale;

public class PageQueryBuilder<T extends Entity> {

    private final SearchResult<T> firstPage;

    public PageQueryBuilder(SearchResult<T> firstPage) {
        Assert.assertTrue(
                firstPage.getPageIndex() == 0,
                IllegalArgumentException.class,
                "You can use only first page for this builder"
        );
        this.firstPage = firstPage;
    }

    /**
     *
     * @param pageIndex - zero based page index (first page has index equal to 0)
     * @return
     * @throws IllegalArgumentException if page is no in available pages range
     */
    public Query<? extends T> getQueryForPage(int pageIndex) throws IllegalArgumentException {
        Assert.assertTrue(
                pageIndex >=0 && pageIndex < firstPage.getTotalPages(),
                IllegalArgumentException.class,
                String.format(Locale.US, "page has to be in range [0, %d) but got %d", firstPage.getTotalPages(), pageIndex)

        );

        QueryBuilder<? extends T> queryBuilder = new QueryBuilder<>(firstPage.getQuery())
                .to(firstPage.getFirstItemTimestamp())
                .page(pageIndex);

        if(firstPage.getPageSize() != null)
            queryBuilder.perPage(firstPage.getPageSize());
        else if(pageIndex > 0) // it's ok if firstPage.getPageSize()==0 for first page of the search result if this search result has only 1 page
            throw new IllegalStateException("page size must be available for any search results with getTotalPages() > 1");

        return queryBuilder.build();
    }

    public int getTotalPages() {
        return firstPage.getTotalPages();
    }
}
