package si.jernej.mexplorer.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

// ICU admission.
@Entity
@Table(name = "icustays", schema = "mimiciii", catalog = "mimic")
public class IcuStaysEntity extends AEntity
{
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    private int icuStayId;                      // ICU stay ID
    private String dbSource;                    // source database of the item
    private String firstCareUnit;               // first care unit associated with the ICU stay
    private String lastCareUnit;                // last care unit associated with the ICU stay
    private short firstWardId;                  // identifier for the first ward the patient was located in
    private short lastWardId;                   // identifier for the last ward the patient is located in
    private LocalDateTime inTime;               // time of admission to the ICU
    private LocalDateTime outTime;              // time of discharge from the ICU
    private Double los;                         // length of stay in the ICU in fractional days
    private Set<ChartEventsEntity> chartEventsEntitys;
    private Set<DatetimeEventsEntity> datetimeEventsEntitys;
    private Set<InputEventsCvEntity> inputEventsCvEntitys;
    private Set<InputEventsMvEntity> inputEventsMvEntitys;
    private Set<OutputEventsEntity> outputEventsEntitys;
    private Set<PrescriptionsEntity> prescriptionsEntitys;
    private Set<ProcedureEventsMvEntity> procedureEventsMvEntitys;
    private Set<TransfersEntity> transfersEntitys;

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

    @Column(name = "icustay_id", nullable = false)
    public int getIcuStayId()
    {
        return icuStayId;
    }

    public void setIcuStayId(int icuStayId)
    {
        this.icuStayId = icuStayId;
    }

    @Column(name = "dbsource", nullable = false, length = 20)
    public String getDbSource()
    {
        return dbSource;
    }

    public void setDbSource(String dbSource)
    {
        this.dbSource = dbSource;
    }

    @Column(name = "first_careunit", nullable = false, length = 20)
    public String getFirstCareUnit()
    {
        return firstCareUnit;
    }

    public void setFirstCareUnit(String firstCareUnit)
    {
        this.firstCareUnit = firstCareUnit;
    }

    @Column(name = "last_careunit", nullable = false, length = 20)
    public String getLastCareUnit()
    {
        return lastCareUnit;
    }

    public void setLastCareUnit(String lastCareUnit)
    {
        this.lastCareUnit = lastCareUnit;
    }

    @Column(name = "first_wardid", nullable = false)
    public short getFirstWardId()
    {
        return firstWardId;
    }

    public void setFirstWardId(short firstWardId)
    {
        this.firstWardId = firstWardId;
    }

    @Column(name = "last_wardid", nullable = false)
    public short getLastWardId()
    {
        return lastWardId;
    }

    public void setLastWardId(short lastWardId)
    {
        this.lastWardId = lastWardId;
    }

    @Column(name = "intime", nullable = false)
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

    @OneToMany(mappedBy = "icuStaysEntity", fetch = FetchType.LAZY)
    public Set<ChartEventsEntity> getChartEventsEntitys()
    {
        return chartEventsEntitys;
    }

    public void setChartEventsEntitys(Set<ChartEventsEntity> chartEventsEntitys)
    {
        this.chartEventsEntitys = chartEventsEntitys;
    }

    @OneToMany(mappedBy = "icuStaysEntity", fetch = FetchType.LAZY)
    public Set<DatetimeEventsEntity> getDatetimeEventsEntitys()
    {
        return datetimeEventsEntitys;
    }

    public void setDatetimeEventsEntitys(Set<DatetimeEventsEntity> datetimeEventsEntitys)
    {
        this.datetimeEventsEntitys = datetimeEventsEntitys;
    }

    @OneToMany(mappedBy = "icuStaysEntity", fetch = FetchType.LAZY)
    public Set<InputEventsCvEntity> getInputEventsCvEntitys()
    {
        return inputEventsCvEntitys;
    }

    public void setInputEventsCvEntitys(Set<InputEventsCvEntity> inputEventsCvEntitys)
    {
        this.inputEventsCvEntitys = inputEventsCvEntitys;
    }

    @OneToMany(mappedBy = "icuStaysEntity", fetch = FetchType.LAZY)
    public Set<InputEventsMvEntity> getInputEventsMvEntitys()
    {
        return inputEventsMvEntitys;
    }

    public void setInputEventsMvEntitys(Set<InputEventsMvEntity> inputEventsMvEntitys)
    {
        this.inputEventsMvEntitys = inputEventsMvEntitys;
    }

    @OneToMany(mappedBy = "icuStaysEntity", fetch = FetchType.LAZY)
    public Set<OutputEventsEntity> getOutputEventsEntitys()
    {
        return outputEventsEntitys;
    }

    public void setOutputEventsEntitys(Set<OutputEventsEntity> outputEventsEntitys)
    {
        this.outputEventsEntitys = outputEventsEntitys;
    }

    @OneToMany(mappedBy = "icuStaysEntity", fetch = FetchType.LAZY)
    public Set<PrescriptionsEntity> getPrescriptionsEntitys()
    {
        return prescriptionsEntitys;
    }

    public void setPrescriptionsEntitys(Set<PrescriptionsEntity> prescriptionsEntitys)
    {
        this.prescriptionsEntitys = prescriptionsEntitys;
    }

    @OneToMany(mappedBy = "icuStaysEntity", fetch = FetchType.LAZY)
    public Set<ProcedureEventsMvEntity> getProcedureEventsMvEntitys()
    {
        return procedureEventsMvEntitys;
    }

    public void setProcedureEventsMvEntitys(Set<ProcedureEventsMvEntity> procedureEventsMvEntitys)
    {
        this.procedureEventsMvEntitys = procedureEventsMvEntitys;
    }

    @OneToMany(mappedBy = "icuStaysEntity", fetch = FetchType.LAZY)
    public Set<TransfersEntity> getTransfersEntitys()
    {
        return transfersEntitys;
    }

    public void setTransfersEntitys(Set<TransfersEntity> transfersEntitys)
    {
        this.transfersEntitys = transfersEntitys;
    }
}
