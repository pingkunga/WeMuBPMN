package com.cu.thesis.WuMuBPMN.repositories.mutantGenerator;

import java.util.List;

import javax.transaction.Transactional;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemHead;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface mutantTestItemRepository extends JpaRepository<mutantTestItemHead, Integer>
{
    @Transactional
    @Modifying
    Long deleteByTestItemIdAndGenMutantType(@Param("testitemid") int testItemId, 
                                            @Param("genmutanttype") String genMutantType);

    List<mutantTestItemHead> findByTestItemId(int pTestItemId);
    
    @Transactional
    @Modifying
    Long deleteByTestItemId(@Param("testitemid") int testItemId);
}