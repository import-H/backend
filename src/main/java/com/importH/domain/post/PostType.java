package com.importH.domain.post;


import lombok.Getter;

@Getter
public enum PostType {

    FREE("free"),
    QUESTIONS("questions"),
    NOTICE("notice");

    private String type;

    PostType(String type) {
        this.type = type;
    }
}
