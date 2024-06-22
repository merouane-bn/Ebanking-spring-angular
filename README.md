# Digital Banking Project using Spring & Angular 
# By : MEROUANE BENELABDY
# 1- Architecture : 

![architecture](./Front-end-main/assets/img.png)

L'architecture d'un projet Digital Banking avec Spring et Angular implique une interaction entre les différentes couches. Le frontend Angular communique avec le backend Java via des requêtes HTTP pour accéder aux fonctionnalités et aux données métier. Le backend utilise des entités, des repositories et des services pour gérer la logique métier et interagir avec la base de données. Les DTOs et les mappers facilitent l'échange de données entre le backend et le frontend. En résumé, le frontend envoie des requêtes au backend, qui les traite en utilisant ses services et repositories, puis renvoie une réponse au frontend. Cette architecture permet une séparation claire des responsabilités et facilite le développement d'une application bancaire numérique.

# 2- Les interfaces de l'application :

###  Interface Customers :

![Customers](./Front-end-main/assets/customer.png)

###  Interface Save New Customer  :

![NewCustomer](./Front-end-main/assets/NewCostumer.png)

###  Interface Account :

![AccountCustomer](./Front-end-main/assets/account%20interface.png)

###  Interface Operations :
10-05-2024-23:33:51

![Operations](./Front-end-main/assets/transfer.png)

Serveur BACKEND 

1-Structure du projet :

![structureprojetbackend](./Front-end-main/assets/backend%20structure.png)

-Entities (Entités) : Les entités représentent les objets métier de l'application et sont utilisées pour représenter les tables de la base de données. Elles contiennent des attributs et des relations qui décrivent les données de l'application.

-Repositories (Répertoires) : Les repositories sont utilisés pour l'accès aux données. Ils fournissent des méthodes permettant d'effectuer des opérations CRUD (Create, Read, Update, Delete) sur les entités, facilitant ainsi l'interaction avec la base de données.

-DTOs (Data Transfer Objects) : Les DTOs sont utilisés pour transférer des données entre les différentes couches de l'application. Ils permettent de structurer et de transférer uniquement les données nécessaires, améliorant ainsi les performances et la sécurité.

-Mappers (Mappateurs) : Les mappers sont utilisés pour convertir les objets entre différentes représentations, par exemple entre les entités et les DTOs. Ils facilitent la transformation des données en effectuant des opérations de mapping appropriées.

-Web : La couche Web fournit des composants tels que les RestControllers qui exposent les fonctionnalités de l'application via des API REST. Elle gère les requêtes HTTP entrantes et renvoie les réponses appropriées.

-Service : La couche de service contient la logique métier de l'application. Elle traite les opérations complexes, effectue des validations, coordonne l'interaction entre les différentes entités et gère les transactions.

-Exception : Les exceptions sont utilisées pour gérer les erreurs et les situations exceptionnelles dans l'application. Elles permettent de gérer les cas d'échec, d'erreur de validation ou toute autre situation qui nécessite une gestion particulière.

2- Composants :

### Entities : 
 
 * Customer : 
 ```java
@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @OneToMany(mappedBy = "customer")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Pas la peine de serializé bankAccount lors de la serialization du customer
    private List<BankAccount> bankAccounts;
}
```
Cette classe "CurrentAccount" est une entité qui représente un compte courant dans l'application. Elle est annotée avec "@Entity" et hérite de la classe "BankAccount". La valeur de discrimination ("CA") spécifiée avec "@DiscriminatorValue" est utilisée pour différencier les enregistrements de comptes courants dans la base de données. Elle inclut également un attribut supplémentaire spécifique aux comptes courants, "overDraft" (découvert autorisé).

 * Bank Account 
 ```java
 @Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE" , length = 4)
@Data @AllArgsConstructor @NoArgsConstructor
public class BankAccount {
    @Id
    private String id;
    private double balance;
    private Date createAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @ManyToOne
    private Customer customer;
    @OneToMany(mappedBy = "bankAccount" , fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations;
}
```
Cette classe "BankAccount" est une entité qui représente un compte bancaire dans l'application. Elle est annotée avec "@Entity" pour être persistée dans la base de données. Elle contient des attributs tels que "balance" (solde du compte), "Date_createdAt" (date de création), "status" (statut du compte) et est associée à un client via la relation "@ManyToOne". De plus, elle est liée à plusieurs opérations de compte via la relation "@OneToMany" et la propriété "mappedBy".

* Saving Account : 

```java
@Entity
@DiscriminatorValue("SA")
@Data @AllArgsConstructor @NoArgsConstructor
public class SavingAccount extends BankAccount{
    private double interestRate;
}
```

Cette classe "SavingAccount" est une entité qui représente un compte d'épargne dans l'application. Elle hérite de la classe "BankAccount" et est annotée avec "@Entity". La valeur de discrimination ("SA") spécifiée avec "@DiscriminatorValue" est utilisée pour distinguer les enregistrements de comptes d'épargne dans la base de données. Elle contient un attribut supplémentaire "interestRate" (taux d'intérêt) spécifique aux comptes d'épargne.

* Current Account :
```java
@Entity
@DiscriminatorValue("CA")
@Data @AllArgsConstructor @NoArgsConstructor
public class CurrentAccount extends  BankAccount{
    private double overDraft;
}
```

Cette classe "CurrentAccount" est une entité qui représente un compte courant dans l'application. Elle hérite de la classe "BankAccount" et est annotée avec "@Entity". La valeur de discrimination ("CA") spécifiée avec "@DiscriminatorValue" est utilisée pour distinguer les enregistrements de compte courant dans la base de données. Elle contient un attribut supplémentaire "overDraft" (découvert autorisé) spécifique aux comptes courants.

* Account Operation : 
```java
@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class AccountOperation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private Date operationDate;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    @ManyToOne
    private BankAccount bankAccount;
    private String description;
}
```

