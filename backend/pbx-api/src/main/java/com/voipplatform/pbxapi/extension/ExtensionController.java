package com.voipplatform.pbxapi.extension;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/extensions")
public class ExtensionController {

    private final PsEndpointRepository repository;

    // Single constructor - Spring injects the repository without an annotation.
    public ExtensionController(PsEndpointRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PsEndpoint> list() {
        return repository.findAll();
    }
}