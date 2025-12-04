package com.smartstay.tenant.dao;


import jakarta.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintUpdates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long updateId;
    String status;
    String userType;
    String updatedBy;
    Date createdAt;

    @ManyToOne
    @JoinColumn(name = "complaint_id")
    ComplaintsV1 complaint;

}