Cette classe "AccountOperation" est une entité qui représente une opération de compte dans l'application. Elle est annotée avec "@Entity" et contient des attributs tels que "operationDate" (date de l'opération), "amount" (montant de l'opération), "description" (description de l'opération) et "type" (type d'opération) qui est une énumération de type "OperationType". Elle est associée à un compte bancaire via la relation "@ManyToOne". L'identifiant de l'opération est généré automatiquement avec l'annotation "@GeneratedValue".

### Enumérations :
 
 * Operation Type  :
 ```java
public enum OperationType {
    CREDIT, DEBIT
}
```
Le premier code représente une énumération "AccountStatus" qui définit les différents statuts possibles pour un compte bancaire. Les statuts sont "CREATED" (créé), "ACTIVATED" (activé) et "SUSPENDED" (suspendu). Cette énumération est utilisée pour représenter l'état d'un compte bancaire.

* Account Status : 

```java
public enum AccountStatus {
    CREATED,ACTIVATED,SUSPENDED

}
```
Le deuxième code représente une énumération "OperationType" qui définit les types d'opérations possibles pour un compte bancaire. Les types d'opérations sont "DEBIT" (débit) et "CREDIT" (crédit). Cette énumération est utilisée pour indiquer le type d'une opération effectuée sur un compte bancaire.

### Exceptions :

* Balance Not Sufficient 
```java
public class BalanceNotSufficientException extends Throwable {
    public BalanceNotSufficientException(String message) {
        super(message);
    }
}
```
-Le premier code représente une classe d'exception "BalanceNotSufficentException" qui est utilisée lorsque le solde d'un compte bancaire n'est pas suffisant pour effectuer une opération. Elle étend la classe "Exception" et prend un message en paramètre pour décrire l'exception.

* Bank Account Not Found

```java
public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
```

-Le deuxième code représente une classe d'exception "BankAccountNotFoundException" qui est utilisée lorsqu'un compte bancaire n'est pas trouvé. Elle étend également la classe "Exception" et prend un message en paramètre pour décrire l'exception.
* Customer Not Found 

```java
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
```

-Le troisième code représente une classe d'exception "CustomerNotFoundException" qui est utilisée lorsqu'un client n'est pas trouvé. Cette classe étend la classe "Exception" et prend un message en paramètre pour décrire l'exception. Cette exception est une exception surveillée (checked exception) car elle étend directement la classe "Exception".
### Repositories :

* Customer
```java
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    List<Customer> findByNameContains(String keyword);
}
```

* Bank Account 
```java
public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
    List<BankAccount> findByCustomer(Optional<Customer> customer);
}
```

*  Account Operaion
```java
public interface AccountOperationRepository extends JpaRepository<AccountOperation,Long> {
    public List<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String accountId);
    public Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String accountId, Pageable pageable);
}
```

### DTOS :

* Bank Account 
```java
@Data
public class BankAccountDTO {
    private String Type;
}
```

Cette classe "BankAccountDTO" est un objet de transfert de données (DTO) utilisé pour transférer les informations d'un compte bancaire entre les couches de l'application. Elle est annotée avec "@Data" pour générer automatiquement les méthodes getters et setters. Elle contient un attribut "type" qui représente le type de compte bancaire.

* Saving Bank Account 
```java
@Data
public class SavingBankAccountDTO extends BankAccountDTO{

    private String id;
    private double balance;
    private Date createAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private CustomerDTO customer;
    private double interestRate;
}
```

Cette classe "SavingBankAccountDTO" est un objet de transfert de données (DTO) spécifique à un compte d'épargne. Elle étend la classe "BankAccountDTO" et contient des attributs supplémentaires tels que "id" (identifiant du compte), "balance" (solde du compte), "Date_createdAt" (date de création du compte), "status" (statut du compte) et "interestRate" (taux d'intérêt du compte). Elle contient également un objet "customerDTO" de type "CustomerDTO" qui représente les informations du client associé au compte, ainsi qu'une liste d'objets "accountOperations" de type "AccountOperation" représentant les opérations effectuées sur le compte.

* Current Bank  Account 

```java
@Data
public class CurrentBankAccountDTO extends BankAccountDTO{

    private String id;
    private double balance;
    private Date createAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private CustomerDTO customer;
    private double overDraft;
}
```

Cette classe "CurrentBankAccountDTO" est un objet de transfert de données (DTO) spécifique à un compte courant. Elle étend la classe "BankAccountDTO" et contient des attributs supplémentaires tels que "id" (identifiant du compte), "balance" (solde du compte), "Date_createdAt" (date de création du compte), "status" (statut du compte) et "overDraft" (découvert autorisé du compte). Elle contient également un objet "customerDTO" de type "CustomerDTO" qui représente les informations du client associé au compte, ainsi qu'une liste d'objets "accountOperations" de type "AccountOperation" représentant les opérations effectuées sur le compte.

* Customer 
```java
@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
}
```

Cette classe "CustomerDTO" est un objet de transfert de données (DTO) qui représente les informations d'un client dans l'application. Elle contient des attributs tels que "id" (identifiant du client), "name" (nom du client) et "email" (adresse e-mail du client). Cette classe est utilisée pour transférer les données du client entre les différentes couches de l'application, notamment lors des opérations liées aux comptes bancaires.

* Account Operation 
```java
@Data
public class AccountOperationDTO {

    private  Long id;
    private Date operationDate;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private String description;
}

```

Cette classe "AccountOperationDTO" est un objet de transfert de données (DTO) qui représente une opération de compte dans l'application. Elle contient des attributs tels que "id" (identifiant de l'opération), "operationDate" (date de l'opération), "amount" (montant de l'opération), "description" (description de l'opération) et "type" (type d'opération) qui est une énumération de type "OperationType". Cette classe est utilisée pour transférer les informations des opérations de compte entre les différentes couches de l'application, telles que la couche de service et la couche de présentation.

* Credit 
```java
@Data
public class CreditDTO {
    private String accountId;
    private double amount;
    private String descritpion;
}
```

Cette classe "CreditDTO" est un objet de transfert de données (DTO) utilisé pour représenter les informations d'une opération de crédit dans l'application. Elle contient des attributs tels que "accountId" (identifiant du compte), "amount" (montant du crédit) et "description" (description du crédit). Cette classe est utilisée pour transférer les informations de crédit entre les différentes couches de l'application, notamment lors de la création d'une opération de crédit.

