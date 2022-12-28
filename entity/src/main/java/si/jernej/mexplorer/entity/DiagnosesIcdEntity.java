package si.jernej.mexplorer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import si.jernej.mexplorer.entity.annotation.PropertyOrder;

// Diagnoses relating to a hospital admission coded using the ICD9 system.
@Entity
@Table(name = "diagnoses_icd", schema = "mimiciii", catalog = "mimic")
public class DiagnosesIcdEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private Integer seqNum;                     // priority of the code. Sequence 1 is the primary code.
    @PropertyOrder(5)
    private String icd9Code;                    // ICD9 code for the diagnosis

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

    @Column(name = "seq_num")
    public Integer getSeqNum()
    {
        return seqNum;
    }

    public void setSeqNum(Integer seqNum)
    {
        this.seqNum = seqNum;
    }

    @Column(name = "icd9_code", length = 10)
    public String getIcd9Code()
    {
        return icd9Code;
    }

    public void setIcd9Code(String icd9Code)
    {
        this.icd9Code = icd9Code;
    }
}
