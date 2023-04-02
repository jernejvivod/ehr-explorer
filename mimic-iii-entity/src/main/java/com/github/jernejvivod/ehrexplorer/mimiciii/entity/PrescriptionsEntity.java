package com.github.jernejvivod.ehrexplorer.mimiciii.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.jernejvivod.ehrexplorer.annotation.PropertyOrder;

// Medicines prescribed.
@Entity
@Table(name = "prescriptions", schema = "mimiciii", catalog = "mimic")
public class PrescriptionsEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private IcuStaysEntity icuStaysEntity;      // foreign key identifying the ICU stay
    @PropertyOrder(5)
    private LocalDateTime startDate;            // date when the prescription started
    @PropertyOrder(6)
    private LocalDateTime endDate;              // date when the prescription ended
    @PropertyOrder(7)
    private String drugType;                    // type of drug
    @PropertyOrder(8)
    private String drug;                        // name of the drug
    @PropertyOrder(9)
    private String drugNamePoe;                 // name of the drug on the Provider Order Entry Interface
    @PropertyOrder(10)
    private String drugNameGeneric;             // generic drug name
    @PropertyOrder(11)
    private String formularyDrugCd;             // formulary drug code
    @PropertyOrder(12)
    private String gsn;                         // generic sequence number
    @PropertyOrder(13)
    private String ndc;                         // national drug code
    @PropertyOrder(14)
    private String prodStrength;                // strength of the drug (product)
    @PropertyOrder(15)
    private String doseValRx;                   // dose of the drug prescribed
    @PropertyOrder(16)
    private String doseUnitRx;                  // unit of measurement associated with the dose
    @PropertyOrder(17)
    private String formValDisp;                 // amount of the formulation dispensed
    @PropertyOrder(18)
    private String formUnitDisp;                // unit of measurement associated with the formulation
    @PropertyOrder(19)
    private String route;                       // route of administration, for example intravenous or oral

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icustay_id", referencedColumnName = "icustay_id")
    public IcuStaysEntity getIcuStaysEntity()
    {
        return icuStaysEntity;
    }

    public void setIcuStaysEntity(IcuStaysEntity icuStaysEntity)
    {
        this.icuStaysEntity = icuStaysEntity;
    }

    @Column(name = "startdate")
    public LocalDateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate)
    {
        this.startDate = startDate;
    }

    @Column(name = "enddate")
    public LocalDateTime getEndDate()
    {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate)
    {
        this.endDate = endDate;
    }

    @Column(name = "drug_type", nullable = false, length = 100)
    public String getDrugType()
    {
        return drugType;
    }

    public void setDrugType(String drugType)
    {
        this.drugType = drugType;
    }

    @Column(name = "drug", nullable = false, length = 100)
    public String getDrug()
    {
        return drug;
    }

    public void setDrug(String drug)
    {
        this.drug = drug;
    }

    @Column(name = "drug_name_poe", length = 100)
    public String getDrugNamePoe()
    {
        return drugNamePoe;
    }

    public void setDrugNamePoe(String drugNamePoe)
    {
        this.drugNamePoe = drugNamePoe;
    }

    @Column(name = "drug_name_generic", length = 100)
    public String getDrugNameGeneric()
    {
        return drugNameGeneric;
    }

    public void setDrugNameGeneric(String drugNameGeneric)
    {
        this.drugNameGeneric = drugNameGeneric;
    }

    @Column(name = "formulary_drug_cd", length = 120)
    public String getFormularyDrugCd()
    {
        return formularyDrugCd;
    }

    public void setFormularyDrugCd(String formularyDrugCd)
    {
        this.formularyDrugCd = formularyDrugCd;
    }

    @Column(name = "gsn", length = 200)
    public String getGsn()
    {
        return gsn;
    }

    public void setGsn(String gsn)
    {
        this.gsn = gsn;
    }

    @Column(name = "ndc", length = 120)
    public String getNdc()
    {
        return ndc;
    }

    public void setNdc(String ndc)
    {
        this.ndc = ndc;
    }

    @Column(name = "prod_strength", length = 120)
    public String getProdStrength()
    {
        return prodStrength;
    }

    public void setProdStrength(String prodStrength)
    {
        this.prodStrength = prodStrength;
    }

    @Column(name = "dose_val_rx", length = 120)
    public String getDoseValRx()
    {
        return doseValRx;
    }

    public void setDoseValRx(String doseValRx)
    {
        this.doseValRx = doseValRx;
    }

    @Column(name = "dose_unit_rx", length = 120)
    public String getDoseUnitRx()
    {
        return doseUnitRx;
    }

    public void setDoseUnitRx(String doseUnitRx)
    {
        this.doseUnitRx = doseUnitRx;
    }

    @Column(name = "form_val_disp", length = 120)
    public String getFormValDisp()
    {
        return formValDisp;
    }

    public void setFormValDisp(String formValDisp)
    {
        this.formValDisp = formValDisp;
    }

    @Column(name = "form_unit_disp", length = 120)
    public String getFormUnitDisp()
    {
        return formUnitDisp;
    }

    public void setFormUnitDisp(String formUnitDisp)
    {
        this.formUnitDisp = formUnitDisp;
    }

    @Column(name = "route", length = 120)
    public String getRoute()
    {
        return route;
    }

    public void setRoute(String route)
    {
        this.route = route;
    }
}
