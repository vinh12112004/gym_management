package com.example.gym.service;

import com.example.gym.model.Package;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PackageService {
    private final Map<Long, Package> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Package create(Package pkg) {
        long id = seq.getAndIncrement();
        pkg.setId(id);
        store.put(id, pkg);
        return pkg;
    }

    public List<Package> listAll() {
        return new ArrayList<>(store.values());
    }

    public boolean extendPackage(long id, int days) {
        Package pkg = store.get(id);
        if (pkg == null) return false;
        pkg.setExpiryDate(pkg.getExpiryDate().plusDays(days));
        return true;
    }

    public boolean deletePackage(long id) {
        return store.remove(id) != null;
    }
}