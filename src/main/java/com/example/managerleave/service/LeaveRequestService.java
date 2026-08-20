package com.example.managerleave.service;

import com.example.managerleave.model.LeaveRequest;
import com.example.managerleave.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public List<LeaveRequest> getAllLeaves() {
        return leaveRequestRepository.findAll();
    }

    public Optional<LeaveRequest> getLeaveById(Long id) {
        return leaveRequestRepository.findById(id);
    }

    public LeaveRequest createLeave(LeaveRequest leaveRequest) {
        leaveRequest.setStatus("PENDING");
        return leaveRequestRepository.save(leaveRequest);
    }

    public void deleteLeave(Long id) {
        leaveRequestRepository.deleteById(id);
    }


    public LeaveRequest approveLeave(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Leave request not found"));

        leaveRequest.setStatus("APPROVED");

        return leaveRequestRepository.save(leaveRequest);

    }

    public LeaveRequest rejectLeave(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Leave request not found"));

        leaveRequest.setStatus("REJECTED");

        return leaveRequestRepository.save(leaveRequest);
    }
}
