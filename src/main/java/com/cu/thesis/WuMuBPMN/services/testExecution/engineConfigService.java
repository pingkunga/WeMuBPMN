package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;

public interface engineConfigService
{
    List<engineConfig> listAll();
    engineConfig getById(Integer id);
    engineConfig save(engineConfig engineConfigEntry);
    void delete(Integer id);
}