package co.blastlab.serviceblbnavi.domain;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Grzegorz Konupek
 */
@MappedSuperclass
public abstract class CustomIdGenerationEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "MyIdGenerator")
    @GenericGenerator(name = "MyIdGenerator", strategy = "co.blastlab.serviceblbnavi.domain.CustomIdGenerator")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
