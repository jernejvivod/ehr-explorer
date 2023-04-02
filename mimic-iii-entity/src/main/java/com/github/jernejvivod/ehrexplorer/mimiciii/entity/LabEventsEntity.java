package com.github.jernejvivod.ehrexplorer.mimiciii.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.jernejvivod.ehrexplorer.annotation.PropertyOrder;

// Events relating to laboratory tests.
@Entity
@Table(name = "labevents", schema = "mimiciii", catalog = "mimic")
public class LabEventsEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private DItemsEntity dItemsEntity;          // foreign key identifying the charted item
    @PropertyOrder(5)
    private LocalDateTime chartTime;            // time when the event occurred
    @PropertyOrder(6)
    private String value;                       // value of the event as a text string
    @PropertyOrder(7)
    private Double valueNum;                    // value of the event as a number
    @PropertyOrder(8)
    private String valueUom;                    // unit of measuremen
    @PropertyOrder(9)
    private String flag;                        // flag indicating whether the lab test value is considered abnormal (null if the test was normal)

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

    @ManyToOne
    @JoinColumn(name = "hadm_id", referencedColumnName = "hadm_id")
    public AdmissionsEntity getAdmissionsEntity()
    {
        return admissionsEntity;
    }

    public void setAdmissionsEntity(AdmissionsEntity admissionsEntity)
    {
        this.admissionsEntity = admissionsEntity;
    }

    @ManyToOne
    @JoinColumn(name = "itemid", referencedColumnName = "itemid")
    public DItemsEntity getdItemsEntity()
    {
        return dItemsEntity;
    }

    public void setdItemsEntity(DItemsEntity dItemsEntity)
    {
        this.dItemsEntity = dItemsEntity;
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

    @Column(name = "value", length = 200)
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Column(name = "valuenum", precision = 0)
    public Double getValueNum()
    {
        return valueNum;
    }

    public void setValueNum(Double valueNum)
    {
        this.valueNum = valueNum;
    }

    @Column(name = "valueuom", length = 20)
    public String getValueUom()
    {
        return valueUom;
    }

    public void setValueUom(String valueUom)
    {
        this.valueUom = valueUom;
    }

    @Column(name = "flag", length = 20)
    public String getFlag()
    {
        return flag;
    }

    public void setFlag(String flag)
    {
        this.flag = flag;
    }

}
