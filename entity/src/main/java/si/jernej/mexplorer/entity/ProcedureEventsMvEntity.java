package si.jernej.mexplorer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import si.jernej.mexplorer.entity.annotation.PropertyOrder;

// Procedure start and stop times recorded for MetaVision patients.
@Entity
@Table(name = "procedureevents_mv", schema = "mimiciii", catalog = "mimic")
public class ProcedureEventsMvEntity extends AEntity
{
    @PropertyOrder(2)
    private PatientsEntity patientsEntity;      // foreign key identifying the patient
    @PropertyOrder(3)
    private AdmissionsEntity admissionsEntity;  // foreign key identifying the hospital stay
    @PropertyOrder(4)
    private IcuStaysEntity icuStaysEntity;      // foreign key identifying the ICU stay
    @PropertyOrder(5)
    private LocalDateTime startTime;            // ?
    @PropertyOrder(6)
    private LocalDateTime endTime;              // ?
    @PropertyOrder(7)
    private DItemsEntity dItemsEntity;          // ?
    @PropertyOrder(8)
    private Double value;                       // ?
    @PropertyOrder(9)
    private String valueUom;                    // ?
    @PropertyOrder(10)
    private String location;                    // ?
    @PropertyOrder(11)
    private String locationCategory;            // ?
    @PropertyOrder(12)
    private LocalDateTime storeTime;            // ?
    @PropertyOrder(13)
    private CareGiversEntity careGiversEntity;  // ?
    @PropertyOrder(14)
    private Integer orderId;                    // ?
    @PropertyOrder(15)
    private Integer linkOrderId;                // ?
    @PropertyOrder(16)
    private String orderCategoryName;           // ?
    @PropertyOrder(17)
    private String secondaryOrderCategoryName;  // ?
    @PropertyOrder(18)
    private String orderCategoryDescription;    // ?
    @PropertyOrder(19)
    private Short isOpenBag;                    // ?
    @PropertyOrder(20)
    private Short continueInNextDept;           // ?
    @PropertyOrder(21)
    private Short cancelReason;                 // ?
    @PropertyOrder(22)
    private String statusDescription;           // ?
    @PropertyOrder(23)
    private String commentsEditedBy;            // ?
    @PropertyOrder(24)
    private String commentsCanceledBy;          // ?
    @PropertyOrder(25)
    private LocalDateTime commentsDate;         // ?

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

    @Column(name = "starttime")
    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    @Column(name = "endtime")
    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
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

    @Column(name = "value", precision = 0)
    public Double getValue()
    {
        return value;
    }

    public void setValue(Double value)
    {
        this.value = value;
    }

    @Column(name = "valueuom", length = 30)
    public String getValueUom()
    {
        return valueUom;
    }

    public void setValueUom(String valueUom)
    {
        this.valueUom = valueUom;
    }

    @Column(name = "location", length = 30)
    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    @Column(name = "locationcategory", length = 30)
    public String getLocationCategory()
    {
        return locationCategory;
    }

    public void setLocationCategory(String locationCategory)
    {
        this.locationCategory = locationCategory;
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

    @Column(name = "orderid")
    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Column(name = "linkorderid")
    public Integer getLinkOrderId()
    {
        return linkOrderId;
    }

    public void setLinkOrderId(Integer linkOrderId)
    {
        this.linkOrderId = linkOrderId;
    }

    @Column(name = "ordercategoryname", length = 100)
    public String getOrderCategoryName()
    {
        return orderCategoryName;
    }

    public void setOrderCategoryName(String orderCategoryName)
    {
        this.orderCategoryName = orderCategoryName;
    }

    @Column(name = "secondaryordercategoryname", length = 100)
    public String getSecondaryOrderCategoryName()
    {
        return secondaryOrderCategoryName;
    }

    public void setSecondaryOrderCategoryName(String secondaryOrderCategoryName)
    {
        this.secondaryOrderCategoryName = secondaryOrderCategoryName;
    }

    @Column(name = "ordercategorydescription", length = 50)
    public String getOrderCategoryDescription()
    {
        return orderCategoryDescription;
    }

    public void setOrderCategoryDescription(String orderCategoryDescription)
    {
        this.orderCategoryDescription = orderCategoryDescription;
    }

    @Column(name = "isopenbag")
    public Short getIsOpenBag()
    {
        return isOpenBag;
    }

    public void setIsOpenBag(Short isOpenBag)
    {
        this.isOpenBag = isOpenBag;
    }

    @Column(name = "continueinnextdept")
    public Short getContinueInNextDept()
    {
        return continueInNextDept;
    }

    public void setContinueInNextDept(Short continueInNextDept)
    {
        this.continueInNextDept = continueInNextDept;
    }

    @Column(name = "cancelreason")
    public Short getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(Short cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    @Column(name = "statusdescription", length = 30)
    public String getStatusDescription()
    {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription)
    {
        this.statusDescription = statusDescription;
    }

    @Column(name = "comments_editedby", length = 30)
    public String getCommentsEditedBy()
    {
        return commentsEditedBy;
    }

    public void setCommentsEditedBy(String commentsEditedBy)
    {
        this.commentsEditedBy = commentsEditedBy;
    }

    @Column(name = "comments_canceledby", length = 30)
    public String getCommentsCanceledBy()
    {
        return commentsCanceledBy;
    }

    public void setCommentsCanceledBy(String commentsCanceledBy)
    {
        this.commentsCanceledBy = commentsCanceledBy;
    }

    @Column(name = "comments_date")
    public LocalDateTime getCommentsDate()
    {
        return commentsDate;
    }

    public void setCommentsDate(LocalDateTime commentsDate)
    {
        this.commentsDate = commentsDate;
    }
}
