package com.voipplatform.pbxapi.extension;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class ExtensionService {

    private static final String DEFAULT_CONTEXT = "from-internal";
    private static final String DEFAULT_TRANSPORT = "transport-udp";
    private static final String DEFAULT_CODECS = "ulaw,alaw";

    private final PsEndpointRepository endpoints;
    private final PsAuthRepository auths;
    private final PsAorRepository aors;
    private final SecureRandom random = new SecureRandom();

    public ExtensionService(PsEndpointRepository endpoints,
                            PsAuthRepository auths,
                            PsAorRepository aors) {
        this.endpoints = endpoints;
        this.auths = auths;
        this.aors = aors;
    }

    @Transactional(readOnly = true)
    public List<ExtensionResponse> list() {
        return endpoints.findAll().stream()
                .map(ExtensionResponse::from)
                .toList();
    }

    /**
     * Creates the three rows an endpoint requires, atomically.
     *
     * An endpoint without its auth or AOR is registerable-looking but broken:
     * REGISTER fails with a 500 and no clear cause. All three succeed or none do.
     */
    @Transactional
    public ExtensionResponse create(CreateExtensionRequest request) {

        String id = request.extension();

        if (endpoints.existsById(id)) {
            throw new ExtensionAlreadyExistsException(id);
        }

        String password = (request.password() == null || request.password().isBlank())
                ? generatePassword()
                : request.password();

        aors.save(new PsAor(id, 2, "yes", 30));
        auths.save(new PsAuth(id, "userpass", id, password));

        PsEndpoint endpoint = new PsEndpoint();
        endpoint.setId(id);
        endpoint.setTransport(DEFAULT_TRANSPORT);
        endpoint.setAors(id);
        endpoint.setAuth(id);
        endpoint.setContext(DEFAULT_CONTEXT);
        endpoint.setDisallow("all");
        endpoint.setAllow(DEFAULT_CODECS);
        endpoint.setCallerid(request.displayName() + " <" + id + ">");
        endpoint.setDirectMedia("no");
        endpoint.setForceReport("yes");
        endpoint.setRewriteContact("yes");

        endpoints.save(endpoint);

        return ExtensionResponse.withPassword(endpoint, password);
    }

    @Transactional
    public void delete(String id) {
        if (!endpoints.existsById(id)) {
            throw new ExtensionNotFoundException(id);
        }
        endpoints.deleteById(id);
        auths.deleteById(id);
        aors.deleteById(id);
    }

    private String generatePassword() {
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
