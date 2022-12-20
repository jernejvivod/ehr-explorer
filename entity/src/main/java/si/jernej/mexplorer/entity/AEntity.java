package si.jernej.mexplorer.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AEntity implements Serializable
{
    private Long rowId;  // unique row identifier

    @Id
    @Column(name = "row_id", nullable = false)
    public Long getRowId()
    {
        return rowId;
    }

    public void setRowId(Long rowId)
    {
        this.rowId = rowId;
    }

    @Override
    public boolean equals(Object o) // specific entity-model related
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof AEntity that) || !getClass().isInstance(o))
        {
            return false;
        }

        return getRowId() != null && getRowId().equals(that.getRowId());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRowId() == null) ? 0 : getRowId().hashCode());
        return result;
    }
}
