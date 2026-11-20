package util.paging;

import java.util.Collections;
import java.util.List;

public class Page<E> {
    private final List<E> elementsOnPage;
    private final int totalNumberOfElements;

    /**
     * Creates a page of elements.
     *
     * @param elementsOnPage        elements belonging to the requested page
     * @param totalNumberOfElements total number of matching elements across all pages
     */
    public Page(List<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage != null ? elementsOnPage : Collections.emptyList();
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public List<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}