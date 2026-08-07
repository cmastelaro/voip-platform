package com.voipplatform.pbxapi.extension;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * No implementation is written. Spring Data generates one at runtime from
 * the interface and the entity mapping.
 *
 * JpaRepository<PsEndpoint, String> reads as: entity type, primary key type.
 * It supplies findAll, findById, save, deleteById and others.
 */
public interface PsEndpointRepository extends JpaRepository<PsEndpoint, String> {
}