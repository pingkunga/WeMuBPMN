package com.cu.thesis.WuMuBPMN;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GLOBAL_CONST {
    public static final String MUTANT_IVR = "IVR";
    public static final String MUTANT_ITR = "ITR";

    public static final String MUTANT_EAR = "EAR";
    public static final String MUTANT_ERR = "ERR";
    public static final String MUTANT_ELR = "ELR";
    public static final String MUTANT_ETA = "ETA";
    public static final String MUTANT_EDA = "EDA";
    public static final String MUTANT_ERA = "ERA";
    public static final String MUTANT_ECA = "ECA";

    public static final String MUTANT_ASR = "ASR";
    public static final String MUTANT_ACR = "ACR";
    public static final String MUTANT_AAM = "AAM";
    public static final String MUTANT_ARM = "ARM";
    public static final String MUTANT_ALM = "ALM";
    public static final String MUTANT_ATR = "ATR";
    public static final String MUTANT_AMR = "AMR";
    public static final String MUTANT_AAS = "AAS";
    public static final String MUTANT_ARS = "ARS";
    public static final String MUTANT_ALS = "ALS";
    public static final String MUTANT_AAA = "AAA";
    public static final String MUTANT_ARA = "ARA";
    public static final String MUTANT_ALA = "ALA";
    public static final String MUTANT_AOR = "AOR";
    public static final String MUTANT_ARR = "ARR";

    public static final String MUTANT_XBR = "XBR";
    
    public static final String ST_WEAK_TYPE = "ST-Weak";
    public static final String BB_WEAK_TYPE = "BB-Weak";

    public static final List<String> ST_WEAK_OPERATOR_LS = Collections.unmodifiableList(
                                                            Arrays.asList(MUTANT_IVR
                                                                        , MUTANT_ITR
                                                                        , MUTANT_EAR
                                                                        , MUTANT_ERR
                                                                        , MUTANT_ELR
                                                                        , MUTANT_ETA
                                                                        , MUTANT_EDA
                                                                        , MUTANT_AAM
                                                                        , MUTANT_ARM
                                                                        , MUTANT_ALM
                                                                        , MUTANT_AAS
                                                                        , MUTANT_ARS
                                                                        , MUTANT_ALS
                                                                        , MUTANT_AAA
                                                                        , MUTANT_ARA
                                                                        , MUTANT_ALA
                                                                        , MUTANT_XBR));

    public static final List<String> BB_WEAK_OPERATOR_LS = Collections.unmodifiableList(
                                                            Arrays.asList(MUTANT_IVR
                                                                        , MUTANT_ITR
                                                                        , MUTANT_EAR
                                                                        , MUTANT_ERR
                                                                        , MUTANT_ELR
                                                                        , MUTANT_ETA
                                                                        , MUTANT_EDA
                                                                        , MUTANT_ERA
                                                                        , MUTANT_ECA
                                                                        , MUTANT_ASR
                                                                        , MUTANT_ACR
                                                                        , MUTANT_AAM
                                                                        , MUTANT_ARM
                                                                        , MUTANT_ALM
                                                                        , MUTANT_ATR
                                                                        , MUTANT_AMR
                                                                        , MUTANT_AAS
                                                                        , MUTANT_ARS
                                                                        , MUTANT_ALS
                                                                        , MUTANT_AAA
                                                                        , MUTANT_ARA
                                                                        , MUTANT_ALA
                                                                        , MUTANT_AOR
                                                                        , MUTANT_ARR
                                                                        , MUTANT_XBR));

     // ถ้าเป็น Expression
     /*
        ** SequenceFlow IVR ITR EAR ERR ELR
        ** 
        ** MultiInstance IVR ITR AAM ARM ALM
        ** 
        ** Time ETA EDA
        ** 
        ** BB-Weak MultiInstance ACR
        ** 
        ** Time ERA ECA
        ** 
    */
    public static final List<String> VARIABLEOPEREATORLS = Collections.unmodifiableList(
                                                                            Arrays.asList(MUTANT_IVR
                                                                                        , MUTANT_ITR));

    public static final List<String> EXPRESSIONOPEREATORLS = Collections.unmodifiableList(
                                                                            Arrays.asList(MUTANT_EAR
                                                                                        , MUTANT_ERR
                                                                                        , MUTANT_ELR
                                                                                        , MUTANT_AAM
                                                                                        , MUTANT_ARM
                                                                                        , MUTANT_ALM));

    public static final List<String> TIMEDELAYOPEREATORLS = Collections.unmodifiableList(
                                                                            Arrays.asList(MUTANT_ETA
                                                                                        , MUTANT_EDA));
    
    public static final List<String> TIMROPEREATORLS = Collections.unmodifiableList(
                                                                            Arrays.asList(MUTANT_ETA
                                                                                        , MUTANT_EDA
                                                                                        , MUTANT_ERA
                                                                                        , MUTANT_ECA));

    public static final String RESULT_KILLED        = "KILLED";
    public static final String RESULT_LIVE          = "LIVE";
    public static final String RESULT_NOT_EXERCISE  = "NOT EXERCISE";

    public static final List<String> METHODVARIABLELS = Collections.unmodifiableList(
                                                                            Arrays.asList(".size()"));

    public static final List<String> SUPPORTEXECUTEMUTANTLS = Collections.unmodifiableList(
                                                                                Arrays.asList(MUTANT_IVR
                                                                                            , MUTANT_ITR
                                                                                            , MUTANT_EAR
                                                                                            , MUTANT_ERR
                                                                                            , MUTANT_ELR
                                                                                            , MUTANT_ETA
                                                                                            , MUTANT_EDA
                                                                                            , MUTANT_ERA
                                                                                            , MUTANT_ECA
                                                                                            , MUTANT_ACR
                                                                                            , MUTANT_AAM
                                                                                            , MUTANT_ARM
                                                                                            , MUTANT_ALM));
}

