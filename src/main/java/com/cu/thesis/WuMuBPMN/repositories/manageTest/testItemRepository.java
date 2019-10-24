package com.cu.thesis.WuMuBPMN.repositories.manageTest;

import java.util.Optional;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface testItemRepository extends JpaRepository<testItem, Integer>
{
    //สำหรับใช้ในตอน Validate
    Optional<testItem> findByTestItemName(String pMutantTestItemName);
    Optional<testItem> findByTestItemPath(String pMutantTestItemPath);
}