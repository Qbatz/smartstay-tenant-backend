package com.smartstay.tenant.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String configId;
    private String userId;
    private String fcmToken;
    private String mPin;
}
