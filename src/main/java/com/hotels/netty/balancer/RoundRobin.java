package com.hotels.netty.balancer;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoundRobin {

    private Iterator<Instance> instancesIterator;
    private final List<Instance> availableInstances;

    public RoundRobin() {

        availableInstances = new ArrayList<>();
        availableInstances.add(new Instance("192.168.99.100", 8080));
        availableInstances.add(new Instance("192.168.99.100", 8081));
        availableInstances.add(new Instance("192.168.99.100", 8082));
        instancesIterator = availableInstances.iterator();
    }

    public final Instance getNext() {

        if (instancesIterator.hasNext()) {
            Instance nextInstance = instancesIterator.next();
            System.out.println("Round robin strategy. Retrieving next instance : " + nextInstance);
            return nextInstance;
        }

        instancesIterator = availableInstances.iterator();
        return instancesIterator.next();
    }
}
