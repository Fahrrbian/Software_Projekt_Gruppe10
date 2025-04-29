package com.testat.proj.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testat.proj.app.entity.Pruefung;

@Repository
public interface PruefungRepo extends JpaRepository<Pruefung, Long>{
	
}
