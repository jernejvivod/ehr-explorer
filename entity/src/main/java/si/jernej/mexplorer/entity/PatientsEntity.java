package si.jernej.mexplorer.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

// Patients associated with an admission to the ICU.
@Entity
@Table(name = "patients", schema = "mimiciii", catalog = "mimic")
public class PatientsEntity extends AEntity
{
    private int subjectId;                            // patient's ID
    private String gender;                            // patient's gender
    private LocalDateTime dob;                        // date of birth
    private LocalDateTime dod;                        // date of death (null if the patient was alive at least 90 days post hospital discharge)
    private LocalDateTime dodHosp;                    // date of death recorded in the hospital records
    private LocalDateTime dodSsn;                     // date of death recorded in the social security records
    private int expireFlag;                           // Lag indicating that the patient has died
    private Set<AdmissionsEntity> admissionsEntitys;
    private Set<IcuStaysEntity> icuStaysEntitys;
    private Set<NoteEventsEntity> noteEventsEntitys;
    private Set<CalloutEntity> calloutEntitys;
    private Set<ChartEventsEntity> chartEventsEntitys;
    private Set<CptEventsEntity> cptEventsEntitys;
    private Set<DatetimeEventsEntity> datetimeEventsEntitys;
    private Set<DiagnosesIcdEntity> diagnosesIcdEntitys;
    private Set<DrgCodesEntity> drgCodesEntitys;
    private Set<InputEventsCvEntity> inputEventsCvEntitys;
    private Set<InputEventsMvEntity> inputEventsMvEntitys;
    private Set<LabEventsEntity> labEventsEntitys;
    private Set<MicrobiologyEventsEntity> microbiologyEventsEntitys;
    private Set<OutputEventsEntity> outputEventsEntitys;
    private Set<PrescriptionsEntity> prescriptionsEntitys;
    private Set<ProcedureEventsMvEntity> procedureEventsMvEntitys;
    private Set<ProceduresIcdEntity> proceduresIcdEntitys;
    private Set<ServicesEntity> servicesEntitys;
    private Set<TransfersEntity> transfersEntitys;

    @Column(name = "subject_id", nullable = false)
    public int getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(int subjectId)
    {
        this.subjectId = subjectId;
    }

    @Column(name = "gender", nullable = false, length = 5)
    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    @Column(name = "dob", nullable = false)
    public LocalDateTime getDob()
    {
        return dob;
    }

    public void setDob(LocalDateTime dob)
    {
        this.dob = dob;
    }

    @Column(name = "dod")
    public LocalDateTime getDod()
    {
        return dod;
    }

    public void setDod(LocalDateTime dod)
    {
        this.dod = dod;
    }

    @Column(name = "dod_hosp")
    public LocalDateTime getDodHosp()
    {
        return dodHosp;
    }

    public void setDodHosp(LocalDateTime dodHosp)
    {
        this.dodHosp = dodHosp;
    }

    @Column(name = "dod_ssn")
    public LocalDateTime getDodSsn()
    {
        return dodSsn;
    }

    public void setDodSsn(LocalDateTime dodSsn)
    {
        this.dodSsn = dodSsn;
    }

    @Column(name = "expire_flag", nullable = false)
    public int getExpireFlag()
    {
        return expireFlag;
    }

    public void setExpireFlag(int expireFlag)
    {
        this.expireFlag = expireFlag;
    }

