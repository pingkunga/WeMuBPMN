package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.repositories.testExecution.engineConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class engineConfigServiceImpl implements engineConfigService
{
    private engineConfigRepository engineConfigRepo;

    @Autowired
    public void setEngineConfigRepo(engineConfigRepository engineConfigRepo) {
        this.engineConfigRepo = engineConfigRepo;
    }

	//==========================================
	//			DATABASE CRUD
	//==========================================
	@Override
	public List<engineConfig> listAll() {
		return engineConfigRepo.findAll();
	}

	@Override
	public engineConfig getById(Integer id) {
		return engineConfigRepo.findById(id).get();
	}

	@Override
	public engineConfig save(engineConfig engineConfigEntry) {
		return engineConfigRepo.save(engineConfigEntry);
	}

	@Override
	public void delete(Integer id) {
		engineConfigRepo.deleteById(id);
	}
}