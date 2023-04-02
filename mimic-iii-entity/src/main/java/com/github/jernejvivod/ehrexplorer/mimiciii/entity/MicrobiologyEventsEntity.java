package com.github.jernejvivod.ehrexplorer.mimiciii.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.jernejvivod.ehrexplorer.annotation.PropertyOrder;

// Events relating to microbiology tests.
@Entity
@Table(name = "microbiologyevents", schema = "mimiciii", catalog = "mimic")
public class MicrobiologyEventsEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private LocalDateTime chartDate;            // date when the event occurred
    @PropertyOrder(5)
    private LocalDateTime chartTime;            // time when the event occurred, if available
    @PropertyOrder(6)
    private DItemsEntity specItem;              // foreign key identifying the specimen
    @PropertyOrder(7)
    private String specTypeDesc;                // description of the specimen
    @PropertyOrder(8)
    private DItemsEntity orgItem;               // foreign key identifying the organism
    @PropertyOrder(9)
    private String orgName;                     // name of the organism
    @PropertyOrder(10)
    private Short isolateNum;                   // isolate number associated with the test
    @PropertyOrder(11)
    private DItemsEntity abItem;                 // foreign key identifying the antibody
    @PropertyOrder(12)
    private String abName;                       // name of the antibody used
    @PropertyOrder(13)
    private String dilutionText;                 // the dilution amount tested for and the comparison which was made against it (e.g. <=4)
    @PropertyOrder(14)
    private String dilutionComparison;           // the comparison component of DILUTION_TEXT: either <= (less than or equal), = (equal), or >= (greater than or equal), or null when not available
    @PropertyOrder(15)
    private Double dilutionValue;                // the value component of DILUTION_TEXT: must be a floating point number
    @PropertyOrder(16)
    private String interpretation;               // interpretation of the test.

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

    @Column(name = "chartdate")
    public LocalDateTime getChartDate()
    {
        return chartDate;
    }

    public void setChartDate(LocalDateTime chartDate)
    {
        this.chartDate = chartDate;
    }

    @Column(name = "charttime")
    public LocalDateTime getChartTime()
    {
        return chartTime;
    }

    public void setChartTime(LocalDateTime chartTime)
    {
        this.chartTime = chartTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_itemid", referencedColumnName = "itemid")
    public DItemsEntity getSpecItem()
    {
        return specItem;
    }

    public void setSpecItem(DItemsEntity specItem)
    {
        this.specItem = specItem;
    }

    @Column(name = "spec_type_desc", length = 100)
    public String getSpecTypeDesc()
    {
        return specTypeDesc;
    }

    public void setSpecTypeDesc(String specTypeDesc)
    {
        this.specTypeDesc = specTypeDesc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_itemid", referencedColumnName = "itemid")
    public DItemsEntity getOrgItem()
    {
        return orgItem;
    }

    public void setOrgItem(DItemsEntity orgItem)
    {
        this.orgItem = orgItem;
    }

    @Column(name = "org_name", length = 100)
    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    @Column(name = "isolate_num")
    public Short getIsolateNum()
    {
        return isolateNum;
    }

    public void setIsolateNum(Short isolateNum)
    {
        this.isolateNum = isolateNum;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ab_itemid", referencedColumnName = "itemid")
    public DItemsEntity getAbItem()
    {
        return abItem;
    }

    public void setAbItem(DItemsEntity abItem)
    {
        this.abItem = abItem;
    }

    @Column(name = "ab_name", length = 30)
    public String getAbName()
    {
        return abName;
    }

    public void setAbName(String abName)
    {
        this.abName = abName;
    }

    @Column(name = "dilution_text", length = 10)
    public String getDilutionText()
    {
        return dilutionText;
    }

    public void setDilutionText(String dilutionText)
    {
        this.dilutionText = dilutionText;
    }

    @Column(name = "dilution_comparison", length = 20)
    public String getDilutionComparison()
    {
        return dilutionComparison;
    }

    public void setDilutionComparison(String dilutionComparison)
    {
        this.dilutionComparison = dilutionComparison;
    }

    @Column(name = "dilution_value", precision = 0)
    public Double getDilutionValue()
    {
        return dilutionValue;
    }

    public void setDilutionValue(Double dilutionValue)
    {
        this.dilutionValue = dilutionValue;
    }

    @Column(name = "interpretation", length = 5)
    public String getInterpretation()
    {
        return interpretation;
    }

    public void setInterpretation(String interpretation)
    {
        this.interpretation = interpretation;
    }
}
