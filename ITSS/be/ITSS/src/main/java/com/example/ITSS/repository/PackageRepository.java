package com.example.ITSS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ITSS.model.Package;

public interface PackageRepository extends JpaRepository<Package, Long> {}