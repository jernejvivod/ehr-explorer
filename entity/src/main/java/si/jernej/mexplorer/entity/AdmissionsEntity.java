package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import si.jernej.mexplorer.entity.annotation.PropertyOrder;

// Hospital admissions associated with an ICU stay.
@Entity
@Table(name = "admissions", schema = "mimiciii", catalog = "mimic")
public class AdmissionsEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;  // foreign key identifying the patient
    @PropertyOrder(3)
    private Long hadmId;                    // hospital admission ID
    @PropertyOrder(4)
    private LocalDateTime admitTime;        // time of admission to the hospital
    @PropertyOrder(5)
    private LocalDateTime dischTime;        // time of discharge from the hospital
    @PropertyOrder(6)
    private LocalDateTime deathTime;        // time of death
    @PropertyOrder(7)
    private String admissionType;           // type of admission (for example emergency or elective)
    @PropertyOrder(8)
    private String admissionLocation;       // admission location
    @PropertyOrder(9)
    private String dischargeLocation;       // discharge location
    @PropertyOrder(10)
    private String insurance;               // insurance type
    @PropertyOrder(11)
    private String language;                // language
    @PropertyOrder(12)
    private String religion;                // religion
    @PropertyOrder(13)
    private String maritalStatus;           // marital status
    @PropertyOrder(14)
    private String ethnicity;               // ethnicity
    @PropertyOrder(15)
    private LocalDateTime edRegTime;        // ?
    @PropertyOrder(16)
    private LocalDateTime edOutTime;        // ?
    @PropertyOrder(17)
    private String diagnosis;               // diagnosis
    @PropertyOrder(18)
    private Short hospitalExpireFlag;       // ?
    @PropertyOrder(19)
    private short hasChartEventsData;       // hospital admission has at least one observation in the chartevents table
    @PropertyOrder(20)
    private Set<NoteEventsEntity> noteEventsEntitys;
    @PropertyOrder(21)
    private Set<IcuStaysEntity> icuStaysEntitys;
    @PropertyOrder(22)
    private Set<CalloutEntity> calloutEntitys;
    @PropertyOrder(23)
    private Set<ChartEventsEntity> chartEventsEntitys;
    @PropertyOrder(24)
    private Set<CptEventsEntity> cptEventsEntitys;
    @PropertyOrder(25)
    private Set<DatetimeEventsEntity> datetimeEventsEntitys;
    @PropertyOrder(26)
    private Set<DiagnosesIcdEntity> diagnosesIcdEntitys;
    @PropertyOrder(27)
    private Set<DrgCodesEntity> drgCodesEntitys;
    @PropertyOrder(28)
    private Set<InputEventsCvEntity> inputEventsCvEntities;
    @PropertyOrder(29)
    private Set<InputEventsMvEntity> inputEventsMvEntitys;
    @PropertyOrder(30)
    private Set<LabEventsEntity> labEventsEntitys;
    @PropertyOrder(31)
    private Set<MicrobiologyEventsEntity> microbiologyEventsEntitys;
    @PropertyOrder(32)
    private Set<OutputEventsEntity> outputEventsEntitys;
    @PropertyOrder(33)
    private Set<PrescriptionsEntity> prescriptionsEntitys;
    @PropertyOrder(34)
    private Set<ProcedureEventsMvEntity> procedureEventsMvEntitys;
    @PropertyOrder(35)
    private Set<ProceduresIcdEntity> proceduresIcdEntitys;
    @PropertyOrder(36)
    private Set<ServicesEntity> servicesEntitys;
    @PropertyOrder(37)
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

    @Column(name = "hadm_id", nullable = false)
    public Long getHadmId()
    {
        return hadmId;
    }

    public void setHadmId(Long hadmId)
    {
        this.hadmId = hadmId;
    }

    @Column(name = "admittime", nullable = false)
    public LocalDateTime getAdmitTime()
    {
        return admitTime;
    }

    public void setAdmitTime(LocalDateTime admitTime)
    {
        this.admitTime = admitTime;
    }

    @Column(name = "dischtime", nullable = false)
    public LocalDateTime getDischTime()
    {
        return dischTime;
    }

    public void setDischTime(LocalDateTime dischTime)
    {
        this.dischTime = dischTime;
    }

    @Column(name = "deathtime")
    public LocalDateTime getDeathTime()
    {
        return deathTime;
    }

    public void setDeathTime(LocalDateTime deathTime)
    {
        this.deathTime = deathTime;
    }

    @Column(name = "admission_type", nullable = false, length = 50)
    public String getAdmissionType()
    {
        return admissionType;
    }

    public void setAdmissionType(String admissionType)
    {
        this.admissionType = admissionType;
    }

    @Column(name = "admission_location", nullable = false, length = 50)
    public String getAdmissionLocation()
    {
        return admissionLocation;
    }

    public void setAdmissionLocation(String admissionLocation)
    {
        this.admissionLocation = admissionLocation;
    }

    @Column(name = "discharge_location", nullable = false, length = 50)
    public String getDischargeLocation()
    {
        return dischargeLocation;
    }

    public void setDischargeLocation(String dischargeLocation)
    {
        this.dischargeLocation = dischargeLocation;
    }

    @Column(name = "insurance", nullable = false, length = 255)
    public String getInsurance()
    {
        return insurance;
    }

    public void setInsurance(String insurance)
    {
        this.insurance = insurance;
    }

    @Column(name = "language", length = 10)
    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    @Column(name = "religion", length = 50)
    public String getReligion()
    {
        return religion;
    }

    public void setReligion(String religion)
    {
        this.religion = religion;
    }

    @Column(name = "marital_status", length = 50)
    public String getMaritalStatus()
    {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus)
    {
        this.maritalStatus = maritalStatus;
    }

    @Column(name = "ethnicity", nullable = false, length = 200)
    public String getEthnicity()
    {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity)
    {
        this.ethnicity = ethnicity;
    }

    @Column(name = "edregtime")
    public LocalDateTime getEdRegTime()
    {
        return edRegTime;
    }

    public void setEdRegTime(LocalDateTime edRegTime)
    {
        this.edRegTime = edRegTime;
    }

    @Column(name = "edouttime")
    public LocalDateTime getEdOutTime()
    {
        return edOutTime;
    }

    public void setEdOutTime(LocalDateTime edOutTime)
    {
        this.edOutTime = edOutTime;
    }

    @Column(name = "diagnosis", length = 255)
    public String getDiagnosis()
    {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis)
    {
        this.diagnosis = diagnosis;
    }

    @Column(name = "hospital_expire_flag")
    public Short getHospitalExpireFlag()
    {
        return hospitalExpireFlag;
    }

    public void setHospitalExpireFlag(Short hospitalExpireFlag)
    {
        this.hospitalExpireFlag = hospitalExpireFlag;
    }

    @Column(name = "has_chartevents_data", nullable = false)
    public short getHasChartEventsData()
    {
        return hasChartEventsData;
    }

    public void setHasChartEventsData(short hasChartEventsData)
    {
        this.hasChartEventsData = hasChartEventsData;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<NoteEventsEntity> getNoteEventsEntitys()
    {
        return noteEventsEntitys;
    }

    public void setNoteEventsEntitys(Set<NoteEventsEntity> noteEventsEntitys)
    {
        this.noteEventsEntitys = noteEventsEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<IcuStaysEntity> getIcuStaysEntitys()
    {
        return icuStaysEntitys;
    }

    public void setIcuStaysEntitys(Set<IcuStaysEntity> icuStaysEntitys)
    {
        this.icuStaysEntitys = icuStaysEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<CalloutEntity> getCalloutEntitys()
    {
        return calloutEntitys;
    }

    public void setCalloutEntitys(Set<CalloutEntity> calloutEntitys)
    {
        this.calloutEntitys = calloutEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<ChartEventsEntity> getChartEventsEntitys()
    {
        return chartEventsEntitys;
    }

    public void setChartEventsEntitys(Set<ChartEventsEntity> chartEventsEntities)
    {
        this.chartEventsEntitys = chartEventsEntities;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<CptEventsEntity> getCptEventsEntitys()
    {
        return cptEventsEntitys;
    }

    public void setCptEventsEntitys(Set<CptEventsEntity> cptEventsEntitys)
    {
        this.cptEventsEntitys = cptEventsEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<DatetimeEventsEntity> getDatetimeEventsEntitys()
    {
        return datetimeEventsEntitys;
    }

    public void setDatetimeEventsEntitys(Set<DatetimeEventsEntity> datetimeEventsEntitys)
    {
        this.datetimeEventsEntitys = datetimeEventsEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<DiagnosesIcdEntity> getDiagnosesIcdEntitys()
    {
        return diagnosesIcdEntitys;
    }

    public void setDiagnosesIcdEntitys(Set<DiagnosesIcdEntity> diagnosesIcdEntitys)
    {
        this.diagnosesIcdEntitys = diagnosesIcdEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<DrgCodesEntity> getDrgCodesEntitys()
    {
        return drgCodesEntitys;
    }

    public void setDrgCodesEntitys(Set<DrgCodesEntity> drgCodesEntitys)
    {
        this.drgCodesEntitys = drgCodesEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<InputEventsCvEntity> getInputEventsCvEntities()
    {
        return inputEventsCvEntities;
    }

    public void setInputEventsCvEntities(Set<InputEventsCvEntity> inputEventsCvEntities)
    {
        this.inputEventsCvEntities = inputEventsCvEntities;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<InputEventsMvEntity> getInputEventsMvEntitys()
    {
        return inputEventsMvEntitys;
    }

    public void setInputEventsMvEntitys(Set<InputEventsMvEntity> inputEventsMvEntitys)
    {
        this.inputEventsMvEntitys = inputEventsMvEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<LabEventsEntity> getLabEventsEntitys()
    {
        return labEventsEntitys;
    }

    public void setLabEventsEntitys(Set<LabEventsEntity> labEventsEntitys)
    {
        this.labEventsEntitys = labEventsEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<MicrobiologyEventsEntity> getMicrobiologyEventsEntitys()
    {
        return microbiologyEventsEntitys;
    }

    public void setMicrobiologyEventsEntitys(Set<MicrobiologyEventsEntity> microbiologyEventsEntitys)
    {
        this.microbiologyEventsEntitys = microbiologyEventsEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<OutputEventsEntity> getOutputEventsEntitys()
    {
        return outputEventsEntitys;
    }

    public void setOutputEventsEntitys(Set<OutputEventsEntity> outputEventsEntitys)
    {
        this.outputEventsEntitys = outputEventsEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<PrescriptionsEntity> getPrescriptionsEntitys()
    {
        return prescriptionsEntitys;
    }

    public void setPrescriptionsEntitys(Set<PrescriptionsEntity> prescriptionsEntitys)
    {
        this.prescriptionsEntitys = prescriptionsEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<ProcedureEventsMvEntity> getProcedureEventsMvEntitys()
    {
        return procedureEventsMvEntitys;
    }

    public void setProcedureEventsMvEntitys(Set<ProcedureEventsMvEntity> procedureEventsMvEntitys)
    {
        this.procedureEventsMvEntitys = procedureEventsMvEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<ProceduresIcdEntity> getProceduresIcdEntitys()
    {
        return proceduresIcdEntitys;
    }

    public void setProceduresIcdEntitys(Set<ProceduresIcdEntity> proceduresIcdEntitys)
    {
        this.proceduresIcdEntitys = proceduresIcdEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<ServicesEntity> getServicesEntitys()
    {
        return servicesEntitys;
    }

    public void setServicesEntitys(Set<ServicesEntity> servicesEntitys)
    {
        this.servicesEntitys = servicesEntitys;
    }

    @OneToMany(mappedBy = "admissionsEntity", fetch = FetchType.LAZY)
    public Set<TransfersEntity> getTransfersEntitys()
    {
        return transfersEntitys;
    }

    public void setTransfersEntitys(Set<TransfersEntity> transfersEntitys)
    {
        this.transfersEntitys = transfersEntitys;
    }
}
