package com.pblues.sportsshop.common.constant;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Auth
    BAD_CREDENTIALS("AUTH_001", "Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH_002", "Tài khoản bị khóa", HttpStatus.LOCKED),
    INVALID_TOKEN("AUTH_003", "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),

    // OTP
    INVALID_OTP("OTP_001", "Mã OTP không hợp lệ", HttpStatus.BAD_REQUEST),
    OTP_EXCEEDED("OTP_002", "Bạn đã vượt quá số lần nhập OTP cho phép. Vui lòng thử lại vào ngày mai", HttpStatus.BAD_REQUEST),

    // User / Resource
    ACCOUNT_ALREADY_EXISTS("USR_001", "Tài khoản đã tồn tại", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("GEN_001", "Không tìm thấy tài nguyên", HttpStatus.NOT_FOUND),
    ACCOUNT_NOT_FOUND("GEN_002", "Tài khoản không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("GEN_003", "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    VARIANT_NOT_FOUND("GEN_004", "Lựa chọn không tồn tại", HttpStatus.NOT_FOUND),
    INVENTORY_NOT_FOUND("GEN_05", "Không tìm thấy kho sản phẩm", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND("GEN_06", "Không tìm thấy địa chỉ giao hàng của bạn", HttpStatus.NOT_FOUND),
    OPERATION_NOT_PERMITTED("GEN_000", "Không được phép thực hiện thao tác này", HttpStatus.FORBIDDEN),

    // Order
    QUANTITY_EXCEEDS_STOCK("ORD_001", "Số lượng vượt quá tồn kho", HttpStatus.CONFLICT),
    PRICE_CHANGED("ORD_002", "Giá sản phẩm đã thay đổi", HttpStatus.CONFLICT);


    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public HttpStatus getStatus() { return status; }
}
