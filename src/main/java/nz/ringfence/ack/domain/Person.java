package nz.ringfence.ack.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Person.
 */
@Entity
@Table(name = "person")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Min(value = 0)
    @Column(name = "grow_count", nullable = false)
    private Integer growCount;

    @NotNull
    @Min(value = 0)
    @Column(name = "create_count", nullable = false)
    private Integer createCount;

    @NotNull
    @Min(value = 0)
    @Column(name = "together_count", nullable = false)
    private Integer togetherCount;

    @NotNull
    @Min(value = 0)
    @Column(name = "express_count", nullable = false)
    private Integer expressCount;

    @OneToMany(mappedBy = "person")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Nomination> nominations = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public Person firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Person lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getGrowCount() {
        return growCount;
    }

    public Person growCount(Integer growCount) {
        this.growCount = growCount;
        return this;
    }

    public void setGrowCount(Integer growCount) {
        this.growCount = growCount;
    }

    public Integer getCreateCount() {
        return createCount;
    }

    public Person createCount(Integer createCount) {
        this.createCount = createCount;
        return this;
    }

    public void setCreateCount(Integer createCount) {
        this.createCount = createCount;
    }

    public Integer getTogetherCount() {
        return togetherCount;
    }

    public Person togetherCount(Integer togetherCount) {
        this.togetherCount = togetherCount;
        return this;
    }

    public void setTogetherCount(Integer togetherCount) {
        this.togetherCount = togetherCount;
    }

    public Integer getExpressCount() {
        return expressCount;
    }

    public Person expressCount(Integer expressCount) {
        this.expressCount = expressCount;
        return this;
    }

    public void setExpressCount(Integer expressCount) {
        this.expressCount = expressCount;
    }

    public Set<Nomination> getNominations() {
        return nominations;
    }

    public Person nominations(Set<Nomination> nominations) {
        this.nominations = nominations;
        return this;
    }

    public Person addNomination(Nomination nomination) {
        nominations.add(nomination);
        nomination.setPerson(this);
        return this;
    }

    public Person removeNomination(Nomination nomination) {
        nominations.remove(nomination);
        nomination.setPerson(null);
        return this;
    }

    public void setNominations(Set<Nomination> nominations) {
        this.nominations = nominations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        if(person.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Person{" +
            "id=" + id +
            ", firstName='" + firstName + "'" +
            ", lastName='" + lastName + "'" +
            ", growCount='" + growCount + "'" +
            ", createCount='" + createCount + "'" +
            ", togetherCount='" + togetherCount + "'" +
            ", expressCount='" + expressCount + "'" +
            '}';
    }
}
