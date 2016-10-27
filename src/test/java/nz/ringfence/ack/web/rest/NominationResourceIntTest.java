package nz.ringfence.ack.web.rest;

import nz.ringfence.ack.AckApp;

import nz.ringfence.ack.domain.Nomination;
import nz.ringfence.ack.domain.Person;
import nz.ringfence.ack.repository.NominationRepository;
import nz.ringfence.ack.service.NominationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the NominationResource REST controller.
 *
 * @see NominationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AckApp.class)
public class NominationResourceIntTest {

    private static final LocalDate DEFAULT_NOMINATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_NOMINATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_REASON = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAA";
    private static final String UPDATED_VALUE = "BBBBB";

    @Inject
    private NominationRepository nominationRepository;

    @Inject
    private NominationService nominationService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restNominationMockMvc;

    private Nomination nomination;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NominationResource nominationResource = new NominationResource();
        ReflectionTestUtils.setField(nominationResource, "nominationService", nominationService);
        this.restNominationMockMvc = MockMvcBuilders.standaloneSetup(nominationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Nomination createEntity(EntityManager em) {
        Nomination nomination = new Nomination()
                .nominationDate(DEFAULT_NOMINATION_DATE)
                .reason(DEFAULT_REASON)
                .value(DEFAULT_VALUE);
        // Add required entity
        Person person = PersonResourceIntTest.createEntity(em);
        em.persist(person);
        em.flush();
        nomination.setPerson(person);
        return nomination;
    }

    @Before
    public void initTest() {
        nomination = createEntity(em);
    }

    @Test
    @Transactional
    public void createNomination() throws Exception {
        int databaseSizeBeforeCreate = nominationRepository.findAll().size();

        // Create the Nomination

        restNominationMockMvc.perform(post("/api/nominations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(nomination)))
                .andExpect(status().isCreated());

        // Validate the Nomination in the database
        List<Nomination> nominations = nominationRepository.findAll();
        assertThat(nominations).hasSize(databaseSizeBeforeCreate + 1);
        Nomination testNomination = nominations.get(nominations.size() - 1);
        assertThat(testNomination.getNominationDate()).isEqualTo(DEFAULT_NOMINATION_DATE);
        assertThat(testNomination.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testNomination.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void checkNominationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = nominationRepository.findAll().size();
        // set the field null
        nomination.setNominationDate(null);

        // Create the Nomination, which fails.

        restNominationMockMvc.perform(post("/api/nominations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(nomination)))
                .andExpect(status().isBadRequest());

        List<Nomination> nominations = nominationRepository.findAll();
        assertThat(nominations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReasonIsRequired() throws Exception {
        int databaseSizeBeforeTest = nominationRepository.findAll().size();
        // set the field null
        nomination.setReason(null);

        // Create the Nomination, which fails.

        restNominationMockMvc.perform(post("/api/nominations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(nomination)))
                .andExpect(status().isBadRequest());

        List<Nomination> nominations = nominationRepository.findAll();
        assertThat(nominations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = nominationRepository.findAll().size();
        // set the field null
        nomination.setValue(null);

        // Create the Nomination, which fails.

        restNominationMockMvc.perform(post("/api/nominations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(nomination)))
                .andExpect(status().isBadRequest());

        List<Nomination> nominations = nominationRepository.findAll();
        assertThat(nominations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNominations() throws Exception {
        // Initialize the database
        nominationRepository.saveAndFlush(nomination);

        // Get all the nominations
        restNominationMockMvc.perform(get("/api/nominations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(nomination.getId().intValue())))
                .andExpect(jsonPath("$.[*].nominationDate").value(hasItem(DEFAULT_NOMINATION_DATE.toString())))
                .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getNomination() throws Exception {
        // Initialize the database
        nominationRepository.saveAndFlush(nomination);

        // Get the nomination
        restNominationMockMvc.perform(get("/api/nominations/{id}", nomination.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(nomination.getId().intValue()))
            .andExpect(jsonPath("$.nominationDate").value(DEFAULT_NOMINATION_DATE.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingNomination() throws Exception {
        // Get the nomination
        restNominationMockMvc.perform(get("/api/nominations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNomination() throws Exception {
        // Initialize the database
        nominationService.save(nomination);

        int databaseSizeBeforeUpdate = nominationRepository.findAll().size();

        // Update the nomination
        Nomination updatedNomination = nominationRepository.findOne(nomination.getId());
        updatedNomination
                .nominationDate(UPDATED_NOMINATION_DATE)
                .reason(UPDATED_REASON)
                .value(UPDATED_VALUE);

        restNominationMockMvc.perform(put("/api/nominations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedNomination)))
                .andExpect(status().isOk());

        // Validate the Nomination in the database
        List<Nomination> nominations = nominationRepository.findAll();
        assertThat(nominations).hasSize(databaseSizeBeforeUpdate);
        Nomination testNomination = nominations.get(nominations.size() - 1);
        assertThat(testNomination.getNominationDate()).isEqualTo(UPDATED_NOMINATION_DATE);
        assertThat(testNomination.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testNomination.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void deleteNomination() throws Exception {
        // Initialize the database
        nominationService.save(nomination);

        int databaseSizeBeforeDelete = nominationRepository.findAll().size();

        // Get the nomination
        restNominationMockMvc.perform(delete("/api/nominations/{id}", nomination.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Nomination> nominations = nominationRepository.findAll();
        assertThat(nominations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
