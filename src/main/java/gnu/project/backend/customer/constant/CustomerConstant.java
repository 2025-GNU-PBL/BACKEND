package gnu.project.backend.customer.constant;


import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerConstant {

    // age
    public static final String AGE_REQUIRED_MESSAGE = "나이는 필수 입력값입니다.";
    public static final String AGE_MAX_MESSAGE = "나이는 120세 이하이어야 합니다.";

    // phone
    public static final String PHONE_NUMBER_REQUIRED_MESSAGE = "전화번호는 필수입니다.";
    public static final String PHONE_NUMBER_PATTERN_MESSAGE = "전화번호는 010-0000-0000 형식이어야 합니다.";

    // address
    public static final String ADDRESS_SIZE_MESSAGE = "주소는 최대 255자까지 허용됩니다.";

    // wedding
    public static final String SIDO_SIZE_MESSAGE = "시/도는 최대 30자까지 허용됩니다.";
    public static final String SIGUNGU_SIZE_MESSAGE = "시/군/구는 최대 50자까지 허용됩니다.";

}

