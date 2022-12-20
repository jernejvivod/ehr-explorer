package si.jernej.mexplorer.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// Outputs recorded during the ICU stay.
@Entity
@Table(name = "outputevents", schema = "mimiciii", catalog = "mimic")
public class OutputEventsEntity extends AEntity
{
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    private IcuStaysEntity icuStaysEntity;      // foreign key identifying the ICU stay
    private LocalDateTime chartTime;            // time when the output was recorded/occurred
    private DItemsEntity dItemsEntity;          // foreign key identifying the charted item
    private Double value;                       // value of the event as a float
    private String valueUom;                    // unit of measurement
    private LocalDateTime storeTime;            // time when the event was recorded in the system
    private CareGiversEntity careGiversEntity;  // foreign key identifying the caregiver
    private String stopped;                     // event was explicitly marked as stopped. Infrequently used by caregivers
    private String newBottle;                   // not applicable to outputs - column always null
    private Integer isError;                    // flag to highlight an error with the measurement

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
    @JoinColumn(name = "itemid", referencedColumnName = "itemid")
    public DItemsEntity getdItemsEntity()
    {
        return dItemsEntity;
    }

    public void setdItemsEntity(DItemsEntity dItemsEntity)
    {
        this.dItemsEntity = dItemsEntity;
    }

    @Column(name = "value", precision = 0)
    public Double getValue()
    {
        return value;
    }

    public void setValue(Double value)
    {
        this.value = value;
    }

    @Column(name = "valueuom", length = 30)
    public String getValueUom()
    {
        return valueUom;
    }

    public void setValueUom(String valueUom)
    {
        this.valueUom = valueUom;
    }

    @Column(name = "storetime")
    public LocalDateTime getStoreTime()
    {
        return storeTime;
    }

    public void setStoreTime(LocalDateTime storeTime)
    {
        this.storeTime = storeTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cgid", referencedColumnName = "cgid")
    public CareGiversEntity getCareGiversEntity()
    {
        return careGiversEntity;
    }

    public void setCareGiversEntity(CareGiversEntity careGiversEntity)
    {
        this.careGiversEntity = careGiversEntity;
    }

    @Column(name = "stopped", length = 30)
    public String getStopped()
    {
        return stopped;
    }

    public void setStopped(String stopped)
    {
        this.stopped = stopped;
    }

    @Column(name = "newbottle", length = -1)
    public String getNewBottle()
    {
        return newBottle;
    }

    public void setNewBottle(String newBottle)
    {
        this.newBottle = newBottle;
    }

    @Column(name = "iserror")
    public Integer getIsError()
    {
        return isError;
    }

    public void setIsError(Integer isError)
    {
        this.isError = isError;
    }
}