* Debit 
```java
@Data
public class DebitDTO {
    private String accountId;
    private double amount;
    private String description;
}

```

Cette classe "DebitDTO" est un objet de transfert de données (DTO) utilisé pour représenter les informations d'une opération de débit dans l'application. Elle contient des attributs tels que "accountId" (identifiant du compte), "amount" (montant du débit) et "description" (description du débit). Cette classe est utilisée pour transférer les informations de débit entre les différentes couches de l'application, notamment lors de la création d'une opération de débit.

* Transfer Request
```java
@Data
public class TransfertRequestDTO {
    private String accountSource;
    private String accountDestination;
    private double amount;
}
```

Cette classe "TransferRequestDTO" est un objet de transfert de données (DTO) utilisé pour représenter les informations d'une demande de transfert de fonds dans l'application. Elle contient des attributs tels que "accountSource" (compte source du transfert), "accountDestination" (compte de destination du transfert) et "amount" (montant du transfert). Cette classe est utilisée pour transférer les informations de demande de transfert entre les différentes couches de l'application, notamment lors de la gestion des transferts de fonds.

* Account History 
```java
@Data
public class AccountHistoryDTO {
    private String accountId;
    private double balance;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private List<AccountOperationDTO> accountOperationDTOS;
}
```
Cette classe "AccountHistoryDTO" est un objet de transfert de données (DTO) utilisé pour représenter l'historique d'un compte bancaire dans l'application. Elle contient des attributs tels que "accountId" (identifiant du compte), "balance" (solde du compte), "currentPage" (page actuelle de l'historique), "totalPages" (nombre total de pages), "pageSize" (nombre d'éléments par page) et "accountOperationDTOS" (liste des opérations de compte sous forme de "AccountOperationDTO"). Cette classe est utilisée pour transférer les informations de l'historique du compte entre les différentes couches de l'application, notamment lors de l'affichage et de la manipulation de l'historique des opérations de compte.

### Mappers :

* Bank Account :
```java
@Service
public class BankAccountMapperImpl {
    public CustomerDTO fromCustomer(Customer customer){
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer,customerDTO);
        return customerDTO;
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO){
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO,customer);
        return customer;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount){
        SavingBankAccountDTO savingBankAccountDTO = new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount,savingBankAccountDTO);
        savingBankAccountDTO.setCustomer(fromCustomer(savingAccount.getCustomer()));
        savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
        return savingBankAccountDTO;
    }

    public SavingAccount fromSavingAccountBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO){
        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(savingBankAccountDTO,savingAccount);
        savingAccount.setCustomer(fromCustomerDTO(savingBankAccountDTO.getCustomer()));
        return savingAccount;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount){
        CurrentBankAccountDTO currentBankAccountDTO = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentAccount,currentBankAccountDTO);
        currentBankAccountDTO.setCustomer(fromCustomer(currentAccount.getCustomer()));
        currentBankAccountDTO.setType(currentAccount.getClass().getSimpleName());
        return currentBankAccountDTO;
    }

    public CurrentAccount fromCurrentAccountBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO){
        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDTO,currentAccount);
        currentAccount.setCustomer(fromCustomerDTO(currentBankAccountDTO.getCustomer()));
        return currentAccount;
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation,accountOperationDTO);

        return accountOperationDTO;
    }


}

```
Cette classe "BankAccountMapperImpl" est une classe de mapping qui se charge de convertir les objets des entités (comme "Customer", "SavingAccount", "CurrentAccount", "AccountOperation") en objets DTO correspondants (comme "CustomerDTO", "SavingBankAccountDTO", "CurrentBankAccountDTO", "AccountOperationDTO"), et vice versa. Elle utilise la méthode "copyProperties" de la classe "BeanUtils" pour effectuer la copie des propriétés d'un objet à un autre. Cette classe est utilisée pour assurer la conversion des données entre les entités et les DTOs dans le projet.Voici l'explication de l'utilit" de quelque fonction :

-La fonction "fromCustomer" convertit un objet de l'entité "Customer" en un objet DTO "CustomerDTO". Elle utilise la méthode "copyProperties" pour copier les propriétés de l'objet "Customer" vers l'objet "CustomerDTO".

-La fonction "fromCustomerDTO" effectue l'opération inverse en convertissant un objet DTO "CustomerDTO" en un objet de l'entité "Customer". Elle utilise également la méthode "copyProperties" pour copier les propriétés de l'objet "CustomerDTO" vers l'objet "Customer".

-La fonction "fromSavingBankAccountDTO" convertit un objet DTO "SavingBankAccountDTO" en un objet de l'entité "SavingAccount". Elle copie les propriétés de l'objet DTO vers l'objet de l'entité et utilise la fonction "fromCustomerDTO" pour convertir l'objet DTO "CustomerDTO" associé en un objet de l'entité "Customer".

-La fonction "fromSavingBankAccount" effectue l'opération inverse en convertissant un objet de l'entité "SavingAccount" en un objet DTO "SavingBankAccountDTO". Elle copie les propriétés de l'objet de l'entité vers l'objet DTO et utilise la fonction "fromCustomer" pour convertir l'objet associé de l'entité "Customer" en un objet DTO "CustomerDTO".


### Services :

* Interface BankAccountService :
```java
public interface BankAccountService {

     //Logger log = LoggerFactory.getLogger(this.getClass().getName());
     CustomerDTO saveCustomer(CustomerDTO customerDTO);
 
     CurrentBankAccountDTO saveCurrentBankAccount(double InitialBalance, Long customerId, double overDraft);
     SavingBankAccountDTO saveSavingBankAccount(double InitialBalance, Long customerId, double interestRate);
     List<CustomerDTO> listCustomers();
     BankAccountDTO getBankAccoount(String accountId);
     void debit(String accountId,double amount,String description);
     void credit(String accountId,double amount,String description);
     void transfert(String accountIdSource,String accountIdDestination,double amount);
     List<BankAccountDTO> bankAccountsList();
     public CustomerDTO getCustomer(Long CustomerId) throws Exception;
 
     CustomerDTO updateCustomer(CustomerDTO customerDTO);
 
     void deleteCustomer(Long CustomerId);
 
     List<AccountOperationDTO> AccountHistory(String accountId);
 
     AccountHistoryDTO getAccountHistory(String accountId, int page, int size);
 
     List<CustomerDTO> searchCustomers(String keyword);
 
     List<BankAccount> getaccountsCustomer(Long CustomerId);
 }
```

Le package com.mouakkal.digitalbanking.service contient une interface BankAccountService qui définit les opérations de service disponibles dans le système de gestion des comptes bancaires. Ces opérations incluent la création et la gestion des clients, des comptes courants et des comptes d'épargne, les opérations de débit, de crédit et de transfert, l'historique des comptes, etc. Les exceptions BalanceNotSufficentException, BankAccountNotFoundException et CustomerNotFoundException sont lancées en cas d'erreurs spécifiques lors de l'exécution de ces opérations.

* Class BankAccountServiceImpl : 

```java
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;

    //Logger log = LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer cust = customerRepository.save(customer);
        return dtoMapper.fromCustomer(cust);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double InitialBalance, Long customerId, double overDraft) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new RuntimeException("Customer not Found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreateAt(new Date());
        currentAccount.setBalance(InitialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount SavedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(SavedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double InitialBalance, Long customerId, double interestRate) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new RuntimeException("Customer not Found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreateAt(new Date());
        savingAccount.setBalance(InitialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount SavedBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(SavedBankAccount);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> collect = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public BankAccountDTO getBankAccoount(String accountId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("BankAccount not Found"));
        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }
        else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("BankAccount not Found"));
        if(bankAccount.getBalance() < amount) throw new RuntimeException("Balance not sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationDate(new Date());
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("BankAccount not Found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationDate(new Date());
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfert(String accountIdSource, String accountIdDestination, double amount) {
        debit(accountIdSource,amount,"Transfert to " + accountIdDestination);
        credit(accountIdDestination,amount,"Transfert from " + accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountsList(){
        List<BankAccount> ba = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = ba.stream().map(bankAccount -> {
            if(bankAccount instanceof SavingAccount)
                return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
            else
                return dtoMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long CustomerId) throws Exception {
        Customer customer = customerRepository.findById(CustomerId).orElseThrow(()->new Exception("Customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("updating new customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer cust = customerRepository.save(customer);
        return dtoMapper.fromCustomer(cust);
    }

    @Override
    public void deleteCustomer(Long CustomerId){
        customerRepository.deleteById(CustomerId);
    }

   @Override
    public List<AccountOperationDTO> AccountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId);
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.stream().map(accountOperation ->
            dtoMapper.fromAccountOperation(accountOperation)).collect(Collectors.toList());
        return accountOperationDTOS;
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        Page<AccountOperation> byBankAccountId = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page,size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = byBankAccountId.getContent().stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(byBankAccountId.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword){
        List<Customer> customers = customerRepository.findByNameContains(keyword);
        List<CustomerDTO> c = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return c;
    }

    @Override
    public List<BankAccount> getaccountsCustomer(Long CustomerId) {
        Optional<Customer> customer = customerRepository.findById(CustomerId);
        return bankAccountRepository.findByCustomer(customer);
    }

}
```

Le code fourni est une implémentation de l'interface BankAccountService dans la classe BankAccountServiceImpl. Cette classe fournit des fonctionnalités pour gérer les opérations liées aux comptes bancaires, y compris la création de clients, de comptes courants et de comptes d'épargne, les opérations de débit, de crédit et de transfert, la récupération des informations sur les comptes, l'historique des opérations, etc. Elle utilise également les repositorires CustomerRepository, BankAccountRepository et AccountOperationRepository pour accéder aux données persistantes.Voici l'explication de quelque fonction impléménté par cette classe :

-saveCustomer(CustomerDTO customerDTO): Cette fonction enregistre un nouveau client en utilisant les informations fournies dans l'objet CustomerDTO. Elle convertit l'objet DTO en entité Customer, le sauvegarde dans le référentiel customerRepository et renvoie une représentation DTO du client sauvegardé.

-saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId): Cette fonction crée un nouveau compte courant avec le solde initial, la limite de découvert et l'identifiant du client donnés. Elle récupère d'abord l'entité Customer correspondante à partir du référentiel customerRepository, puis crée un nouvel objet CurrentAccount avec les valeurs fournies. Le compte courant est ensuite sauvegardé dans le référentiel bankAccountRepository et une représentation DTO du compte sauvegardé est renvoyée.

