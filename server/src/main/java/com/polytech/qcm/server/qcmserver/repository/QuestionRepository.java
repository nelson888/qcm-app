package com.polytech.qcm.server.qcmserver.repository;

import com.polytech.qcm.server.qcmserver.data.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MySQL repository
 * MySQL requests are handled directly by hibernates (jpa)
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

  Optional<Question> findById(int id);
}
