package com.drrr.domain.metacategory.domain;

public enum MetaCategoryType {
    REPLACE, // 다른 카테고리로 치환
    NONE, // 카테고리 제한 없음
    IGNORE, // 무시
    EXTEND, // 확장 (기존 태그 그대로 둔 상태에서 다른 태그도 추가로 가짐)
    
}
