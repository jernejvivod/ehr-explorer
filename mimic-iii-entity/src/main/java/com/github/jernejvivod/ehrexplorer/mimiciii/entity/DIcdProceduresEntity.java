package com.github.jernejvivod.ehrexplorer.mimiciii.entity;

import com.github.jernejvivod.ehrexplorer.annotation.PropertyOrder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

// Dictionary of the International Classification of Diseases, 9th Revision (Procedures).
@Entity
@Table(name = "d_icd_procedures", schema = "mimiciii", catalog = "mimic")
public class DIcdProceduresEntity extends AEntity
{
    @PropertyOrder(2)
    private String icd9Code;    // ICD9 code - note that this is a fixed length character field, as whitespaces are important in uniquely identifying ICD-9 codes.
    @PropertyOrder(3)
    private String shortTitle;  // short title associated with the code
    @PropertyOrder(4)
    private String longTitle;   // long title associated with the code

    @Column(name = "icd9_code", nullable = false, length = 10)
    public String getIcd9Code()
    {
        return icd9Code;
    }

    public void setIcd9Code(String icd9Code)
    {
        this.icd9Code = icd9Code;
    }

    @Column(name = "short_title", nullable = false, length = 50)
    public String getShortTitle()
    {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle)
    {
        this.shortTitle = shortTitle;
    }

    @Column(name = "long_title", nullable = false, length = 255)
    public String getLongTitle()
    {
        return longTitle;
    }

    public void setLongTitle(String longTitle)
    {
        this.longTitle = longTitle;
    }
}
