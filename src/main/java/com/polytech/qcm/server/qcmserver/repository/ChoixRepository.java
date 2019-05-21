package com.polytech.qcm.server.qcmserver.repository;

import com.polytech.qcm.server.qcmserver.data.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * MySQL repository
 * MySQL requests are handled directly by hibernates (jpa)
 */
@Repository
public interface ChoixRepository extends JpaRepository<Choice, Integer> {



}
