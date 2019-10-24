package com.cu.thesis.WuMuBPMN.repositories.mutantGenerator;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface mutantTestItemDetailRepository extends JpaRepository<mutantTestItemDetail, Integer>
{

}