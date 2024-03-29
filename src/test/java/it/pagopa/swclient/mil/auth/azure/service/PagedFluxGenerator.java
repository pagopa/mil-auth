/*
 * PagedFluxGenerator.java
 *
 * 26 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.rest.PagedFlux;
import com.azure.core.http.rest.PagedResponse;
import com.azure.core.util.IterableStream;

import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
public class PagedFluxGenerator<T> {
	/**
	 * 
	 * @param dataList
	 * @return
	 */
	public PagedFlux<T> from(List<T> dataList) {
		/*
		 * Function that fetches subsequent pages of data given a continuation token.
		 */
		BiFunction<String, Integer, Mono<PagedResponse<T>>> nextPageRetriever = (continuationToken, pageSizeObj) -> Mono.just(new PagedResponse<T>() {
			private int getPageSize(Integer pageSizeObj) {
				if (pageSizeObj == null) {
					return 5;
				} else {
					return pageSizeObj.intValue();
				}
			}

			@Override
			public IterableStream<T> getElements() {
				int pageSize = getPageSize(pageSizeObj);
				int pageIndex = continuationToken == null ? 1 : Integer.parseInt(continuationToken);
				int pageCount = (int) Math.ceil((double) dataList.size() / pageSize);
				List<T> subList = null;
				if (pageIndex > 0 && pageIndex <= pageCount) {
					int startIndex = (pageIndex - 1) * pageSize;
					int endIndex = Math.min(startIndex + pageSize, dataList.size());
					subList = dataList.subList(startIndex, endIndex);
				} else {
					subList = List.of();
				}
				IterableStream<T> elements = new IterableStream<T>(subList);
				return elements;
			}

			@Override
			public String getContinuationToken() {
				int pageSize = getPageSize(pageSizeObj);
				int currentPageIndex = continuationToken == null ? 1 : Integer.parseInt(continuationToken);
				int pageCount = (int) Math.ceil((double) dataList.size() / pageSize);
				String continuationToken = null;
				if (currentPageIndex < pageCount) {
					continuationToken = Integer.toString(++currentPageIndex);
				}
				return continuationToken;
			}

			@Override
			public int getStatusCode() {
				return 0;
			}

			@Override
			public HttpHeaders getHeaders() {
				return null;
			}

			@Override
			public HttpRequest getRequest() {
				return null;
			}

			@Override
			public void close() throws IOException {
			}
		});

		/*
		 * Function that fetches the first page of data.
		 */
		Function<Integer, Mono<PagedResponse<T>>> firstPageRetriever = (pageSize) -> nextPageRetriever.apply(null, pageSize);

		/*
		 * Construction of the PagedFlux.
		 */
		return new PagedFlux<T>(firstPageRetriever, nextPageRetriever);
	}
}