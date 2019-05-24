package com.polytech.qcm.server.qcmserver.repository;

import com.polytech.qcm.server.qcmserver.data.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MySQL repository
 * MySQL requests are handled directly by hibernates (jpa)
 */
@Repository
public interface ResponseRepository extends JpaRepository<Response, Integer> {

  List<Response> findAllByChoice_Id(int id);

  // returns whether a user has already answered for a question
  List<Response> findAllByUser_UsernameAndChoice_Question_Id(String username, Integer questionId);

  //returns all response for a given question
  List<Response> findAllByChoice_Question_Id(Integer questionId);

}
