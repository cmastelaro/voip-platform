package com.voipplatform.pbxapi.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Incoming request. Validation annotations are enforced by @Valid on the
 * controller method; a violation produces a 400 before any code runs.
 */
public record CreateExtensionRequest(

        @NotBlank(message = "extension number is required")
        @Pattern(regexp = "1[0-9]{3}", message = "extension must be four digits starting with 1")
        String extension,

        @NotBlank(message = "display name is required")
        @Size(max = 60)
        String displayName,

        @Size(min = 12, message = "password must be at least 12 characters")
        String password
) {
}
