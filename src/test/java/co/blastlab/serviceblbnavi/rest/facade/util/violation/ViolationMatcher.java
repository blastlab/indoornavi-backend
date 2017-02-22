package co.blastlab.serviceblbnavi.rest.facade.util.violation;

import org.hamcrest.CustomMatcher;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class ViolationMatcher {
    public static CustomMatcher<SpecificViolation> validViolation(final String path, final String message) {
        return new CustomMatcher<SpecificViolation>(String.format("Expected path = %s and message = %s", path, message)) {
            @Override
            public boolean matches(Object o) {
                assertThat(o, instanceOf(SpecificViolation.class));
                SpecificViolation violation = (SpecificViolation) o;
                return violation.getPath().matches(String.format("arg\\d+\\.%s", path)) && Objects.equals(violation.getMessages().get(0), message);
            }
        };
    }
}
