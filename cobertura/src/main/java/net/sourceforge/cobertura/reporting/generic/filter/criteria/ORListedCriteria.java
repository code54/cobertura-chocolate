package net.sourceforge.cobertura.reporting.generic.filter.criteria;

import java.util.Set;

public class ORListedCriteria implements Criteria {

    private Set<Criteria> criterias;

    private ORListedCriteria(){}

    public ORListedCriteria(Set<Criteria>criterias){
        this.criterias = criterias;
    }

    @Override
    public boolean matches(Object element) {
        boolean matches = false;
        for(Criteria criteria: criterias){
            matches = matches||criteria.matches(element);
        }
        return matches;
    }
}