-debit(String accountId, double amount, String description): Cette fonction effectue une opération de débit sur un compte bancaire donné. Elle récupère d'abord l'entité BankAccount correspondante à partir du référentiel bankAccountRepository. Ensuite, elle crée une nouvelle opération de compte avec le type de débit, le montant et la description spécifiés, et l'associe au compte bancaire. L'opération est sauvegardée dans le référentiel accountOperationRespository et le solde du compte est mis à jour en déduisant le montant.

### Rest Controller : 

* Class BankAccountRestAPI :

```java
@RestController
@CrossOrigin("*")
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    public BankAccountRestAPI(BankAccountService bankAccountService){
        this.bankAccountService = bankAccountService;
    }

    @GetMapping(path = "/accounts/{id}")
    public BankAccountDTO getBankAccount(@PathVariable(name = "id") String accountId){
        return bankAccountService.getBankAccoount(accountId);
    }

    @GetMapping(path = "/accounts")
    public List<BankAccountDTO> listAccounts(){
        return bankAccountService.bankAccountsList();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId){
        return bankAccountService.AccountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(@PathVariable String accountId,
                                               @RequestParam(name = "page",defaultValue = "0") int page,
                                               @RequestParam(name = "size",defaultValue = "5") int size){
        return bankAccountService.getAccountHistory(accountId,page,size);
    }

    @PostMapping(path = "/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO){
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping(path = "/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO){
        this.bankAccountService.debit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescritpion());
        return creditDTO;
    }

    @GetMapping(path = "/accounts/{customerid}")
    public List<BankAccount> getaccountsCustomer(@PathVariable Long customerid){
        return this.bankAccountService.getaccountsCustomer(customerid);
    }


    @PostMapping(path = "/accounts/transfert")
    public void transfert(@RequestBody TransfertRequestDTO transfertRequestDTO){
        this.bankAccountService.transfert(transfertRequestDTO.getAccountSource(),transfertRequestDTO.getAccountDestination(),transfertRequestDTO.getAmount());
    }
}
```

