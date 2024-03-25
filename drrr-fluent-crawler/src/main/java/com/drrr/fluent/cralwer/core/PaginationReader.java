package com.drrr.fluent.cralwer.core;

import java.util.Optional;
import org.openqa.selenium.WebDriver;


/**
 * 크롤링하는 페이지의 페이지네이션 부분을 읽어오는 인터페이스 입니다.
 */
public interface PaginationReader {


    PaginationInformation read(WebDriver webDriver);

    /**
     * PagintaionInformation은 페이지네이션 영역에서 얻을 수 있는 데이터 가집니다.
     *
     * @param maxPage  페이지네이션에서 얻을 수 있는 페이지 번호
     * @param lastPage 페이지네이션에서 얻을 수 있는 마지막 페이지 번호
     */
    record PaginationInformation(int maxPage, Optional<Integer> lastPage) {


        public PaginationInformation(int maxPage) {
            this(maxPage, Optional.empty());
        }

        /**
         * @formatter:off
         * <p>
         * 페이지네이션 크롤링 중 페이지 정보를 가져올 수 없는 경우 적용
         * 예를 들어 마지막 페이지의 경우 다음으로 이동할 수 없는 페이지의 번호를 가져올 때 사용함
         * </p>
         * @formatter:on
         * @return
         */
        public static PaginationInformation stopInformation() {
            return new PaginationInformation(Integer.MIN_VALUE);
        }

        /**
         * 크롤링 완료 조건 마지막 페이지가 현재 크롤링 중인 페이지보다 작거나 같은 경우
         *
         * @param currentPage 현재 페이지 번호
         * @return
         */
        public boolean complete(int currentPage) {
            return lastPage.orElse(maxPage) < currentPage;
        }
    }
}
