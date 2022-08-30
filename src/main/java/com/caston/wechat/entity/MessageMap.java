package com.caston.wechat.entity;

import java.util.HashMap;
import java.util.Map;

public class MessageMap {
    public static class Builder {
        public Map<String, Content> map;

        public Builder() {
            this.map = new HashMap<>();
        }

        public Builder put(String field, String value) {
            this.map.put(field, new Content(value));
            return this;
        }

        public Builder put(String field, String value, String color) {
            this.map.put(field, new Content(value, color));
            return this;
        }

        public Map<String, Content> build() {
            return this.map;
        }
    }
}
