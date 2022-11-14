package com.pcs.daejeon.dto;

public class AccountResDto {
    private String result; // success, fail
    private String message; // if fail, set
    private Object data; // if success, set data

    public static AccountResDto success(Object data) {
        return new AccountResDto("success", null, data);
    }

    public static AccountResDto success(Object data, String value) {
        return new AccountResDto("success", value, data);
    }

    public static AccountResDto fail(String message) {
        return new AccountResDto("fail", message, null);
    }

    private AccountResDto(String result, String message, Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
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

    public void setData(Object data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "AccountResDto [result=" + result + ", message=" + message + ", data=" + data + "]";
    }
}
