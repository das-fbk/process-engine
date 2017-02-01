package eu.fbk.das.process.engine.impl;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.ProcessEngine;

/**
 * Manage correlation between processes
 */
public class CorrelationManager {

    private ArrayListMultimap<DomainObjectInstance, DomainObjectInstance> correlations = ArrayListMultimap
	    .create();
    private ProcessEngine pe;

    public CorrelationManager(ProcessEngine pe) {
	this.pe = pe;
    }

    /**
     * remove all correlation for a given process pid (process id )
     * 
     * @param key
     * @return value
     */
    public void remove(DomainObjectInstance key, DomainObjectInstance value) {
	if (correlations.containsKey(key)) {
	    correlations.get(key).remove(value);
	}
    }

    /**
     * Create a correlation between two different {@link DomainObjectInstance}.
     * Two DomainObjectInstance are different when respective Id are different
     * 
     * @param first
     * @param second
     */
    public void create(DomainObjectInstance first, DomainObjectInstance second) {
	if (first.getId().equals(second.getId())) {
	    return;
	}
	if (!correlations.get(first).contains(second)) {
	    correlations.put(first, second);
	}
	if (!correlations.get(second).contains(first)) {
	    correlations.put(second, first);
	}
    }

    /**
     * @param pid
     * @return correlated pid
     */
    public List<DomainObjectInstance> get(DomainObjectInstance doi) {
	return correlations.get(doi);
    }

    /**
     * Check if given pid have correlations
     * 
     * @param pid
     * @return true if any
     */
    public boolean hasCorrelation(DomainObjectInstance doi) {
	return !correlations.get(doi).isEmpty();
    }

    public boolean isEmpty() {
	return correlations == null ? true : correlations.isEmpty();
    }

    /**
     * @param first
     * @param second
     * @return true if there is correlation between first and second
     */
    public boolean isCorrelated(DomainObjectInstance first,
	    DomainObjectInstance second) {
	if (correlations.get(first).contains(second)) {
	    return true;
	}
	return false;
    }

}
