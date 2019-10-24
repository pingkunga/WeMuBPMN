package com.cu.thesis.WuMuBPMN.exceptions;

public class BPMNEngineException extends Exception
{
    private static final long serialVersionUID = 1L;

    public BPMNEngineException(String errorMessage) {
        super(errorMessage);
    }
}