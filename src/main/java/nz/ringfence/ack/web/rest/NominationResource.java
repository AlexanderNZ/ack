package nz.ringfence.ack.web.rest;

import com.codahale.metrics.annotation.Timed;
import nz.ringfence.ack.domain.Nomination;
import nz.ringfence.ack.domain.Person;
import nz.ringfence.ack.service.NominationService;
import nz.ringfence.ack.service.PersonService;
import nz.ringfence.ack.web.rest.util.HeaderUtil;
import nz.ringfence.ack.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Nomination.
 */
@RestController
@RequestMapping("/api")
public class NominationResource {

    private final Logger log = LoggerFactory.getLogger(NominationResource.class);

    @Inject
    private NominationService nominationService;

    @Inject
    private PersonService personService;

    /**
     * POST  /nominations : Create a new nomination.
     *
     * @param nomination the nomination to create
     * @return the ResponseEntity with status 201 (Created) and with body the new nomination, or with status 400 (Bad Request) if the nomination has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/nominations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Nomination> createNomination(@Valid @RequestBody Nomination nomination) throws URISyntaxException {
        log.debug("REST request to save Nomination : {}", nomination);
        if (nomination.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("nomination", "idexists", "A new nomination cannot already have an ID")).body(null);
        }

        modifyCount(nomination, true);

        // Save as per normal
        Nomination result = nominationService.save(nomination);
        return ResponseEntity.created(new URI("/api/nominations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("nomination", result.getId().toString()))
            .body(result);
    }

    private void modifyCount(@Valid @RequestBody Nomination nomination, boolean increase) {
        int delta;

        if (increase) {
            delta = 1;
        } else {
            delta = -1;}

        switch (nomination.getValue()) {
            case "Grow here":
                nomination.getPerson().setGrowCount(nomination.getPerson().getGrowCount() + delta);
                break;
            case "Create with Purpose":
                nomination.getPerson().setCreateCount(nomination.getPerson().getCreateCount() + delta);
                break;
            case "Be great Together":
                nomination.getPerson().setTogetherCount(nomination.getPerson().getTogetherCount() + delta);
                break;
            case "Express with Integrity":
                nomination.getPerson().setExpressCount(nomination.getPerson().getExpressCount() + delta);
                break;
            default: log.error("Value count increment failed - there was a problem in createNomination() in NominationResource.java");
        }

        // Update person separately
        personService.save(nomination.getPerson());
    }

    /**
     * PUT  /nominations : Updates an existing nomination.
     *
     * @param nomination the nomination to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated nomination,
     * or with status 400 (Bad Request) if the nomination is not valid,
     * or with status 500 (Internal Server Error) if the nomination couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/nominations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Nomination> updateNomination(@Valid @RequestBody Nomination nomination) throws URISyntaxException {
        log.debug("REST request to update Nomination : {}", nomination);
        if (nomination.getId() == null) {
            return createNomination(nomination);
        }
        Nomination result = nominationService.save(nomination);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("nomination", nomination.getId().toString()))
            .body(result);
    }

    /**
     * GET  /nominations : get all the nominations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of nominations in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/nominations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Nomination>> getAllNominations(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Nominations");
        Page<Nomination> page = nominationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/nominations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /nominations/:id : get the "id" nomination.
     *
     * @param id the id of the nomination to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the nomination, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/nominations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Nomination> getNomination(@PathVariable Long id) {
        log.debug("REST request to get Nomination : {}", id);
        Nomination nomination = nominationService.findOne(id);
        return Optional.ofNullable(nomination)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /nominations/:id : delete the "id" nomination.
     *
     * @param id the id of the nomination to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/nominations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNomination(@PathVariable Long id) {
        log.debug("REST request to delete Nomination : {}", id);

        // Get the nomination

        // Get the person the nomination concerns
        Nomination nomination = nominationService.findOne(id);
        // Decrement the appropriate count
        modifyCount(nomination, false);

        // If that was successful, delete the nomination
        nominationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("nomination", id.toString())).build();

        // else return an error

    }

}
