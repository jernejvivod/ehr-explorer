package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// Location of patients during their hospital stay.
@Entity
@Table(name = "transfers", schema = "mimiciii", catalog = "mimic")
public class TransfersEntity extends AEntity
{
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    private IcuStaysEntity icuStaysEntity;      // foreign key identifying the ICU stay
    private String dbSource;                    // source database of the item
    private String eventType;                   // type of event, for example admission or transfer
    private String prevCareUnit;                // previous careunit
    private String currCareUnit;                // current careunit
    private Short prevWardId;                   // identifier for the previous ward the patient was located in
    private Short currWardId;                   // identifier for the current ward the patient is located in
    private LocalDateTime inTime;               // time when the patient was transferred into the unit
    private LocalDateTime outTime;              // time when the patient was transferred out of the unit
    private Double los;                         // length of stay in minutes

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

    @Column(name = "dbsource", length = 20)
    public String getDbSource()
    {
        return dbSource;
    }

    public void setDbSource(String dbSource)
    {
        this.dbSource = dbSource;
    }

    @Column(name = "eventtype", length = 20)
    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    @Column(name = "prev_careunit", length = 20)
    public String getPrevCareUnit()
    {
        return prevCareUnit;
    }

    public void setPrevCareUnit(String prevCareUnit)
    {
        this.prevCareUnit = prevCareUnit;
    }

    @Column(name = "curr_careunit", length = 20)
    public String getCurrCareUnit()
    {
        return currCareUnit;
    }

    public void setCurrCareUnit(String currCareUnit)
    {
        this.currCareUnit = currCareUnit;
    }

    @Column(name = "prev_wardid")
    public Short getPrevWardId()
    {
        return prevWardId;
    }

    public void setPrevWardId(Short prevWardId)
    {
        this.prevWardId = prevWardId;
    }

    @Column(name = "curr_wardid")
    public Short getCurrWardId()
    {
        return currWardId;
    }

    public void setCurrWardId(Short currWardId)
    {
        this.currWardId = currWardId;
    }

    @Column(name = "intime")
    public LocalDateTime getInTime()
    {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime)
    {
        this.inTime = inTime;
    }

    @Column(name = "outtime")
    public LocalDateTime getOutTime()
    {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime)
    {
        this.outTime = outTime;
    }

    @Column(name = "los", precision = 0)
    public Double getLos()
    {
        return los;
    }

    public void setLos(Double los)
    {
        this.los = los;
    }
}
