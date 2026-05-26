package com.sonsure.dustman.test.common.model;

import com.sonsure.dustman.common.model.Pagination;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pagination 单元测试
 */
public class PaginationTest {

    @Test
    public void testConstructorDefault() {
        Pagination p = new Pagination();
        assertEquals(1, p.getPageNum());
        assertEquals(Pagination.UNKNOWN_ITEMS, p.getTotalItems());
        assertEquals(Pagination.DEFAULT_PAGE_SIZE, p.getPageSize());
    }

    @Test
    public void testConstructorWithPageSize() {
        Pagination p = new Pagination(10);
        assertEquals(1, p.getPageNum());
        assertEquals(Pagination.UNKNOWN_ITEMS, p.getTotalItems());
        assertEquals(10, p.getPageSize());
    }

    @Test
    public void testConstructorWithPageSizeZero() {
        Pagination p = new Pagination(0);
        assertEquals(Pagination.DEFAULT_PAGE_SIZE, p.getPageSize());
    }

    @Test
    public void testConstructorWithPageSizeAndTotalItems() {
        Pagination p = new Pagination(10, 100);
        assertEquals(10, p.getPageSize());
        assertEquals(100, p.getTotalItems());
        assertEquals(1, p.getPageNum());
    }

    @Test
    public void testConstructorWithNegativeTotalItems() {
        Pagination p = new Pagination(10, -50);
        assertEquals(0, p.getTotalItems());
    }

    @Test
    public void testGetPages() {
        Pagination p = new Pagination(10, 100);
        assertEquals(10, p.getPages());
    }

    @Test
    public void testGetPages_WithRemainder() {
        Pagination p = new Pagination(10, 95);
        assertEquals(10, p.getPages());
    }

    @Test
    public void testGetPages_ZeroItems() {
        Pagination p = new Pagination(10, 0);
        assertEquals(0, p.getPages());
    }

    @Test
    public void testSetPageNum_Normal() {
        Pagination p = new Pagination(10, 100);
        assertEquals(2, p.setPageNum(2));
        assertEquals(2, p.getPageNum());
    }

    @Test
    public void testSetPageNum_Negative() {
        Pagination p = new Pagination(10, 100);
        assertEquals(1, p.setPageNum(-5));
        assertEquals(1, p.getPageNum());
    }

    @Test
    public void testSetPageNum_ExceedMax() {
        Pagination p = new Pagination(10, 100);
        assertEquals(10, p.setPageNum(999));
        assertEquals(10, p.getPageNum());
    }

    @Test
    public void testSetPageNum_ZeroItems() {
        Pagination p = new Pagination(10, 0);
        assertEquals(0, p.setPageNum(5));
        assertEquals(0, p.getPageNum());
    }

    @Test
    public void testSetTotalItems_Normal() {
        Pagination p = new Pagination(10, 100);
        assertEquals(200, p.setTotalItems(200));
        assertEquals(200, p.getTotalItems());
    }

    @Test
    public void testSetTotalItems_Negative() {
        Pagination p = new Pagination(10, 100);
        assertEquals(0, p.setTotalItems(-10));
        assertEquals(0, p.getTotalItems());
    }

