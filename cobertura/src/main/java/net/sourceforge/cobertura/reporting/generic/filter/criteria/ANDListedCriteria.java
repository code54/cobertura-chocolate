package net.sourceforge.cobertura.reporting.generic.filter.criteria;

import java.util.Set;

public class ANDListedCriteria implements Criteria {

    private Set<Criteria> criterias;

    private ANDListedCriteria(){}

    public ANDListedCriteria(Set<Criteria>criterias){
        this.criterias = criterias;
    }

    @Override
    public boolean matches(Object element) {
        boolean matches = true;
        for(Criteria criteria: criterias){
            matches = matches&&criteria.matches(element);
        }
        return matches;
    }
}
