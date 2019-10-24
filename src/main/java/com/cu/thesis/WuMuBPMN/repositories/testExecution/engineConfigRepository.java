package com.cu.thesis.WuMuBPMN.repositories.testExecution;

import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface engineConfigRepository extends JpaRepository<engineConfig, Integer>
{

}