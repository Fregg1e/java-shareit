package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class OffsetPageRequestTest {
    @Test
    void createOffsetPageRequestWithOffsetLessThen0Test() {
        assertThrows(IllegalArgumentException.class,
                () -> new OffsetPageRequest(-1, 2, Sort.unsorted()));
    }

    @Test
    void createOffsetPageRequestWithLimitLessThen1Test() {
        assertThrows(IllegalArgumentException.class,
                () -> new OffsetPageRequest(0, 0, Sort.unsorted()));
    }

    @Test
    void getPageNumberTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(4, 2);

        assertEquals(2, pageRequest.getPageNumber());
    }

    @Test
    void nextTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(4, 2);
        OffsetPageRequest nextPage = (OffsetPageRequest) pageRequest.next();

        assertEquals(6, nextPage.getOffset());
    }

    @Test
    void hasPreviousTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(4, 2);

        assertTrue(pageRequest.hasPrevious());
    }

    @Test
    void previousWhenHasTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(4, 2);

        assertEquals(2, pageRequest.previous().getOffset());
    }

    @Test
    void previousWhenHasNotTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(2, 4);

        assertEquals(pageRequest, pageRequest.previous());
    }

    @Test
    void previousOrFirstWhenHasPreviousTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(4, 2);

        assertEquals(2, pageRequest.previousOrFirst().getOffset());
    }

    @Test
    void previousOrFirstWhenHasNotPreviousTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(2, 4);

        assertEquals(0, pageRequest.previousOrFirst().getOffset());
    }

    @Test
    void withPageTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(2, 4);

        assertEquals(5, pageRequest.withPage(5).getOffset());
        assertEquals(4, pageRequest.withPage(5).getPageSize());
    }

    @Test
    void equalsWhenNullTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(2, 4);

        assertFalse(pageRequest.equals(null));
    }

    @Test
    void equalsWhenNotSameClassTest() {
        OffsetPageRequest pageRequest = new OffsetPageRequest(2, 4);

        assertFalse(pageRequest.equals(PageRequest.of(2, 4)));
    }

    @Test
    void equalsWhenDifferentFieldTest() {
        OffsetPageRequest pageRequest1 = new OffsetPageRequest(2, 4);
        OffsetPageRequest pageRequest2 = new OffsetPageRequest(3, 4);

        assertFalse(pageRequest1.equals(pageRequest2));
    }

    @Test
    void equalsTest() {
        OffsetPageRequest pageRequest1 = new OffsetPageRequest(2, 4);
        OffsetPageRequest pageRequest2 = new OffsetPageRequest(2, 4);

        assertTrue(pageRequest1.equals(pageRequest2));
    }
}