Le code fourni est une classe BankAccountRestAPI qui expose des points de terminaison (endpoints) pour l'API REST liée aux opérations bancaires. Voici une brève explication des principales fonctions de cette classe :

1-getBankAccount(String accountId): Cette fonction est associée à la requête GET "/accounts/{accountId}" et renvoie les informations d'un compte bancaire spécifié par son identifiant.

2-listAccounts(): Cette fonction est associée à la requête GET "/accounts" et renvoie la liste de tous les comptes bancaires enregistrés.

3-getHistory(String accountId): Cette fonction est associée à la requête GET "/accounts/{accountId}/operations" et renvoie l'historique des opérations effectuées sur un compte bancaire spécifié.

4-getAccountHistory(String accountId, int page, int size): Cette fonction est associée à la requête GET "/accounts/{accountId}/pageOperations" et renvoie l'historique des opérations d'un compte bancaire spécifié, avec une pagination personnalisée.

debit(DebitDTO debitDTO): Cette fonction est associée à la requête POST "/accounts/debit" et effectue un débit sur un compte bancaire spécifié, en déduisant le montant spécifié.

5-credit(CreditDTO creditDTO): Cette fonction est associée à la requête POST "/accounts/credit" et effectue un crédit sur un compte bancaire spécifié, en ajoutant le montant spécifié.

6-transfer(TransferRequestDTO transferRequestDTO): Cette fonction est associée à la requête POST "/accounts/transfer" et effectue un transfert d'argent entre deux comptes bancaires, en débitant le compte source et en créditant le compte de destination.

7-getBankAccountCustomer(String customerId): Cette fonction est associée à la requête GET "/accounts/customer/{customerId}" et renvoie la liste des comptes bancaires associés à un client spécifié par son identifiant.

Ces fonctions permettent d'interagir avec les opérations bancaires via des requêtes HTTP et de fournir les résultats correspondants aux clients qui utilisent l'API.

* Class CustomerRestController :

```java
@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountService bankAccountService;

    @GetMapping(path = "/customers")
    public List<CustomerDTO> customers(){
        return bankAccountService.listCustomers();
    }
    @GetMapping(path = "/customers/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword" , defaultValue = "") String keyword){
        return bankAccountService.searchCustomers(keyword);
    }

    @GetMapping(path = "/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long Customerid) throws Exception {
        return bankAccountService.getCustomer(Customerid);
    }

    // signifie que les données du customer vont etre recuperer a partir des données de la requete en format JSON
    @PostMapping(path = "/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable(name="customerId") Long customerId,@RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping(path = "/customers/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId){
        bankAccountService.deleteCustomer(customerId);
    }

}

```

Le code fourni est une classe CustomerRestController qui expose des points de terminaison (endpoints) pour l'API REST liée à la gestion des clients bancaires. Voici une brève explication des principales fonctions de cette classe :

1-customers(): Cette fonction est associée à la requête GET "/customers" et renvoie la liste de tous les clients enregistrés.

2-searchCustomers(String keyword): Cette fonction est associée à la requête GET "/customers/search" et renvoie la liste des clients correspondant à un mot-clé spécifié.

3-getCustomer(Long customerId): Cette fonction est associée à la requête GET "/customers/{id}" et renvoie les informations d'un client spécifié par son identifiant.

4-saveCustomer(CustomerDTO customerDTO): Cette fonction est associée à la requête POST "/customers" et enregistre un nouveau client en utilisant les informations fournies.

5-updateCustomer(Long customerId, CustomerDTO customerDTO): Cette fonction est associée à la requête PUT "/customers/{customerId}" et met à jour les informations d'un client spécifié par son identifiant en utilisant les nouvelles informations fournies.

6-deleteCustomer(Long id): Cette fonction est associée à la requête DELETE "/customers/{id}" et supprime un client spécifié par son identifiant.

Ces fonctions permettent de gérer les opérations liées aux clients bancaires via des requêtes HTTP et de fournir les résultats correspondants aux clients qui utilisent l'API.

### Fichier pom.xml :

```java
 <groupId>com.mouakkal</groupId>
    <artifactId>DigitalBanking</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>DigitalBanking</name>
    <description>DigitalBanking</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
       <!-- <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.1.0</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```

Les dépendances indiquées dans le code XML sont utilisées dans un projet Spring Boot avec des fonctionnalités spécifiques. Voici une brève explication des principales dépendances utilisées :

1-spring-boot-starter-data-jpa: Cette dépendance permet l'intégration de JPA (Java Persistence API) pour l'accès aux données et la gestion des entités.

2-spring-boot-starter-web: Cette dépendance fournit les fonctionnalités nécessaires pour développer des applications web avec Spring Boot, y compris le serveur web embarqué.

3-spring-boot-devtools: Cette dépendance fournit des outils de développement pour le rechargement automatique des modifications et une expérience de développement plus rapide.

4-mysql-connector-j: Cette dépendance permet la connexion à une base de données MySQL en utilisant le connecteur JDBC.

5-lombok: Cette dépendance simplifie le développement en générant automatiquement du code boilerplate tel que les accesseurs et les constructeurs.

6-springdoc-openapi-ui: Cette dépendance permet de générer automatiquement une documentation API basée sur OpenAPI (anciennement Swagger) et fournit une interface utilisateur pour explorer et tester l'API.

7-spring-boot-starter-test: Cette dépendance fournit les outils et les bibliothèques nécessaires pour écrire des tests unitaires et d'intégration dans les applications Spring Boot.

