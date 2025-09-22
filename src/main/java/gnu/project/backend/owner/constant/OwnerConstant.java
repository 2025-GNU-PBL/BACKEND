package gnu.project.backend.owner.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OwnerConstant {

    public static final String PROFILE_IMAGE_SIZE_MESSAGE = "프로필 이미지 URL은 500자를 초과할 수 없습니다.";

    public static final String AGE_REQUIRED_MESSAGE = "나이는 필수 입력값입니다.";
    public static final String AGE_MIN_MESSAGE = "사업자는 18세 이상이어야 합니다.";
    public static final String AGE_MAX_MESSAGE = "나이는 120세를 초과할 수 없습니다.";

    public static final String PHONE_NUMBER_REQUIRED_MESSAGE = "전화번호는 필수 입력값입니다.";
    public static final String PHONE_NUMBER_PATTERN_MESSAGE = "전화번호는 010-0000-0000 형식이어야 합니다.";

    public static final String BZ_NUMBER_REQUIRED_MESSAGE = "사업자등록번호는 필수 입력값입니다.";
    public static final String BZ_NUMBER_PATTERN_MESSAGE = "사업자등록번호는 000-00-00000 형식이어야 합니다.";

    public static final String BANK_ACCOUNT_REQUIRED_MESSAGE = "계좌번호는 필수 입력값입니다.";
    public static final String BANK_ACCOUNT_PATTERN_MESSAGE = "계좌번호는 숫자와 하이픈(-) 만 입력 가능합니다.";
    public static final String BANK_ACCOUNT_SIZE_MESSAGE = "계좌번호는 10자 이상 20자 이하여야 합니다.";


}
