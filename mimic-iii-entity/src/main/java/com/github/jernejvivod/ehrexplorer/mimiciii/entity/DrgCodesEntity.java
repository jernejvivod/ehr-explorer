package com.github.jernejvivod.ehrexplorer.mimiciii.entity;

import com.github.jernejvivod.ehrexplorer.annotation.PropertyOrder;

import javax.persistence.*;

// Hospital stays classified using the Diagnosis-Related Group system.
@Entity
@Table(name = "drgcodes", schema = "mimiciii", catalog = "mimic")
public class DrgCodesEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;        // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;    // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private String drgType;                       // Type of Diagnosis-Related Group, for example APR is All Patient Refined
    @PropertyOrder(5)
    private String drgCode;                       // Diagnosis-Related Group code
    @PropertyOrder(6)
    private String description;                   // Description of the Diagnosis-Related Group
    @PropertyOrder(7)
    private Short drgSeverity;                    // Relative severity, available for type APR only
    @PropertyOrder(8)
    private Short drgMortality;                   // Relative mortality, available for type APR only

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", referencedColumnName = "subject_id")
    public PatientsEntity getPatientsEntity()
    {
        return patientsEntity;
    }

    public void setPatientsEntity(PatientsEntity patientsEntity)
    {
        this.patientsEntity = patientsEntity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hadm_id", referencedColumnName = "hadm_id")
    public AdmissionsEntity getAdmissionsEntity()
    {
        return admissionsEntity;
    }

    public void setAdmissionsEntity(AdmissionsEntity admissionsEntity)
    {
        this.admissionsEntity = admissionsEntity;
    }

    @Column(name = "drg_type", nullable = false, length = 20)
    public String getDrgType()
    {
        return drgType;
    }

    public void setDrgType(String drgType)
    {
        this.drgType = drgType;
    }

    @Column(name = "drg_code", nullable = false, length = 20)
    public String getDrgCode()
    {
        return drgCode;
    }

    public void setDrgCode(String drgCode)
    {
        this.drgCode = drgCode;
    }

    @Column(name = "description", length = 255)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Column(name = "drg_severity")
    public Short getDrgSeverity()
    {
        return drgSeverity;
    }

    public void setDrgSeverity(Short drgSeverity)
    {
        this.drgSeverity = drgSeverity;
    }

    @Column(name = "drg_mortality")
    public Short getDrgMortality()
    {
        return drgMortality;
    }

    public void setDrgMortality(Short drgMortality)
    {
        this.drgMortality = drgMortality;
    }
}
