package gnu.project.backend.product.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductConstant {

    // 공통 메시지
    public static final String MAKEUP_DELETE_SUCCESS = "메이크업 삭제를 성공적으로 삭제하였습니다.";
    public static final String DRESS_DELETE_SUCCESS = "메이크업 삭제를 성공적으로 삭제하였습니다.";
    public static final String WEDDING_HALL_DELETE_SUCCESS = "웨딩홀 상품을 성공적으로 삭제하였습니다.";
    public static final String DEFAULT_SORT_TYPE = "LATEST";

    public static final String SEARCH_KEY_PREFIX = "recent_search:";
    public static final int MAX_SEARCH_HISTORY = 10;
    // --- 필수 입력 메시지 ---
    public static final String NAME_REQUIRED = "상품명은 필수입니다";
    public static final String PRICE_REQUIRED = "가격은 필수입니다";
    public static final String ADDRESS_REQUIRED = "주소는 필수입니다";
    public static final String DETAIL_REQUIRED = "상세 설명은 필수입니다";
    public static final String OPTION_NAME_REQUIRED = "옵션명은 필수입니다";
    public static final String OPTION_PRICE_REQUIRED = "옵션 가격은 필수입니다";
    public static final String REGION_REQUIRE = "지역 입력은 필수입니다.";
    public static final String OPTION_LIMIT = "옵션은 최대 10개까지 추가 가능합니다";

    // --- 길이 제한 메시지 ---
    public static final String NAME_LENGTH = "상품명은 10000자 이하여야 합니다";
    public static final String ADDRESS_LENGTH = "주소는 500자 이하여야 합니다";
    public static final String DETAIL_LENGTH = "상세 설명은 10000자 이하여야 합니다";
    public static final String AVAILABLE_TIMES_LENGTH = "이용 가능 시간은 100자 이하여야 합니다";
    public static final String OPTION_NAME_LENGTH = "옵션명은 50자 이하여야 합니다";
    public static final String OPTION_DETAIL_LENGTH = "옵션 설명은 200자 이하여야 합니다";

    // --- 최소값 메시지 ---
    public static final String PRICE_MIN = "가격은 0 이상이어야 합니다";
    public static final String OPTION_PRICE_MIN = "옵션 가격은 0 이상이어야 합니다";

    // --- 필드 제한값 ---
    public static final int MAX_NAME_LENGTH = 3000;
    public static final int MAX_ADDRESS_LENGTH = 500;
    public static final int MAX_DETAIL_LENGTH = 3000;
    public static final int MAX_STYLE_LENGTH = 50;
    public static final int MAX_AVAILABLE_TIMES = 3000;
    public static final int MAX_TYPE_LENGTH = 50;
    public static final int MAX_OPTION_COUNT = 10;
    public static final int MAX_OPTION_NAME_LENGTH = 500;
    public static final int MAX_OPTION_DETAIL_LENGTH = 20000;

    // --- 최소값 ---
    public static final int MIN_PRICE = 0;
    public static final int MIN_OPTION_PRICE = 0;
    //
    public static final String STRING_COLUMN_DEFINITION = "LONGTEXT";
}
