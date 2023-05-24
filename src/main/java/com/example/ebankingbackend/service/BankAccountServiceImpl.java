package com.example.ebankingbackend.service;

import com.example.ebankingbackend.dtos.*;
import com.example.ebankingbackend.entities.*;
import com.example.ebankingbackend.enums.OperationType;
import com.example.ebankingbackend.exceptions.BalanceNotSuffisantException;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;
import com.example.ebankingbackend.mappers.BankAccountMapperImpl;
import com.example.ebankingbackend.repositories.AccountOperationRepository;
import com.example.ebankingbackend.repositories.BankAccountRepository;
import com.example.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
//on peut utilise un constructeur avec tous les paramettre ou l'annotation autowired  pour chaque objet ou l'annotation allargsconstructor
@AllArgsConstructor
@Slf4j

public class BankAccountServiceImpl implements BankAccountService{
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;
//    Logger log= LoggerFactory.getLogger(this.getClass().getName());
//    si ona enlover l'objet log il suffit d'ajouter l'annotation @slf4j
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("saving new customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
       Customer savedCustomer= customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer );
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAcount(double initalBalance, double overdraft, Long customerId) throws ClassNotFoundException {
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
        return dtoMapper.fromCurrentBankAccount( savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAcount(double initalBalance, double interestRate, Long customerId) throws ClassNotFoundException {
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
        return dtoMapper.fromSavingBankAccount (savedBankAccount) ;

    }


    @Override
    public List<CustomerDTO> listCustomer() {
        List<Customer>customers=customerRepository.findAll();
        List<CustomerDTO> customerDTOS= customers.stream().map(cust -> dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
//        return customerRepository.findAll() ;
        //la programmation imperative
         /*
        List<CustomerDTO> customerDTOS=new ArrayList<>();
        for (Customer customer:customers){
            CustomerDTO customerDTO=dtoMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }
        *
         */
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount  Not found"));
        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount= (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount= (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }

    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException ,BalanceNotSuffisantException{
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount  Not found"));
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
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount  Not found"));

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
    public List <BankAccountDTO> bankAccountList(){

         List<BankAccount> bankAccounts= bankAccountRepository.findAll();
         List<BankAccountDTO> bankAccountDTOS= bankAccounts.stream().map(bankAccount -> {
             if (bankAccount instanceof SavingAccount){
                 SavingAccount savingAccount=(SavingAccount) bankAccount;
                 return dtoMapper.fromSavingBankAccount(savingAccount);
             }else
                 {
                     CurrentAccount currentAccount=(CurrentAccount) bankAccount;
                     return dtoMapper.fromCurrentBankAccount(currentAccount);

             }
         }).collect(Collectors.toList());
         return bankAccountDTOS;
    }
    @Override
    public CustomerDTO getCustomer(Long customerId)throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).
                orElseThrow(() ->
                        new CustomerNotFoundException("customer not found"));

        return dtoMapper.fromCustomer(customer);

    }
    @Override

    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("saving new customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer= customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer );
    }
    @Override
    public  void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }
    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
      List<AccountOperation> accountOperations= accountOperationRepository.findByBankAccountId(accountId);
        return   accountOperations.stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());

    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException{
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFoundException("Account not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO=new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }


}

