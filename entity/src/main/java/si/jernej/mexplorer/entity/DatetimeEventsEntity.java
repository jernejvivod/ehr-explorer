package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// Events relating to a datetime.
@Entity
@Table(name = "datetimeevents", schema = "mimiciii", catalog = "mimic")
public class DatetimeEventsEntity extends AEntity
{
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    private IcuStaysEntity icuStaysEntity;      // foreign key identifying the ICU stay
    private DItemsEntity dItemsEntity;          // foreign key identifying the charted item
    private LocalDateTime chartTime;            // time when the event occurred
    private LocalDateTime storeTime;            // time when the event was recorded in the system
    private CareGiversEntity careGiversEntity;  // foreign key identifying the caregiver
    private LocalDateTime value;                // value of the event as a text string
    private String valueUom;                    // unit of measurement
    private Short warning;                      // flag to highlight that the value has triggered a warning
    private Short error;                        // flag to highlight an error with the event
    private String resultStatus;                // result status of lab data
    private String stopped;                     // event was explicitly marked as stopped. Infrequently used by caregivers.

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

    @Column(name = "charttime", nullable = false)
    public LocalDateTime getChartTime()
    {
        return chartTime;
    }

    public void setChartTime(LocalDateTime chartTime)
    {
        this.chartTime = chartTime;
    }

    @Column(name = "storetime", nullable = false)
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

    @Column(name = "value")
    public LocalDateTime getValue()
    {
        return value;
    }

    public void setValue(LocalDateTime value)
    {
        this.value = value;
    }

    @Column(name = "valueuom", nullable = false, length = 50)
    public String getValueUom()
    {
        return valueUom;
    }

    public void setValueUom(String valueUom)
    {
        this.valueUom = valueUom;
    }

    @Column(name = "warning")
    public Short getWarning()
    {
        return warning;
    }

    public void setWarning(Short warning)
    {
        this.warning = warning;
    }

    @Column(name = "error")
    public Short getError()
    {
        return error;
    }

    public void setError(Short error)
    {
        this.error = error;
    }

    @Column(name = "resultstatus", length = 50)
    public String getResultStatus()
    {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus)
    {
        this.resultStatus = resultStatus;
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
