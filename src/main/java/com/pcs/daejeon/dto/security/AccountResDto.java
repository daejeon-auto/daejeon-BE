package com.pcs.daejeon.dto.security;

public class AccountResDto {
    private String result;
    private String message;

    public static AccountResDto success(Object data) {
        return new AccountResDto("success", null);
    }

    public static AccountResDto success(Object data, String value) {
        return new AccountResDto("success", value);
    }

    public static AccountResDto fail(String message) {
        return new AccountResDto("fail", message);
    }

    private AccountResDto(String result, String message) {
        this.result = result;
        this.message = message;
    }

    public AccountResDto() {
        super();
        // TODO Auto-generated constructor stub
    }


    public void setResult(String result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "AccountResDto [result=" + result + ", message=" + message + "]";
    }
}
