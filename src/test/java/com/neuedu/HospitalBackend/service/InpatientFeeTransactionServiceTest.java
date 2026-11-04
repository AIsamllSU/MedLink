package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.InpatientFeeLedgerDTO;
import com.neuedu.HospitalBackend.entity.InpatientAdmission;
import com.neuedu.HospitalBackend.entity.InpatientFeeTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InpatientFeeTransactionServiceTest {
    @Autowired
    InpatientFeeTransactionService transactionService;

    @Test
    void listAdmissions() {
        List<InpatientAdmission> list = transactionService.listAdmissions();
        for(InpatientAdmission admission:list){
            System.out.println(admission);
        }
    }

    @Test
    void testLedger(){
        InpatientAdmission admission = new InpatientAdmission();
        admission.setId(1L);
        InpatientFeeLedgerDTO ledger = transactionService.ledger(admission.getId());
        System.out.println(ledger);
    }
}