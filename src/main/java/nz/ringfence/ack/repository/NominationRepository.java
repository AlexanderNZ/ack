package nz.ringfence.ack.repository;

import nz.ringfence.ack.domain.Nomination;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Nomination entity.
 */
@SuppressWarnings("unused")
public interface NominationRepository extends JpaRepository<Nomination,Long> {

}
