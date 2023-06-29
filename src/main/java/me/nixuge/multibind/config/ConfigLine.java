package me.nixuge.multibind.config;

import lombok.Getter;

@Getter
public class ConfigLine {
    private final String keyDesc;
    private final int listId;
    private final int keyCode;
    public ConfigLine(String keyDesc, String listId, String keyCode) {
        this.keyDesc = keyDesc;
        this.listId = Integer.parseInt(listId);
        this.keyCode = Integer.parseInt(keyCode);
    }
}
