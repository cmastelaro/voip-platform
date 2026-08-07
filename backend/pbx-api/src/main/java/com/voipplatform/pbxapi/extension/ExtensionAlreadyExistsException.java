package com.voipplatform.pbxapi.extension;

public class ExtensionAlreadyExistsException extends RuntimeException {
    public ExtensionAlreadyExistsException(String id) {
        super("Extension already exists: " + id);
    }
}
