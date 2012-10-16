package net.sourceforge.cobertura.reporting.generic.filter.criteria;

public class AlwaysMatchCriteria implements Criteria {
    @Override
    public boolean matches(Object element) {
        return true;
    }
}