8-hibernate-validator: Cette dépendance fournit la validation des contraintes sur les objets métier en utilisant les annotations de validation de Hibernate.

Ces dépendances sont utilisées pour faciliter le développement d'une application Spring Boot avec des fonctionnalités telles que l'accès aux données, le développement web, la documentation de l'API et la validation des données.

### Fichier de configuration application.properties :

```java
spring.application.name=DigitalBanking_Spring_Angular
server.port=8086
spring.datasource.url=jdbc:mysql://localhost:3306/DB-BANK?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
spring.jpa.show-sql=true
```
Le code de configuration de l'application Spring Boot que vous avez partagé contient des propriétés essentielles pour la connexion à une base de données MySQL, la gestion du schéma et d'autres paramètres de l'application. Par exemple, la propriété spring.datasource.url définit l'URL de connexion à la base de données, tandis que spring.jpa.hibernate.ddl-auto spécifie le mode de gestion du schéma. De plus, la propriété server.port indique sur quel port le serveur web intégré écoutera les requêtes. Ces propriétés sont cruciales pour garantir une configuration adéquate de l'application et assurer son bon fonctionnement.

### Main :

```java
@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    //@Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Mahmoud","Merouane","Meriem").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                bankAccountService.saveCurrentBankAccount(Math.random() * 90000, customer.getId(), 9000);
                bankAccountService.saveSavingBankAccount(Math.random() * 120000, customer.getId(), 5.5);
                bankAccountService.bankAccountsList().forEach(b -> {
                    String s ;
                    if(b instanceof SavingBankAccountDTO) s = ((SavingBankAccountDTO) b).getId();
                    else s = ((CurrentBankAccountDTO) b).getId();
                    bankAccountService.credit(s, 10000 + Math.random() * 120000, "Credit");
                    bankAccountService.debit(s, 100 + Math.random() * 1200, "Debit");
                });
            });
        };
    }

    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository, 
                            AccountOperationRepository accountOperationRepository) {
        return args -> {
            Stream.of("Mahmoud","Merouane","Meriem").forEach(cust -> {
                Customer customer = new Customer();
                customer.setName(cust);
                customer.setEmail(cust+"@gmail.com");

                customerRepository.save(customer);
            });

            customerRepository.findAll().forEach(cust -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 10000);
                currentAccount.setCreateAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);

                bankAccountRepository.save(currentAccount);
            });

            customerRepository.findAll().forEach(cust -> {
                SavingAccount savingAccount = new SavingAccount();
                savingAccount
                        .setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 10000);
                savingAccount.setCreateAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5);

                bankAccountRepository.save(savingAccount);
            });

            bankAccountRepository.findAll().forEach(ba -> {
                AccountOperation accountOperation = new AccountOperation();
                accountOperation.setOperationDate(new Date());
                accountOperation.setAmount(Math.random()*500);
                accountOperation.setType(Math.random() > 0.5? OperationType.DEBIT : OperationType.CREDIT);
                accountOperation.setBankAccount(ba);

                accountOperationRepository.save(accountOperation);
            });
        };
    }

}


```
Le code fourni est l'implémentation de la classe principale EBankingApplication dans une application Spring Boot. Cette classe contient une méthode main qui démarre l'application. De plus, elle définit deux méthodes commandLineRunner annotées avec @Bean, qui sont exécutées au démarrage de l'application.

La première méthode commandLineRunner utilise le service BankAccountService pour créer des clients, des comptes courants et des comptes d'épargne, et effectue des opérations de crédit et de débit sur ces comptes à des fins de test.

La deuxième méthode start utilise les référentiels CustomerRepository, BankAccountRepository et AccountOperationRepository pour créer des clients, des comptes courants et des comptes d'épargne en utilisant des entités de base de données. Elle effectue également des opérations de crédit et de débit sur les comptes créés.

Ces méthodes CommandLineRunner permettent de pré-remplir la base de données avec des données de test lors du démarrage de l'application. Cela facilite le développement et les tests.

## Partie Frontend
1-Architecture Angular :

![architectureAngular](./Front-end-main/assets/angularArchitecture.png)

Dans un projet Angular, l'architecture est basée sur le modèle MVVM (Modèle-Vue-VueModèle) qui permet une séparation claire des responsabilités.

-Le modèle (Model) représente les données de l'application, généralement sous forme d'objets ou de services.
-La vue (View) correspond à la partie visible de l'interface utilisateur, définie à l'aide de fichiers HTML et de feuilles de style CSS.
-Le contrôleur (VueModèle) agit comme une couche intermédiaire entre le modèle et la vue, gérant les interactions utilisateur et la logique métier.
Angular utilise également des composants pour encapsuler la logique et la présentation d'une partie spécifique de l'interface utilisateur, ainsi que des services pour partager des fonctionnalités et des données entre différents composants.

L'architecture d'un projet Angular favorise la modularité, la réutilisabilité du code et la séparation des préoccupations, ce qui facilite le développement, la maintenance et l'évolutivité de l'application.


2- Strcuture d'un projet Angular :

![structureAngular](./Front-end-main/assets/frontend%20structure.png)

La structure d'un projet Angular typique comprend plusieurs répertoires et fichiers :

-Le répertoire "src" : Il contient le code source de l'application Angular.
-"app" : Ce répertoire contient les composants, services, modules et autres fichiers spécifiques à l'application.
-"assets" : Ce répertoire contient les fichiers statiques tels que les images, les fichiers CSS, etc.
-"environments" : Ce répertoire contient des fichiers de configuration pour différents environnements (développement, production, etc.).
-"index.html" : C'est le point d'entrée de l'application, où l'application Angular est chargée dans le navigateur.
-Le fichier "angular.json" : Il s'agit du fichier de configuration global de l'application Angular. Il spécifie les paramètres de construction, de compilation et de déploiement de l'application.
-Le fichier "tsconfig.json" : Il contient la configuration TypeScript pour l'application.
-Les fichiers de services : Les services Angular sont généralement définis dans des fichiers séparés et fournissent des fonctionnalités partagées et des opérations de traitement des données.

* Fichier package.json 

