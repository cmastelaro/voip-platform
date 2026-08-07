package com.voipplatform.pbxapi.extension;

/**
 * Outgoing representation. The SIP password is returned only on creation,
 * never on subsequent reads.
 */
public record ExtensionResponse(
        String extension,
        String displayName,
        String context,
        String codecs,
        String sipPassword
) {

    public static ExtensionResponse from(PsEndpoint endpoint) {
        return new ExtensionResponse(
                endpoint.getId(),
                endpoint.getCallerid(),
                endpoint.getContext(),
                endpoint.getAllow(),
                null
        );
    }

    public static ExtensionResponse withPassword(PsEndpoint endpoint, String password) {
        return new ExtensionResponse(
                endpoint.getId(),
                endpoint.getCallerid(),
                endpoint.getContext(),
                endpoint.getAllow(),
                password
        );
    }
}
