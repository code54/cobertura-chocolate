package net.sourceforge.cobertura.reporting.generic.filter.criteria;

public class EqCriteria implements Criteria {

    private Object match;

    private EqCriteria(){}

    public EqCriteria(Object match){
        this.match = match;
    }

    @Override
    public boolean matches(Object element) {
        return match.equals(element);
    }
}
