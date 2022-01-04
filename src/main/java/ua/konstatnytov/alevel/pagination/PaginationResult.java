package ua.konstatnytov.alevel.pagination;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;

@Getter
public class PaginationResult<E> {
    private final int totalRecords;
    private final int currentPage;
    private final List<E> list;
    private final int maxResult;
    private final int totalPages;
    private final int maxNavigationPage;
    private List<Integer> navigationPages;

    public PaginationResult(Query<E> query, int page, int maxResult, int maxNavigationPage) {
        final int pageIndex = Math.max(page - 1, 0);
        int fromRecordIndex = pageIndex * maxResult;
        int maxRecordIndex = fromRecordIndex + maxResult;
        ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
        List<E> results = new ArrayList<>();
        boolean hasResult = resultScroll.first();
        if (hasResult) {
            hasResult = resultScroll.scroll(fromRecordIndex);
            if (hasResult) {
                do {
                    E record = (E) resultScroll.get(0);
                    results.add(record);
                } while (resultScroll.next()//
                        && resultScroll.getRowNumber() >= fromRecordIndex
                        && resultScroll.getRowNumber() < maxRecordIndex);
            }
            resultScroll.last();
        }
        totalRecords = resultScroll.getRowNumber() + 1;
        currentPage = pageIndex + 1;
        list = results;
        this.maxResult = maxResult;
        if (totalRecords % maxResult == 0) {
            totalPages = totalRecords / maxResult;
        } else {
            totalPages = (totalRecords / maxResult) + 1;
        }
        this.maxNavigationPage = maxNavigationPage;
        calcNavigationPages();
    }

    private void calcNavigationPages() {
        navigationPages = new ArrayList<>();
        int current = Math.min(currentPage, totalPages);
        int begin = current - maxNavigationPage / 2;
        int end = current + maxNavigationPage / 2;
        navigationPages.add(1);
        if (begin > 2) {
            navigationPages.add(-1);
        }
        for (int i = begin; i < end; i++) {
            if (i > 1 && i < totalPages) {
                navigationPages.add(i);
            }
        }
        if (end < totalPages - 2) {
            navigationPages.add(-1);
        }
        navigationPages.add(totalPages);
    }
}