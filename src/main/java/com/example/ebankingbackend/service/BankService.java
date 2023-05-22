package com.example.ebankingbackend.service;

import com.example.ebankingbackend.entities.BankAccount;
import com.example.ebankingbackend.entities.CurrentAccount;
import com.example.ebankingbackend.entities.SavingAccount;
import com.example.ebankingbackend.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public void consulter() {

        BankAccount bankAccount =
                bankAccountRepository.findById("07174c86-658e-4c8e-b061-fafd5bdc42f2").orElse(null);
        if (bankAccount != null) {
            System.out.println("**************************************************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());
            System.out.println(bankAccount.getClass().getSimpleName());
            if (bankAccount instanceof CurrentAccount) {
                System.out.println("Overdraft=>" + ((CurrentAccount) bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount) {
                System.out.println("interestrate=>" + ((SavingAccount) bankAccount).getInterestRate());

            }
            bankAccount.getAccountOperations().forEach(op -> {
                System.out.println("=========================");
                System.out.println(op.getType() + "\t" + op.getOperationDate() + "\t" + op.getAmount());
//                   System.out.println(op.getOperationDate());
//                   System.out.println(op.getAmount());
            });
        }
    }
}

