package nz.ringfence.ack.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Nomination.
 */
@Entity
@Table(name = "nomination")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Nomination implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nomination_date", nullable = false)
    private LocalDate nominationDate;

    @NotNull
    @Size(min = 60)
    @Column(name = "reason", nullable = false)
    private String reason;

    @NotNull
    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne
    @NotNull
    private Person person;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getNominationDate() {
        return nominationDate;
    }

    public Nomination nominationDate(LocalDate nominationDate) {
        this.nominationDate = nominationDate;
        return this;
    }

    public void setNominationDate(LocalDate nominationDate) {
        this.nominationDate = nominationDate;
    }

    public String getReason() {
        return reason;
    }

    public Nomination reason(String reason) {
        this.reason = reason;
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getValue() {
        return value;
    }

    public Nomination value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Person getPerson() {
        return person;
    }

    public Nomination person(Person person) {
        this.person = person;
        return this;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Nomination nomination = (Nomination) o;
        if(nomination.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, nomination.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Nomination{" +
            "id=" + id +
            ", nominationDate='" + nominationDate + "'" +
            ", reason='" + reason + "'" +
            ", value='" + value + "'" +
            '}';
    }
}
