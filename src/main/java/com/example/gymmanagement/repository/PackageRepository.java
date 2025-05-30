package com.example.gymmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gymmanagement.model.Package;

public interface PackageRepository extends JpaRepository<Package, Long> {}
