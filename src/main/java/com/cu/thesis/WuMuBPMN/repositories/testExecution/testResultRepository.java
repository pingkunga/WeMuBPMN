package com.cu.thesis.WuMuBPMN.repositories.testExecution;

import java.util.List;

import javax.transaction.Transactional;

import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultHead;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface testResultRepository extends JpaRepository<testResultHead, Integer>
{
    @Modifying
    @Transactional
    void deleteByMutantTestItemId(int pTestItemId);

    List<testResultHead> findByMutantTestItemId(int pMutantTestItemId);
}