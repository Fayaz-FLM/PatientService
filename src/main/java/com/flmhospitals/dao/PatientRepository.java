package com.flmhospitals.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.flmhospitals.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
	
	@Query(value = "SELECT patient_id FROM patients ORDER BY patient_id DESC LIMIT 1", nativeQuery = true)
	String findLastPatientId();
	
	List<Patient> findBypatientIdIn(List<String> listOfPatientIds);
	
	boolean existsByPatientEmail(String email);
	
	boolean existsByPatientPhoneNumber(String phoneNumber);

}
