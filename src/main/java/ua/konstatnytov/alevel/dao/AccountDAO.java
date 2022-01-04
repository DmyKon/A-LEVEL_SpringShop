package ua.konstatnytov.alevel.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.konstatnytov.alevel.entity.Account;

import javax.transaction.Transactional;

@Transactional
@Repository
public class AccountDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Account findAccount(String userName) {
        Session session = sessionFactory.getCurrentSession();
        return session.find(Account.class, userName);
    }
}