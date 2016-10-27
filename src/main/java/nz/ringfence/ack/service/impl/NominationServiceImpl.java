package nz.ringfence.ack.service.impl;

import nz.ringfence.ack.service.NominationService;
import nz.ringfence.ack.domain.Nomination;
import nz.ringfence.ack.repository.NominationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Nomination.
 */
@Service
@Transactional
public class NominationServiceImpl implements NominationService{

    private final Logger log = LoggerFactory.getLogger(NominationServiceImpl.class);
    
    @Inject
    private NominationRepository nominationRepository;

    /**
     * Save a nomination.
     *
     * @param nomination the entity to save
     * @return the persisted entity
     */
    public Nomination save(Nomination nomination) {
        log.debug("Request to save Nomination : {}", nomination);
        Nomination result = nominationRepository.save(nomination);
        return result;
    }

    /**
     *  Get all the nominations.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Nomination> findAll(Pageable pageable) {
        log.debug("Request to get all Nominations");
        Page<Nomination> result = nominationRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one nomination by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Nomination findOne(Long id) {
        log.debug("Request to get Nomination : {}", id);
        Nomination nomination = nominationRepository.findOne(id);
        return nomination;
    }

    /**
     *  Delete the  nomination by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Nomination : {}", id);
        nominationRepository.delete(id);
    }
}
