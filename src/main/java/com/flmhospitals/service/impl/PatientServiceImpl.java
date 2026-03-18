package com.flmhospitals.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.flmhospitals.builder.PatientBuilder;
import com.flmhospitals.clients.AppointmentClient;
import com.flmhospitals.dao.PatientRepository;
import com.flmhospitals.dto.RegisterPatientRequestDto;
import com.flmhospitals.dto.RegisterPatientResponseDto;
import com.flmhospitals.dto.builder.PatientDTOBuilder;
import com.flmhospitals.exception.PatientNotFoundException;
import com.flmhospitals.model.Patient;
import com.flmhospitals.service.PatientService;
import com.flmhospitals.utils.PatientIdGenerator;

@Service
public class PatientServiceImpl implements PatientService {

	public final PatientRepository patientRepository;

	public final PatientIdGenerator patientIdGenerator;
	
	public final AppointmentClient appointmentClient;

	public PatientServiceImpl(PatientRepository patientRepository, PatientIdGenerator patientIdGenerator, AppointmentClient appointmentClient) {
	
		this.patientRepository = patientRepository;
		
		this.patientIdGenerator = patientIdGenerator;
		
		this.appointmentClient = appointmentClient;
	}

	@Override
	public RegisterPatientResponseDto regiesterPatient(RegisterPatientRequestDto registerPatientRequestDto) {
		
		// Check if email already exists
		if (patientRepository.existsByPatientEmail(registerPatientRequestDto.getPatientEmail())) {
			throw new IllegalArgumentException("Email " + registerPatientRequestDto.getPatientEmail() + " is already registered");
		}

		// Check if phone number already exists
		if (patientRepository.existsByPatientPhoneNumber(registerPatientRequestDto.getPatientPhoneNumber())) {
			throw new IllegalArgumentException("Phone number " + registerPatientRequestDto.getPatientPhoneNumber() + " is already registered");
		}

		Patient patient = PatientBuilder.buildPatientFromRegisterPatientRequestDto(registerPatientRequestDto);

		Patient registedPatient = patientRepository.save(patient);

		return PatientDTOBuilder.fromPatientEntityToRegPatientRespDtO(registedPatient);
	}

	@Override
	public RegisterPatientResponseDto updatePatient(RegisterPatientRequestDto patientRequestDto,
			String patientId) {
		
		Patient existingPatient= patientRepository.findById(patientId).orElseThrow(()->new PatientNotFoundException("No patient found with ID "+patientId));
		
		Patient updatedPatient=PatientBuilder.buildPatientFromRegisterPatientRequestDto(patientRequestDto);
		
		updatedPatient.setPatientId(patientId);
		
		if(existingPatient.getPatientAddress()!=null && updatedPatient.getPatientAddress() !=null) {
			
			updatedPatient.getPatientAddress().setPatientAddressId(existingPatient.getPatientAddress().getPatientAddressId());
		}
		
		Patient savedPatient= patientRepository.save(updatedPatient);
		
		return PatientDTOBuilder.fromPatientEntityToRegPatientRespDtO(savedPatient);
		
	}

	@Override
	public RegisterPatientResponseDto getPatientById(String patientId) {
		Patient patient=patientRepository.findById(patientId).orElseThrow(()->new PatientNotFoundException("No patient found with ID "+patientId));
		
		return PatientDTOBuilder.fromPatientEntityToRegPatientRespDtO(patient);
	}

	@Override
	public List<Patient> getPatientsByDoctor(List<String> listOfPatientIds) {
		
		List<Patient> patients = patientRepository.findBypatientIdIn(listOfPatientIds);

		return patients;
	}

	@Override
	public List<String> getPatientsVisitedByDoctor(String staffId, LocalDate startDate, LocalDate endDate) {
		
        String startdate = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		String enddate = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		return appointmentClient.getPatientsVisitedByDoctor(staffId, startdate, enddate);
	}
		
	@Override	
	public String getPatientName(String patientId) {
		
		Patient patient=patientRepository.findById(patientId).orElseThrow(()->new PatientNotFoundException("No patient found with ID "+patientId));
		
		return patient.getPatientName();
	}

	@Override
	public List<RegisterPatientResponseDto> getAllPatients() {
		
		List<Patient> allPatients = patientRepository.findAll();
		
		return PatientDTOBuilder.fromListOfPatientToListOfRegPatientRespDto(allPatients);
		
	}
	
}