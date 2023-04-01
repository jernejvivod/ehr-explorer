package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import si.jernej.mexplorer.annotation.PropertyOrder;

// Events occurring on a patient chart.
@Entity
@Table(name = "chartevents", schema = "mimiciii", catalog = "mimic")
public class ChartEventsEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private IcuStaysEntity icuStaysEntity;      // foreign key identifying the ICU stay
    @PropertyOrder(5)
    private DItemsEntity dItemsEntity;          // foreign key identifying the charted item
    @PropertyOrder(6)
    private LocalDateTime chartTime;            // time when the event occurred
    @PropertyOrder(7)
    private LocalDateTime storeTime;            // time when the event was recorded in the system
    @PropertyOrder(8)
    private CareGiversEntity careGiversEntity;  // foreign key identifying the caregiver
    @PropertyOrder(9)
    private String value;                       // value of the event as a text string
    @PropertyOrder(10)
    private Double valueNum;                    // value of the event as a number
    @PropertyOrder(11)
    private String valueUom;                    // unit of measurement
    @PropertyOrder(12)
    private Integer warning;                    // flag to highlight that the value has triggered a warning
    @PropertyOrder(13)
    private Integer error;                      // flag to highlight an error with the event
    @PropertyOrder(14)
    private String resultStatus;                // result status of lab data
    @PropertyOrder(15)
    private String stopped;                     // text string indicating the stopped status of an event (i.e. stopped, not stopped)

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

    @Column(name = "charttime")
    public LocalDateTime getChartTime()
    {
        return chartTime;
    }

    public void setChartTime(LocalDateTime chartTime)
    {
        this.chartTime = chartTime;
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

    @Column(name = "value", length = 255)
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

    @Column(name = "valueuom", length = 50)
    public String getValueUom()
    {
        return valueUom;
    }

    public void setValueUom(String valueUom)
    {
        this.valueUom = valueUom;
    }

    @Column(name = "warning")
    public Integer getWarning()
    {
        return warning;
    }

    public void setWarning(Integer warning)
    {
        this.warning = warning;
    }

    @Column(name = "error")
    public Integer getError()
    {
        return error;
    }

    public void setError(Integer error)
    {
        this.error = error;
    }

    @Column(name = "resultstatus", length = 50)
    public String getResultStatus()
    {
        return resultStatus;
    }

    public void setResultStatus(String resultstatus)
    {
        this.resultStatus = resultstatus;
    }

    @Column(name = "stopped", length = 50)
    public String getStopped()
    {
        return stopped;
    }

    public void setStopped(String stopped)
    {
        this.stopped = stopped;
    }

}
