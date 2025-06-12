

package com.batch.Database.Entities;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("TagLog")
public class TagLog {
    @Id
    private Long id;
    private String name;
    private String attribute;
    private double value;
    @CreatedDate
    private LocalTime time;
    @CreatedDate
    private LocalDate Date;

    public TagLog(String name, String attribute, double value) {
        this.name = name;
        this.attribute = attribute;
        this.value = value;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public double getValue() {
        return this.value;
    }

    public LocalTime getTime() {
        return this.time;
    }

    public LocalDate getDate() {
        return this.Date;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    public void setTime(final LocalTime time) {
        this.time = time;
    }

    public void setDate(final LocalDate Date) {
        this.Date = Date;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TagLog)) {
            return false;
        } else {
            TagLog other = (TagLog)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (Double.compare(this.getValue(), other.getValue()) != 0) {
                return false;
            } else {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$attribute = this.getAttribute();
                Object other$attribute = other.getAttribute();
                if (this$attribute == null) {
                    if (other$attribute != null) {
                        return false;
                    }
                } else if (!this$attribute.equals(other$attribute)) {
                    return false;
                }

                Object this$time = this.getTime();
                Object other$time = other.getTime();
                if (this$time == null) {
                    if (other$time != null) {
                        return false;
                    }
                } else if (!this$time.equals(other$time)) {
                    return false;
                }

                Object this$Date = this.getDate();
                Object other$Date = other.getDate();
                if (this$Date == null) {
                    if (other$Date != null) {
                        return false;
                    }
                } else if (!this$Date.equals(other$Date)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TagLog;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $value = Double.doubleToLongBits(this.getValue());
        result = result * 59 + (int)($value >>> 32 ^ $value);
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $attribute = this.getAttribute();
        result = result * 59 + ($attribute == null ? 43 : $attribute.hashCode());
        Object $time = this.getTime();
        result = result * 59 + ($time == null ? 43 : $time.hashCode());
        Object $Date = this.getDate();
        result = result * 59 + ($Date == null ? 43 : $Date.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getId();
        return "TagLog(id=" + var10000 + ", name=" + this.getName() + ", attribute=" + this.getAttribute() + ", value=" + this.getValue() + ", time=" + this.getTime() + ", Date=" + this.getDate() + ")";
    }

    public TagLog(final Long id, final String name, final String attribute, final double value, final LocalTime time, final LocalDate Date) {
        this.id = id;
        this.name = name;
        this.attribute = attribute;
        this.value = value;
        this.time = time;
        this.Date = Date;
    }

    public TagLog() {
    }
}
