package nz.ringfence.ack.service;

import nz.ringfence.ack.domain.Nomination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Nomination.
 */
public interface NominationService {

    /**
     * Save a nomination.
     *
     * @param nomination the entity to save
     * @return the persisted entity
     */
    Nomination save(Nomination nomination);

    /**
     *  Get all the nominations.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Nomination> findAll(Pageable pageable);

    /**
     *  Get the "id" nomination.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Nomination findOne(Long id);

    /**
     *  Delete the "id" nomination.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
}
