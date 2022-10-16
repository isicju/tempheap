package com.example.demo.controllers;

import com.example.demo.dao.Employee;
import com.example.demo.dao.EmployeeDetails;
import com.example.demo.dao.UserRepository;
import com.example.demo.model.MailDetails;
import com.example.demo.model.ReportRequest;
import com.example.demo.services.MailService;
import com.example.demo.services.PdfGeneratorService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping(path = "/employees")
public class EmployeeController {

    final UserRepository userRepository;
    final PdfGeneratorService pdfService;
    final MailService mailService;

    @GetMapping(value = "/", produces = "application/json")
    public List<Employee> getEmployees() {
        return userRepository.getEmployees();
    }

    @GetMapping("/details/{id}")
    public EmployeeDetails getFullEmployeeDetails(@PathVariable("id") Integer id) {
        return userRepository.getEmployeeFullDetails(id);
    }

    @PostMapping(value = "/report", produces = "application/pdf")
    public @ResponseBody
    String getPdf(@Valid @RequestBody ReportRequest reportRequest) {
        EmployeeDetails employeeDetails =
                userRepository.getEmployeeFullDetails(reportRequest.getEmployeeId());
        return pdfService.generatePdf(employeeDetails, reportRequest.getReportMessage());
    }

    @PostMapping(value = "/mail")
    public @ResponseBody
    ResponseEntity sendPdfViaEmail(@RequestBody String mailDetailsJson) throws Exception {
        log.info("mail body: " + mailDetailsJson);
        System.out.println("mail body: " + mailDetailsJson);
        MailDetails mailDetails = MailDetails.buildAndValidate(mailDetailsJson);
        EmployeeDetails employeeDetails =
                userRepository.getEmployeeFullDetails(mailDetails.getEmployeeId());
        log.info("mail employee details: " + employeeDetails);
        System.out.println("mail employee details: " + employeeDetails);
        pdfService.generatePdf(employeeDetails, mailDetails.getReportMessage());
        System.out.println("generating pdf done " + employeeDetails);
        System.out.println("sending email to  " + mailDetails.getEmail());
        mailService.sendEmail(mailDetails.getEmail());
        return ResponseEntity.status(200).build();
    }

}
