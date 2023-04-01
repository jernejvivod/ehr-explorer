package si.jernej.mexplorer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import si.jernej.mexplorer.annotation.PropertyOrder;

// High-level dictionary of the Current Procedural Terminology.
@Entity
@Table(name = "d_cpt", schema = "mimiciii", catalog = "mimic")
public class DCptEntity extends AEntity
{
    @PropertyOrder(2)
    private short category;           // code category
    @PropertyOrder(3)
    private String sectionRange;      // range of codes within the high-level section
    @PropertyOrder(4)
    private String sectionHeader;     // section header
    @PropertyOrder(5)
    private String subsectionRange;   // range of codes within the subsection
    @PropertyOrder(6)
    private String subsectionHeader;  // subsection header
    @PropertyOrder(7)
    private String codeSuffix;        // text element of the current procedural terminology, if any
    @PropertyOrder(8)
    private int minCodeInSubsection;  // minimum code within the subsection
    @PropertyOrder(9)
    private int maxCodeInSubsection;  // maximum code within the subsection

    @Column(name = "category", nullable = false)
    public short getCategory()
    {
        return category;
    }

    public void setCategory(short category)
    {
        this.category = category;
    }

    @Column(name = "sectionrange", nullable = false, length = 100)
    public String getSectionRange()
    {
        return sectionRange;
    }

    public void setSectionRange(String sectionRange)
    {
        this.sectionRange = sectionRange;
    }

    @Column(name = "sectionheader", nullable = false, length = 50)
    public String getSectionHeader()
    {
        return sectionHeader;
    }

    public void setSectionHeader(String sectionHeader)
    {
        this.sectionHeader = sectionHeader;
    }

    @Column(name = "subsectionrange", nullable = false, length = 100)
    public String getSubsectionRange()
    {
        return subsectionRange;
    }

    public void setSubsectionRange(String subsectionRange)
    {
        this.subsectionRange = subsectionRange;
    }

    @Column(name = "subsectionheader", nullable = false, length = 255)
    public String getSubsectionHeader()
    {
        return subsectionHeader;
    }

    public void setSubsectionHeader(String subsectionHeader)
    {
        this.subsectionHeader = subsectionHeader;
    }

    @Column(name = "codesuffix", length = 5)
    public String getCodeSuffix()
    {
        return codeSuffix;
    }

    public void setCodeSuffix(String codeSuffix)
    {
        this.codeSuffix = codeSuffix;
    }

    @Column(name = "mincodeinsubsection", nullable = false)
    public int getMinCodeInSubsection()
    {
        return minCodeInSubsection;
    }

    public void setMinCodeInSubsection(int minCodeInSubsection)
    {
        this.minCodeInSubsection = minCodeInSubsection;
    }

    @Column(name = "maxcodeinsubsection", nullable = false)
    public int getMaxCodeInSubsection()
    {
        return maxCodeInSubsection;
    }

    public void setMaxCodeInSubsection(int maxCodeInSubsection)
    {
        this.maxCodeInSubsection = maxCodeInSubsection;
    }
}
