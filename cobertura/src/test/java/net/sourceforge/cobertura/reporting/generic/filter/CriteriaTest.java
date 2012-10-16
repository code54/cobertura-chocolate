package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.filter.criteria.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CriteriaTest {
    @Test
    public void testAlwaysMatchCriteria(){
        assertTrue(new AlwaysMatchCriteria().matches(null));
    }

    @Test
    public void testEqCriteria(){
        String match = "match";
        assertTrue(new EqCriteria(match).matches(match));
        assertFalse(new EqCriteria(match).matches("someOtherString"));
    }

    @Test
    public void testORCriteria(){
        String match = "match";
        Criteria alwaysMatch = new AlwaysMatchCriteria();
        Criteria eq = new EqCriteria(match);

        Set<Criteria>criterias = new HashSet<Criteria>();
        criterias.add(alwaysMatch);
        criterias.add(eq);
        Criteria orCriteria = new ORListedCriteria(criterias);

        assertTrue(orCriteria.matches(match));
        assertTrue(orCriteria.matches("someOtherString"));
        assertTrue(orCriteria.matches(null));
    }

    @Test
    public void testANDCriteria(){
        String match = "match";
        Criteria alwaysMatch = new AlwaysMatchCriteria();
        Criteria eq = new EqCriteria(match);

        Set<Criteria>criterias = new HashSet<Criteria>();
        criterias.add(alwaysMatch);
        criterias.add(eq);
        Criteria andCriteria = new ANDListedCriteria(criterias);

        assertTrue(andCriteria.matches(match));
        assertFalse(andCriteria.matches("someOtherString"));
        assertFalse(andCriteria.matches(null));
    }
}
