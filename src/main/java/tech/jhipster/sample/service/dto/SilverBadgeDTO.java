package tech.jhipster.sample.service.dto;

import io.micronaut.core.annotation.Introspected;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link tech.jhipster.sample.domain.SilverBadge} entity.
 */
@Introspected
public class SilverBadgeDTO implements Serializable {

    private Long id;

    private String name;

    private IdentifierDTO iden;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IdentifierDTO getIden() {
        return iden;
    }

    public void setIden(IdentifierDTO iden) {
        this.iden = iden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SilverBadgeDTO)) {
            return false;
        }

        SilverBadgeDTO silverBadgeDTO = (SilverBadgeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, silverBadgeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SilverBadgeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", iden=" + getIden() +
            "}";
    }
}
