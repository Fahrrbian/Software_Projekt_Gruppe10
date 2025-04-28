package com.testat.proj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testat.proj.model.Pruefung;

@Repository
public interface PruefungRepo extends JpaRepository<Pruefung, Long>{
	
}
