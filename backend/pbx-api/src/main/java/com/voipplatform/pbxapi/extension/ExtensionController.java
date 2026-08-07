package com.voipplatform.pbxapi.extension;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/extensions")
public class ExtensionController {

    private final ExtensionService service;

    public ExtensionController(ExtensionService service) {
        this.service = service;
    }

    @GetMapping
    public List<ExtensionResponse> list() {
        return service.list();
    }

    @PostMapping
    public ResponseEntity<ExtensionResponse> create(@Valid @RequestBody CreateExtensionRequest request) {
        ExtensionResponse created = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/extensions/" + created.extension()))
                .body(created);
    }

    @DeleteMapping("/{extension}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String extension) {
        service.delete(extension);
    }
}