    @OneToMany(targetEntity = AdmissionsEntity.class, mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<AdmissionsEntity> getAdmissionsEntitys()
    {
        return admissionsEntitys;
    }

    public void setAdmissionsEntitys(Set<AdmissionsEntity> admissionsEntitys)
    {
        this.admissionsEntitys = admissionsEntitys;
    }

    @OneToMany(targetEntity = IcuStaysEntity.class, mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<IcuStaysEntity> getIcuStaysEntitys()
    {
        return icuStaysEntitys;
    }

    public void setIcuStaysEntitys(Set<IcuStaysEntity> icuStaysEntitys)
    {
        this.icuStaysEntitys = icuStaysEntitys;
    }

    @OneToMany(targetEntity = NoteEventsEntity.class, mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<NoteEventsEntity> getNoteEventsEntitys()
    {
        return noteEventsEntitys;
    }

    public void setNoteEventsEntitys(Set<NoteEventsEntity> noteEventsEntitys)
    {
        this.noteEventsEntitys = noteEventsEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<CalloutEntity> getCalloutEntitys()
    {
        return calloutEntitys;
    }

    public void setCalloutEntitys(Set<CalloutEntity> calloutEntitys)
    {
        this.calloutEntitys = calloutEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<ChartEventsEntity> getChartEventsEntitys()
    {
        return chartEventsEntitys;
    }

    public void setChartEventsEntitys(Set<ChartEventsEntity> chartEventsEntitys)
    {
        this.chartEventsEntitys = chartEventsEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<CptEventsEntity> getCptEventsEntitys()
    {
        return cptEventsEntitys;
    }

    public void setCptEventsEntitys(Set<CptEventsEntity> cptEventsEntitys)
    {
        this.cptEventsEntitys = cptEventsEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<DatetimeEventsEntity> getDatetimeEventsEntitys()
    {
        return datetimeEventsEntitys;
    }

    public void setDatetimeEventsEntitys(Set<DatetimeEventsEntity> datetimeEventsEntitys)
    {
        this.datetimeEventsEntitys = datetimeEventsEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<DiagnosesIcdEntity> getDiagnosesIcdEntitys()
    {
        return diagnosesIcdEntitys;
    }

    public void setDiagnosesIcdEntitys(Set<DiagnosesIcdEntity> diagnosesIcdEntitys)
    {
        this.diagnosesIcdEntitys = diagnosesIcdEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<DrgCodesEntity> getDrgCodesEntitys()
    {
        return drgCodesEntitys;
    }

    public void setDrgCodesEntitys(Set<DrgCodesEntity> drgCodesEntitys)
    {
        this.drgCodesEntitys = drgCodesEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<InputEventsCvEntity> getInputEventsCvEntitys()
    {
        return inputEventsCvEntitys;
    }

    public void setInputEventsCvEntitys(Set<InputEventsCvEntity> inputEventsCvEntitys)
    {
        this.inputEventsCvEntitys = inputEventsCvEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<InputEventsMvEntity> getInputEventsMvEntitys()
    {
        return inputEventsMvEntitys;
    }

    public void setInputEventsMvEntitys(Set<InputEventsMvEntity> inputEventsMvEntitys)
    {
        this.inputEventsMvEntitys = inputEventsMvEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<LabEventsEntity> getLabEventsEntitys()
    {
        return labEventsEntitys;
    }

    public void setLabEventsEntitys(Set<LabEventsEntity> labEventsEntitys)
    {
        this.labEventsEntitys = labEventsEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<MicrobiologyEventsEntity> getMicrobiologyEventsEntitys()
    {
        return microbiologyEventsEntitys;
    }

    public void setMicrobiologyEventsEntitys(Set<MicrobiologyEventsEntity> microbiologyEventsEntitys)
    {
        this.microbiologyEventsEntitys = microbiologyEventsEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<OutputEventsEntity> getOutputEventsEntitys()
    {
        return outputEventsEntitys;
    }

    public void setOutputEventsEntitys(Set<OutputEventsEntity> outputEventsEntitys)
    {
        this.outputEventsEntitys = outputEventsEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<PrescriptionsEntity> getPrescriptionsEntitys()
    {
        return prescriptionsEntitys;
    }

    public void setPrescriptionsEntitys(Set<PrescriptionsEntity> prescriptionsEntitys)
    {
        this.prescriptionsEntitys = prescriptionsEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<ProcedureEventsMvEntity> getProcedureEventsMvEntitys()
    {
        return procedureEventsMvEntitys;
    }

    public void setProcedureEventsMvEntitys(Set<ProcedureEventsMvEntity> procedureEventsMvEntitys)
    {
        this.procedureEventsMvEntitys = procedureEventsMvEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<ProceduresIcdEntity> getProceduresIcdEntitys()
    {
        return proceduresIcdEntitys;
    }

    public void setProceduresIcdEntitys(Set<ProceduresIcdEntity> proceduresIcdEntitys)
    {
        this.proceduresIcdEntitys = proceduresIcdEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<ServicesEntity> getServicesEntitys()
    {
        return servicesEntitys;
    }

    public void setServicesEntitys(Set<ServicesEntity> servicesEntitys)
    {
        this.servicesEntitys = servicesEntitys;
    }

    @OneToMany(mappedBy = "patientsEntity", fetch = FetchType.LAZY)
    public Set<TransfersEntity> getTransfersEntitys()
    {
        return transfersEntitys;
    }

    public void setTransfersEntitys(Set<TransfersEntity> transfersEntitys)
    {
        this.transfersEntitys = transfersEntitys;
    }
}
