package com.example.ebankingbackend.service;

import com.example.ebankingbackend.entities.*;
import com.example.ebankingbackend.enums.OperationType;
import com.example.ebankingbackend.exceptions.BalanceNotSuffisantException;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;
import com.example.ebankingbackend.repositories.AccountOperationRepository;
import com.example.ebankingbackend.repositories.BankAccountRepository;
import com.example.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
//on peut utilise un constructeur avec tous les paramettre ou l'annotation autowired  pour chaque objet ou l'annotation allargsconstructor
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
//    Logger log= LoggerFactory.getLogger(this.getClass().getName());
//    si ona enlover l'objet log il suffit d'ajouter l'annotation @slf4j
    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("saving new customer");
       Customer savedCustomer= customerRepository.save(customer);
        return savedCustomer;
    }

    @Override
    public CurrentAccount saveCurrentBankAcount(double initalBalance, double overdraft, Long customerId) throws ClassNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null){
            throw new CustomerNotFoundException("costumer not found");
        }

        CurrentAccount currentAccount=new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initalBalance);
        currentAccount.setOverDraft(overdraft);
        currentAccount.setCustomer(customer);
        CurrentAccount savedBankAccount=bankAccountRepository.save(currentAccount);
        return savedBankAccount ;
    }

    @Override
    public SavingAccount saveSavingBankAcount(double initalBalance, double interestRate, Long customerId) throws ClassNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null){
            throw new CustomerNotFoundException("costumer not found");
        }

        SavingAccount savingAccount=new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initalBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        SavingAccount savedBankAccount=bankAccountRepository.save(savingAccount);
        return savedBankAccount ;

    }


    @Override
    public List<Customer> listCustomer() {
        return customerRepository.findAll() ;
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount  Not found"));
        return bankAccount;
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException ,BalanceNotSuffisantException{
        BankAccount bankAccount=getBankAccount(accountId);
        if (bankAccount.getBalance()<amount){
              throw new BalanceNotSuffisantException("Balance not suffisant");}
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT );
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);



    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException ,BalanceNotSuffisantException{
        BankAccount bankAccount=getBankAccount(accountId);

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException {
    debit(accountIdSource,amount,"Transfer to"+accountIdDestination);
    credit(accountIdDestination,amount,"Transfer from"+accountIdSource);
    }
    @Override
    public List <BankAccount> bankAccountList(){
        return bankAccountRepository.findAll();
    }
}