Le fichier "package.json" : Il spécifie les dépendances du projet et contient des scripts pour les tâches courantes, telles que la compilation et le lancement de l'application.

* Fichier angular.json

Le fichier "angular.json" est un fichier de configuration essentiel dans un projet Angular. Il définit les paramètres de construction, de compilation et de déploiement de l'application. Dans cet exemple, on a intégré bootstrap 5 et ses icons dans notre prôjet en modifiant l'attribut "styles" et "scripts"
Le fichier "package.json" : Il spécifie les dépendances du projet et contient des scripts pour les tâches courantes, telles que la compilation et le lancement de l'application.

* app.component.html 

Le fichier "app.component.html" est un fichier de modèle (view) dans un projet Angular. Il contient le code HTML qui définit la structure et la mise en page du composant principal de l'application. Vous pouvez y inclure des balises HTML, des directives Angular, des liaisons de données et des événements pour rendre le contenu dynamique.

* app.component.ts 

Le fichier "app.component.ts" est le fichier de composant TypeScript correspondant au fichier HTML mentionné ci-dessus. Il contient la logique du composant, les propriétés, les méthodes et les interactions avec les services et les autres composants. Vous pouvez définir les données, les fonctions, les cycles de vie du composant et gérer les événements utilisateur à l'intérieur de ce fichier. Il sert également de point d'entrée principal pour le composant principal de l'application.

* app-module.ts 

Le fichier "app.module.ts" : C'est le module principal de l'application qui importe et configure tous les modules, services et composants nécessaires à l'application.

* app-routing.module.ts 

Le fichier "app-routing.module.ts" est un module de routage dans un projet Angular. Il permet de définir les routes de l'application, c'est-à-dire les associations entre les URL et les composants correspondants. Ce module utilise le service RouterModule fourni par Angular pour gérer la navigation entre les différentes vues de l'application.

Cette structure de projet fournit une organisation claire du code et facilite la maintenance, la réutilisabilité et l'extension de l'application Angular.

3-Variables d'Environnement :

Le fichier environment.ts définit les paramètres de configuration pour l'environnement de développement, avec la variable backendHost spécifiant l'URL du serveur backend local. Le commentaire suggère d'importer un fichier pour faciliter le débogage en mode développement, mais il est commenté par défaut pour éviter les impacts de performance en production.

4-Models :

L'interface Customer définit la structure d'un objet représentant un client, comprenant les propriétés id, name et email.

* Bank Account : 

L'interface BankAccount définit la structure d'un objet représentant un compte bancaire, comprenant les propriétés id, type, date_createdAt, balance, status, overDraft et interestRate.

* Account Operation : 

L'interface AccountDetails définit la structure d'un objet représentant les détails d'un compte bancaire, comprenant les propriétés accountId, balance, currentPage, totalPages, pageSize et accountOperationDTOS, où accountOperationDTOS est un tableau d'objets AccountOperation.

5-Services :

* accounts-service.ts :

Le service AccountsService est injectable et utilise le module HttpClient pour effectuer des requêtes HTTP. Il contient des méthodes pour récupérer les détails d'un compte, effectuer des opérations de débit, de crédit et de transfert d'argent, ainsi que pour récupérer les comptes d'un client spécifique. Les URL des requêtes sont basées sur l'environnement backendHost défini dans environment.ts. Voici quelque exemple de service pour les comptes bancaires :

-La méthode getAccount récupère les détails d'un compte en utilisant son identifiant, le numéro de page et la taille de la page, en effectuant une requête GET vers l'API backend.

-Les méthodes debit, credit et transfer effectuent respectivement des opérations de débit, de crédit et de transfert d'argent en envoyant les données nécessaires (compte source, compte destination, montant et description) via des requêtes POST à l'API backend.

* customer-service : 

Le service CustomerService est un injectable dans Angular utilisé pour interagir avec les données des clients. Il utilise le module HttpClient pour effectuer des requêtes HTTP vers l'API backend et fournit des méthodes telles que getCustomers pour récupérer la liste de tous les clients, searchCustomers pour rechercher des clients en fonction d'un mot-clé, saveCustomer pour enregistrer un nouveau client, et deleteCustomer pour supprimer un client existant. Voici quelque exemple de service pour les clients :

-La méthode getCustomers récupère la liste de tous les customers en effectuant une requête GET à l'API backend.

-La méthode searchCustomers permet de rechercher des customers en fonction d'un mot-clé donné.

-La méthode saveCustomer enregistre un nouveau customer en envoyant les données via une requête POST.

-La méthode deleteCustomer supprime un customer spécifié en effectuant une requête DELETE à l'API backend.

6-Les Composants :

* Explication : 

Chaque composant Angular a généralement un fichier TypeScript (.ts), un fichier de modèle HTML (.html) et un fichier de style CSS (.css) correspondants.

### Navbar :

- navbar.component.html :

Son code représente une barre de navigation (navbar) pour une application de banque en ligne. Elle comporte un logo "E-Banking" et plusieurs liens de navigation. Les liens incluent une page d'accueil, une page pour afficher les comptes (Accounts), un menu déroulant pour les clients (Customers) avec des options pour rechercher des clients (Search customers) et créer un nouveau client (New customer), ainsi qu'un lien désactivé (Disabled). La barre de navigation est stylisée avec un thème sombre (navbar-dark bg-dark).

### Customers :

- customers.component.html :

Son code représente une vue dans une application Angular qui affiche une liste de clients. Il comprend un formulaire de recherche permettant de filtrer les clients en fonction d'un mot-clé. Les informations des clients, telles que leur ID, nom et email, sont affichées dans un tableau. Des boutons sont également présents pour supprimer un client ou accéder à ses comptes. En cas d'erreur lors du chargement des données ou en cas de chargement en cours, des messages appropriés sont affichés à l'utilisateur.

- customers.component.ts :

Son code représente le composant Angular "CustomersComponent". Il utilise le service CustomerService pour récupérer la liste des clients à afficher. Il comprend des méthodes pour rechercher et supprimer un client, ainsi que pour rediriger vers la page des comptes d'un client spécifique. Le composant utilise également FormBuilder pour créer un formulaire de recherche et Router pour la navigation entre les pages.

