package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Complex;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class ComplexBean {

    @Inject
    private ComplexRepository complexRepository;

    public List<Complex> findAllByPerson(Long personId) {
        List<Complex> complexes = complexRepository.findAllByPerson(personId);
        Set<Complex> complexSet = new HashSet<>(complexes);
        complexes = new ArrayList<>(complexSet);

        complexes.forEach((complex) -> {
            List<String> permissions = new ArrayList<>();
            complex.getACL_complexes().stream().forEach((aclComplex) -> {
                if (Objects.equals(aclComplex.getPerson().getId(), personId)) {
                    permissions.add(aclComplex.getPermission().getName());
                }
            });
            complex.setPermissions(permissions);
        });

        return complexes;
    }
}
