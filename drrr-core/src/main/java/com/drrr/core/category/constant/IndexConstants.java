package com.drrr.core.category.constant;

import lombok.Getter;

@Getter
public enum IndexConstants {
    가("\uAC00"), 나("\uB098"), 다("\uB2E4"), 라("\uB77C"), 마("\uB9C8"),
    바("\uBC14"), 사("\uC0AC"), 아("\uC544"), 자("\uC790"), 차("\uCC28"),
    카("\uCE74"), 타("\uD0C0"), 파("\uD30C"), 하("\uD558"), 힣("\uD7A3"),
    A("A"), B("B"), C("C"), D("D"), E("E"), F("F"), G("G"),
    H("H"), I("I"), J("J"), K("K"), L("L"), M("M"), N("N"),
    O("O"), P("P"), Q("Q"), R("R"), S("S"), T("T"), U("U"),

    V("V"), W("W"), X("X"), Y("Y"), Z("Z");

    private final String character;
    private final static IndexConstants[] values = values();

    IndexConstants(final String character) {
        this.character = character;
    }

    public IndexConstants getNext() {
        // 현재 enum 값의 순서를 찾고, 다음 값을 반환
        IndexConstants[] values = values();
        int ordinal = this.ordinal();

        // 마지막 enum 값이면 첫 번째 값으로 순환
        if (ordinal == values.length - 1) {
            return values[0];
        }
        return values[ordinal + 1];
    }


}
