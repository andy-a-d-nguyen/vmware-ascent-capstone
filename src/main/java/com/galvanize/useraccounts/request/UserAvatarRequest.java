package com.galvanize.useraccounts.request;

public class UserAvatarRequest {
    private String url;

    public UserAvatarRequest() {}

    public UserAvatarRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
