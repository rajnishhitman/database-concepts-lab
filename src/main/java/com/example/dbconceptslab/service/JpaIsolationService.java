package com.example.dbconceptslab.service;

import com.example.dbconceptslab.repository.LabAccountRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class JpaIsolationService {

    private final LabAccountRepository repository;
    private final EntityManager entityManager;


    public JpaIsolationService(LabAccountRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

//    private void pause(){
//        try{
//            Thread.sleep(8000);
//        }catch (InterruptedException)
//    }
}
