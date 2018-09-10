package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@Cacheable
@ToString
public class Tag extends Uwb {

}