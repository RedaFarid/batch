

package com.batch.GUI.Reporting.Reports;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class ReportModel {
    private long batchID;
    private String batchName;
    private LocalDate creationDate;
    private LocalTime creationTime;
    private LocalDateTime endTime;
    private String product;
    private String client;
    private String comment;
    private List<ReportTableDataModel> data = new LinkedList();

    public long getBatchID() {
        return this.batchID;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public LocalTime getCreationTime() {
        return this.creationTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public String getProduct() {
        return this.product;
    }

    public String getClient() {
        return this.client;
    }

    public String getComment() {
        return this.comment;
    }

    public List<ReportTableDataModel> getData() {
        return this.data;
    }

    public void setBatchID(final long batchID) {
        this.batchID = batchID;
    }

    public void setBatchName(final String batchName) {
        this.batchName = batchName;
    }

    public void setCreationDate(final LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreationTime(final LocalTime creationTime) {
        this.creationTime = creationTime;
    }

    public void setEndTime(final LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setProduct(final String product) {
        this.product = product;
    }

    public void setClient(final String client) {
        this.client = client;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public void setData(final List<ReportTableDataModel> data) {
        this.data = data;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ReportModel)) {
            return false;
        } else {
            ReportModel other = (ReportModel)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getBatchID() != other.getBatchID()) {
                return false;
            } else {
                Object this$batchName = this.getBatchName();
                Object other$batchName = other.getBatchName();
                if (this$batchName == null) {
                    if (other$batchName != null) {
                        return false;
                    }
                } else if (!this$batchName.equals(other$batchName)) {
                    return false;
                }

                Object this$creationDate = this.getCreationDate();
                Object other$creationDate = other.getCreationDate();
                if (this$creationDate == null) {
                    if (other$creationDate != null) {
                        return false;
                    }
                } else if (!this$creationDate.equals(other$creationDate)) {
                    return false;
                }

                Object this$creationTime = this.getCreationTime();
                Object other$creationTime = other.getCreationTime();
                if (this$creationTime == null) {
                    if (other$creationTime != null) {
                        return false;
                    }
                } else if (!this$creationTime.equals(other$creationTime)) {
                    return false;
                }

                Object this$endTime = this.getEndTime();
                Object other$endTime = other.getEndTime();
                if (this$endTime == null) {
                    if (other$endTime != null) {
                        return false;
                    }
                } else if (!this$endTime.equals(other$endTime)) {
                    return false;
                }

                Object this$product = this.getProduct();
                Object other$product = other.getProduct();
                if (this$product == null) {
                    if (other$product != null) {
                        return false;
                    }
                } else if (!this$product.equals(other$product)) {
                    return false;
                }

                Object this$client = this.getClient();
                Object other$client = other.getClient();
                if (this$client == null) {
                    if (other$client != null) {
                        return false;
                    }
                } else if (!this$client.equals(other$client)) {
                    return false;
                }

                Object this$comment = this.getComment();
                Object other$comment = other.getComment();
                if (this$comment == null) {
                    if (other$comment != null) {
                        return false;
                    }
                } else if (!this$comment.equals(other$comment)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ReportModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $batchID = this.getBatchID();
        result = result * 59 + (int)($batchID >>> 32 ^ $batchID);
        Object $batchName = this.getBatchName();
        result = result * 59 + ($batchName == null ? 43 : $batchName.hashCode());
        Object $creationDate = this.getCreationDate();
        result = result * 59 + ($creationDate == null ? 43 : $creationDate.hashCode());
        Object $creationTime = this.getCreationTime();
        result = result * 59 + ($creationTime == null ? 43 : $creationTime.hashCode());
        Object $endTime = this.getEndTime();
        result = result * 59 + ($endTime == null ? 43 : $endTime.hashCode());
        Object $product = this.getProduct();
        result = result * 59 + ($product == null ? 43 : $product.hashCode());
        Object $client = this.getClient();
        result = result * 59 + ($client == null ? 43 : $client.hashCode());
        Object $comment = this.getComment();
        result = result * 59 + ($comment == null ? 43 : $comment.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        long var10000 = this.getBatchID();
        return "ReportModel(batchID=" + var10000 + ", batchName=" + this.getBatchName() + ", creationDate=" + this.getCreationDate() + ", creationTime=" + this.getCreationTime() + ", endTime=" + this.getEndTime() + ", product=" + this.getProduct() + ", client=" + this.getClient() + ", comment=" + this.getComment() + ", data=" + this.getData() + ")";
    }

    public ReportModel(final long batchID, final String batchName, final LocalDate creationDate, final LocalTime creationTime, final LocalDateTime endTime, final String product, final String client, final String comment, final List<ReportTableDataModel> data) {
        this.batchID = batchID;
        this.batchName = batchName;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.endTime = endTime;
        this.product = product;
        this.client = client;
        this.comment = comment;
        this.data = data;
    }

    public ReportModel() {
    }
}
