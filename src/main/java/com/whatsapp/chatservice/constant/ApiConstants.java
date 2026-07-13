package com.whatsapp.chatservice.constant;

public final class ApiConstants {

    public static final String API_BASE_PATH = "/api/v1/chats";
    public static final String API_CONVERSATIONS_PATH = API_BASE_PATH + "/conversations";
    public static final String API_MESSAGES_PATH = API_BASE_PATH + "/messages";

    public static final String PROBLEM_DETAIL_TYPE = "about:blank";
    public static final String PROBLEM_DETAIL_TITLE_VALIDATION = "Validation Failed";
    public static final String PROBLEM_DETAIL_TITLE_NOT_FOUND = "Not Found";
    public static final String PROBLEM_DETAIL_TITLE_CONFLICT = "Conflict";
    public static final String PROBLEM_DETAIL_TITLE_BAD_REQUEST = "Bad Request";
    public static final String PROBLEM_DETAIL_TITLE_FORBIDDEN = "Forbidden";

    private ApiConstants() {
    }
}
