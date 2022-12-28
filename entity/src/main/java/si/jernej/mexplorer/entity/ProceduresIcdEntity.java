package si.jernej.mexplorer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import si.jernej.mexplorer.entity.annotation.PropertyOrder;

// Procedures relating to a hospital admission coded using the ICD9 system.
@Entity
@Table(name = "procedures_icd", schema = "mimiciii", catalog = "mimic")
public class ProceduresIcdEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private int seqNum;                         // lower procedure numbers occurred earlier
    @PropertyOrder(5)
    private String icd9Code;                    // ICD9 code associated with the procedure

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

    @Column(name = "seq_num", nullable = false)
    public int getSeqNum()
    {
        return seqNum;
    }

    public void setSeqNum(int seqNum)
    {
        this.seqNum = seqNum;
    }

    @Column(name = "icd9_code", nullable = false, length = 10)
    public String getIcd9Code()
    {
        return icd9Code;
    }

    public void setIcd9Code(String icd9Code)
    {
        this.icd9Code = icd9Code;
    }
}
