package com.nonmus.nonmus.modules.user.events;

public class S3DeleteProfilePictureEvent {
    private final String key;

    public S3DeleteProfilePictureEvent(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
