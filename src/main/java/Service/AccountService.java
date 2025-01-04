package Service;

import DAO.AccountDao;
import Model.Account;


public class AccountService {
    private AccountDao accountDao;
    
    public AccountService(){
        accountDao = new AccountDao();
    }

    public AccountService(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    public Account createAccount(Account account){
        return accountDao.createAccount(account);
    }

    public Account verifyLogin(Account account){
        return accountDao.verifyLogin(account);
    }


}
