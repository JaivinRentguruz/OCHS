package com.ochs.app.Card;

import com.ochs.app.R;

import java.util.regex.Pattern;

public enum CardType {

    VISA("^4\\d*",
            R.string.VISA,
            16, 16,
            3, R.string.bt_cvv, null),
    MASTERCARD("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[0-1]|2720)\\d*",
            R.string.MASTER,
            16, 16,
            3, R.string.bt_cvc, null),
    DISCOVER("^(6011|65|64[4-9]|622)\\d*",
            R.string.discover,
            16, 19,
            3, R.string.bt_cid, null),
    AMEX("^3[47]\\d*",
            R.string.amex,
            15, 15,
            4, R.string.bt_cid, null),
    DINERS_CLUB("^(36|38|30[0-5])\\d*",
            R.string.diners_club,
            14, 14,
            3, R.string.bt_cvv, null),
    JCB("^35\\d*",
            R.string.jcb,
            16, 16,
            3, R.string.bt_cvv, null),
    MAESTRO("^(5018|5020|5038|5043|5[6-9]|6020|6304|6703|6759|676[1-3])\\d*",
            R.string.maestro,
            12, 19,
            3, R.string.bt_cvc,
            "^6\\d*"),
    UNIONPAY("^62\\d*",
            R.string.unionpay,
            16, 19,
            3, R.string.bt_cvn, null),
    HIPER("^637(095|568|599|609|612)\\d*",
            R.string.hiper,
            16, 16,
            3, R.string.bt_cvc, null),
    HIPERCARD("^606282\\d*",
            R.string.hipercard,
            16, 16,
            3, R.string.bt_cvc, null),
    UNKNOWN("\\d+",
            R.string.unknown,
            12, 19,
            3, R.string.bt_cvv, null),
    EMPTY("^$",
            R.string.unknown,
            12, 19,
            3, R.string.bt_cvv, null);

    private static final int[] AMEX_SPACE_INDICES = {4, 10};
    private static final int[] DEFAULT_SPACE_INDICES = {4, 8, 12};

    private final Pattern mPattern;
    private final Pattern mRelaxedPrefixPattern;
    private final int mFrontResource;
    private final int mMinCardLength;
    private final int mMaxCardLength;
    private final int mSecurityCodeLength;
    private final int mSecurityCodeName;

    CardType(String regex, int frontResource, int minCardLength, int maxCardLength, int securityCodeLength,
             int securityCodeName, String relaxedPrefixPattern) {
        mPattern = Pattern.compile(regex);
        mRelaxedPrefixPattern = relaxedPrefixPattern == null ? null : Pattern.compile(relaxedPrefixPattern);
        mFrontResource = frontResource;
        mMinCardLength = minCardLength;
        mMaxCardLength = maxCardLength;
        mSecurityCodeLength = securityCodeLength;
        mSecurityCodeName = securityCodeName;
    }
}
