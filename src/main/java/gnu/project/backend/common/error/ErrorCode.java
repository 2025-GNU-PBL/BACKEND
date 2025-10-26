package gnu.project.backend.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러가 발생했습니다."),
    MESSAGE_BODY_UNREADABLE(HttpStatus.BAD_REQUEST, "COMMON400", "요청 본문을 읽을 수 없습니다."),
    INVALID_ENUM_FORMAT(HttpStatus.BAD_REQUEST, "COMMON400", "'%s'은(는) 유효한 %s 값이 아닙니다."),


    // auth
    IS_NOT_VALID_SOCIAL(HttpStatus.BAD_REQUEST, "AUTH001", "지원하지 않는 플랫폼 입니다"),
    AUTH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH4001", "토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "AUTH4002", "토큰 정보가 올바르지 않습니다."),
    AUTH_NOT_SUPPORTED_USER_TYPE(HttpStatus.BAD_REQUEST, "AUTH4003", "지원하지 않는 유저 타입입니다."),
    AUTH_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH4004", "존재하지 않는 유저입니다."),
    AUTH_FORBIDDEN(HttpStatus.BAD_REQUEST, "AUTH4005", "해당 리소스에 접근할 권한이 없습니다."),
    OAUTH_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "AUTH4006", "소셜 로그인 중 액세스 토큰 요청에 실패했습니다."),
    OAUTH_USERINFO_RESPONSE_EMPTY(HttpStatus.BAD_REQUEST, "AUTH4007",
        "소셜 로그인 중 사용자 정보 응답이 비어 있습니다."),

    // owner
    OWNER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "OWNER001", "사장을 찾을 수 없습니다."),
    OWNER_PROFILE_IMAGE_NOT_SET(HttpStatus.NOT_FOUND, "OWNER001", "사장의 이미지가 지정되어 있지 않습니다."),
    // Customer EXCEPTION ADD.
    CUSTOMER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "CUSTOMER001", "고객을 찾을 수 없습니다."),
    CUSTOMER_DELETED_EXCEPTION(HttpStatus.BAD_REQUEST, "CUSTOMER002", "탈퇴한 회원입니다."),
    // Image
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE5001", "이미지 파일 업로드에 실패했습니다."),
    IMAGE_FILE_READ_FAILED(HttpStatus.BAD_REQUEST, "IMAGE4001", "업로드된 이미지 파일의 바이트 읽기에 실패했습니다."),
    IMAGE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE5002", "이미지 파일 다운로드에 실패했습니다."),
    IMAGE_FILE_INVALID_NAME(HttpStatus.BAD_REQUEST, "IMAGE4002", "업로드할 이미지 파일명이 없습니다."),
    IMAGE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "IMAGE4003", "지원하지 않는 이미지 형식입니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE5001", "이미지 파일 삭제에 실패했습니다."),

    // makeup
    MAKEUP_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "MAKEUP001", "해당 메이크업 상품을 찾을 수 없습니다"),
    // WeddingHall
    WEDDING_HALL_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "WEDDING_HALL001", "해당 웨딩홀 상품을 찾을 수 없습니다."),
    //Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT001", "존재하지 않는 주문입니다."),
    // 결제 승인 관련
    PAYMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PAYMENT002", "결제에 대한 접근 권한이 없습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT003", "결제 요청 금액이 주문 금액과 일치하지 않습니다."),
    PAYMENT_CONFIRM_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT004", "결제 승인 실패."),

    // 결제 조회/취소 관련
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT005", "결제 내역을 찾을 수 없습니다."),
    PAYMENT_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "PAYMENT006", "이미 취소된 결제입니다."),
    PAYMENT_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT007", "결제 취소 요청에 실패했습니다."),
    PAYMENT_GATEWAY_ERROR(HttpStatus.BAD_REQUEST,"PAYMENT008","결제 취소 실패." ),

    // 웹훅 관련
    UNAUTHORIZED_WEBHOOK(HttpStatus.BAD_REQUEST, "WEBHOOK001", "웹훅 요청이 잘못되었습니다."),

    // 환불 관련
    PAYMENT_REFUND_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "REFUND001", "환불이 허용되지 않은 상태입니다."),
    PAYMENT_REFUND_FAILED(HttpStatus.BAD_REQUEST, "REFUND002", "환불 요청에 실패했습니다."),



    // dress
    DRESS_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "DRESS001", "해당 드레스 상품을 찾을 수 없습니다"),

    // studio
    STUDIO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "STUDIO001", "해당 스튜디오 상품을 찾을 수 없습니다"),

    // product
    PRODUCT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "PRODUCT001", "해당 상품을 찾을 수 없습니다"),

    // reservation
    RESERVATION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "RESERVATION001", "해당 예약을 찾을 수 없습니다");


    private final HttpStatus status;
    private final String code;
    private final String message;


    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }


}
