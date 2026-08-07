package com.voipplatform.pbxapi.extension;

public class ExtensionNotFoundException extends RuntimeException {
    public ExtensionNotFoundException(String id) {
        super("Extension not found: " + id);
    }
}
