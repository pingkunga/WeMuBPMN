package com.cu.thesis.WuMuBPMN.controllers.testExecution;

import java.util.Optional;

import javax.validation.Valid;

import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.services.testExecution.engineConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class engineConfigController
{
    @Autowired
    private engineConfigService EngineConfigService;

   
    /*
     * ดึงข้อมูล Engine ทั้งหมด
     */
    @RequestMapping(value = "engineconfig", method = RequestMethod.GET)
    public String viewEngineConfig(@PathVariable Optional<Integer> id, Model model)
    {
        engineConfig config = null;
        if (!id.isPresent())
        {
            try
            {
                config = EngineConfigService.getById(1);
            }
            catch(Exception ex)
            {
                config = new engineConfig();
            }
            
        }
        model.addAttribute("engineconfig", config);
        return "testExecution/engineConfig";
    }

    /**
     * บันทึกข้อมูลลง DB
     */
    @RequestMapping(value ="saveengineconfig", method = RequestMethod.POST)
    public String saveTestItem(@Valid engineConfig engineconfigEntry
                             , BindingResult result)
    {
        EngineConfigService.save(engineconfigEntry);
       
        return "redirect:/engineconfig";
    }
}