    @Test
    public void testSetTotalItems_AdjustPageNum() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(10);
        p.setTotalItems(50);
        assertEquals(5, p.getPageNum());
    }

    @Test
    public void testSetPageSize_Normal() {
        Pagination p = new Pagination(10, 100);
        assertEquals(20, p.setPageSize(20));
        assertEquals(20, p.getPageSize());
    }

    @Test
    public void testSetPageSize_Zero() {
        Pagination p = new Pagination(10, 100);
        assertEquals(Pagination.DEFAULT_PAGE_SIZE, p.setPageSize(0));
    }

    @Test
    public void testSetPageSize_Negative() {
        Pagination p = new Pagination(10, 100);
        assertEquals(Pagination.DEFAULT_PAGE_SIZE, p.setPageSize(-5));
    }

    @Test
    public void testSetPageSize_AdjustPageNum() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(5);
        p.setPageSize(20);
        assertEquals(3, p.getPageNum());
    }

    @Test
    public void testSetPageSize_PageUnchanged() {
        Pagination p = new Pagination(10, 100);
        // pageSize 不变时 pageNum 不变
        assertEquals(1, p.getPageNum());
        assertEquals(10, p.getPageSize());
    }

    @Test
    public void testGetOffset_PageNumPositive() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(3);
        assertEquals(20, p.getOffset());
    }

    @Test
    public void testGetOffset_PageNumZero() {
        Pagination p = new Pagination(10, 100);
        assertEquals(0, p.getOffset());
    }

    @Test
    public void testSetOffset() {
        Pagination p = new Pagination(10, 100);
        assertEquals(3, p.setOffset(25));
        assertEquals(3, p.getPageNum());
    }

    @Test
    public void testGetLength_PageNumPositive() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(3);
        assertEquals(10, p.getLength());
    }

    @Test
    public void testGetLength_PageNotSet() {
        // pageNum 默认 1，length = min(10*1, 100) - 10*0 = 10
        Pagination p = new Pagination(10, 100);
        assertEquals(10, p.getLength());
    }

    @Test
    public void testGetLength_LastPagePartial() {
        Pagination p = new Pagination(10, 25);
        p.setPageNum(3);
        assertEquals(5, p.getLength());
    }

    @Test
    public void testGetBeginIndex_PageNumPositive() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(3);
        assertEquals(20, p.getBeginIndex());
    }

    @Test
    public void testGetBeginIndex_PageNumZero() {
        Pagination p = new Pagination(10, 100);
        assertEquals(0, p.getBeginIndex());
    }

    @Test
    public void testGetEndIndex_PageNumPositive() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(3);
        assertEquals(30, p.getEndIndex());
    }

    @Test
    public void testGetEndIndex_LastPage() {
        Pagination p = new Pagination(10, 25);
        p.setPageNum(3);
        assertEquals(25, p.getEndIndex());
    }

    @Test
    public void testGetEndIndex_FirstPage() {
        Pagination p = new Pagination(10, 100);
        assertEquals(10, p.getEndIndex());
    }

    @Test
    public void testSetItem() {
        Pagination p = new Pagination(10, 100);
        assertEquals(3, p.setItem(25));
    }

    @Test
    public void testGetFirstPage() {
        Pagination p = new Pagination(10, 100);
        assertEquals(1, p.getFirstPage());
    }

    @Test
    public void testGetFirstPage_ZeroItems() {
        Pagination p = new Pagination(10, 0);
        assertEquals(0, p.getFirstPage());
    }

    @Test
    public void testGetLastPage() {
        Pagination p = new Pagination(10, 95);
        assertEquals(10, p.getLastPage());
    }

    @Test
    public void testGetLastPage_ZeroItems() {
        Pagination p = new Pagination(10, 0);
        assertEquals(0, p.getLastPage());
    }

    @Test
    public void testGetPreviousPage() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(5);
        assertEquals(4, p.getPreviousPage());
    }

    @Test
    public void testGetPreviousPage_FirstPage() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(1);
        assertEquals(1, p.getPreviousPage());
    }

    @Test
    public void testGetPreviousPageN() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(5);
        assertEquals(3, p.getPreviousPage(2));
    }

    @Test
    public void testGetNextPage() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(5);
        assertEquals(6, p.getNextPage());
    }

    @Test
    public void testGetNextPage_LastPage() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(10);
        assertEquals(10, p.getNextPage());
    }

    @Test
    public void testGetNextPageN() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(5);
        assertEquals(7, p.getNextPage(2));
    }

    @Test
    public void testIsDisabledPage_LessThanOne() {
        Pagination p = new Pagination(10, 100);
        assertTrue(p.isDisabledPage(0));
    }

    @Test
    public void testIsDisabledPage_GreaterThanPages() {
        Pagination p = new Pagination(10, 100);
        assertTrue(p.isDisabledPage(11));
    }

    @Test
    public void testIsDisabledPage_CurrentPage() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(5);
        assertTrue(p.isDisabledPage(5));
    }

    @Test
    public void testIsDisabledPage_Valid() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(5);
        assertFalse(p.isDisabledPage(3));
    }

    @Test
    public void testIsDisabledPage_ZeroItems() {
        Pagination p = new Pagination(10, 0);
        assertTrue(p.isDisabledPage(1));
    }

    @Test
    public void testGetSlider_Default() {
        Pagination p = new Pagination(10, 200);
        p.setPageNum(10);
        int[] slider = p.getSlider();
        assertEquals(Pagination.DEFAULT_SLIDER_SIZE, slider.length);
        // 页码范围
        assertTrue(slider[0] >= 1);
        assertTrue(slider[slider.length - 1] <= p.getPages());
    }

    @Test
    public void testGetSlider_NarrowWidth() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(5);
        // 窗口比总页数小
        int[] slider = p.getSlider(5);
        assertEquals(5, slider.length);
    }

    @Test
    public void testGetSlider_WidthExceedsPages() {
        Pagination p = new Pagination(10, 30);
        p.setPageNum(2);
        // 窗口比总页数大，应截断为总页数
        int[] slider = p.getSlider(10);
        assertEquals(3, slider.length);
        assertEquals(1, slider[0]);
        assertEquals(3, slider[2]);
    }

    @Test
    public void testGetSlider_WidthLessThanOne() {
        Pagination p = new Pagination(10, 100);
        int[] slider = p.getSlider(0);
        assertEquals(0, slider.length);
    }

    @Test
    public void testGetSlider_NegativeWidth() {
        Pagination p = new Pagination(10, 100);
        int[] slider = p.getSlider(-1);
        assertEquals(0, slider.length);
    }

    @Test
    public void testGetSlider_ZeroPages() {
        Pagination p = new Pagination(10, 0);
        int[] slider = p.getSlider(5);
        assertEquals(0, slider.length);
    }

    @Test
    public void testGetSlider_FirstPageNearOne() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(1);
        int[] slider = p.getSlider(7);
        assertEquals(1, slider[0]);
    }

    @Test
    public void testGetSlider_LastPage() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(10);
        int[] slider = p.getSlider(7);
        assertEquals(4, slider[0]);
        assertEquals(10, slider[6]);
    }

    @Test
    public void testToString_Normal() {
        Pagination p = new Pagination(10, 100);
        p.setPageNum(3);
        String s = p.toString();
        assertTrue(s.contains("Pagination"));
        assertTrue(s.contains("total 100 items"));
    }

    @Test
    public void testToString_ZeroPages() {
        Pagination p = new Pagination(10, 0);
        String s = p.toString();
        assertTrue(s.contains("Pagination"));
    }
}
