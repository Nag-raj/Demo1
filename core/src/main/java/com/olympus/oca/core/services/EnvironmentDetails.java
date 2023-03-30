package com.olympus.oca.core.services;

/**
 * Interface to get details of the execution Environment
 */
public interface EnvironmentDetails {

    /**
     * Returns the current Environment runmode
     * @return runmode based on configuration
     */
    public String getEnvironmentRunMode();

    /**
     * Returns environment type
     * local, dev , stage and prod
     * @return
     */
    public String getEnvironmentType();

}