-La fonction handleSearchCustomers() est appelée lorsque l'utilisateur soumet le formulaire de recherche. Elle récupère le mot-clé saisi par l'utilisateur, appelle la méthode searchCustomers() du service CustomerService pour effectuer la recherche, et affiche les résultats dans la liste des clients. En cas d'erreur, elle affiche un message d'erreur.

-La fonction handleDeleteCustomer(c: Customer) est appelée lorsque l'utilisateur clique sur le bouton de suppression d'un client. Elle affiche une boîte de confirmation, puis appelle la méthode deleteCustomer() du service CustomerService pour supprimer le client spécifié. Si la suppression est réussie, elle met à jour la liste des clients en retirant le client supprimé.

-La fonction handleCustomerAccounts(customer: Customer) est appelée lorsque l'utilisateur clique sur le bouton "Accounts" pour afficher les comptes d'un client spécifique. Elle utilise le Router pour naviguer vers la page des comptes du client en passant les informations du client en tant que paramètre.

### New-Customer :

- new-customer.component.html :

Son code représente un formulaire pour créer un nouveau client. Il comporte des champs pour saisir le nom et l'e-mail du client, ainsi qu'une validation des champs avec des messages d'erreur affichés en cas de saisie incorrecte. Lorsque le formulaire est soumis, la fonction handleSaveCustomer() est appelée. Un bouton "Save" est affiché et est désactivé tant que le formulaire n'est pas valide. Le formulaire est affiché à l'intérieur d'une carte stylisée.

- new-customer.component.ts :


Son code représente le composant Angular pour la création d'un nouveau client. Il initialise un formulaire réactif (newCustomerFormGroup) avec des champs de nom et d'e-mail du client, qui sont soumis à des validations. Lorsque le formulaire est soumis, la fonction handleSaveCustomer() est appelée pour enregistrer le nouveau client en utilisant le service CustomerService. En cas de succès, une alerte est affichée et l'utilisateur est redirigé vers la liste des clients. En cas d'erreur, un message est affiché dans la console.

### Accounts :

- accounts.component.html :

Son code représente une vue Angular pour la gestion des comptes bancaires. Il est composé de deux colonnes.

Dans la première colonne, il y a un formulaire de recherche de compte. L'utilisateur peut saisir un identifiant de compte et cliquer sur le bouton de recherche pour afficher les détails du compte. En dessous du formulaire de recherche, il y a un espace réservé pour afficher les éventuelles erreurs ou un message de chargement. Si les détails du compte sont disponibles, ils sont affichés, y compris l'identifiant du compte, le solde et un tableau des opérations associées au compte. En bas de la première colonne, il y a une pagination pour naviguer entre les pages d'opérations.

Dans la deuxième colonne, il y a un formulaire pour effectuer des opérations sur le compte sélectionné. L'utilisateur peut choisir le type d'opération (débit, crédit ou virement), saisir le montant et la description de l'opération. S'il choisit le type de virement, il doit également saisir le compte de destination. En cliquant sur le bouton "Save Operation", l'opération est enregistrée.

Ce code utilise les directives structurelles *ngIf pour conditionner l'affichage des éléments en fonction des états des observables et des valeurs des formulaires. Il utilise également les directives de liaison de données formControlName pour lier les champs de saisie à des contrôles de formulaire réactif.

- accounts.component.ts :

Son code représente un composant Angular appelé AccountsComponent qui gère la logique et l'affichage des comptes bancaires.
Il utilise les imports pour les fonctionnalités nécessaires, notamment la création de formulaires, la gestion des erreurs avec RxJS, et les modèles de compte.
Le composant comprend des propriétés pour les formulaires, la pagination, les observables, les messages d'erreur, ainsi que des méthodes pour rechercher des comptes, passer à la page suivante et effectuer des opérations sur les comptes (débit, crédit, transfert).

-handleSearchAccount() récupère l'ID du compte à partir du formulaire, appelle le service AccountsService pour obtenir les détails du compte correspondant, gère les erreurs potentielles et assigne les résultats à la variable accountObservable.

-handleAccountOperation() récupère les valeurs des champs du formulaire d'opération, appelle les fonctions correspondantes du service AccountsService en fonction du type d'opération sélectionné (débit, crédit ou transfert), affiche une alerte en cas de succès et réinitialise le formulaire.

### Customer-Accounts :

- customer-accounts.component.html :

Le code HTML représente une interface utilisateur qui affiche les comptes clients d'un certain client. Le nom du client est affiché en tant qu'en-tête. Les détails des comptes client sont affichés dans un tableau avec les colonnes suivantes : ID du compte, statut, type de compte, date de création, solde et éventuellement taux d'intérêt ou découvert autorisé.Les comptes clients sont récupérés de manière asynchrone à l'aide de la directive *ngFor, qui itère sur chaque compte et affiche ses attributs dans les cellules correspondantes du tableau.Si un compte a un taux d'intérêt, il est affiché dans une cellule distincte. De même, si un compte a un découvert autorisé, il est également affiché dans une cellule distincte.L'ensemble de l'interface utilisateur est encapsulé dans une div avec la classe "container" pour le style et la mise en page.

---customer-accounts.component.ts :

Son code représente un composant Angular appelé CustomerAccountsComponent qui gère les comptes d'un client.Il importe les dépendances nécessaires, y compris le modèle Customer et BankAccount, ainsi que le service AccountsService pour récupérer les comptes.Le composant utilise l'injection de dépendances pour obtenir une instance du service AccountsService, ainsi que les instances de ActivatedRoute et Router.

-La fonction handleCustomerAccounts2() utilise le service AccountsService pour récupérer les comptes du client en utilisant l'identifiant. Elle gère également les erreurs potentielles en utilisant l'opérateur catchError() pour capturer les erreurs et les stocker dans la variable errorMessage.Les comptes du client sont stockés dans la variable customerAccount, qui est un Observable contenant un tableau de BankAccount, et peuvent être utilisés dans le template associé pour afficher les informations des comptes.
