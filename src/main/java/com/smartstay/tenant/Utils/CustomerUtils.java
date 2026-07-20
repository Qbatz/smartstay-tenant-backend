package com.smartstay.tenant.Utils;

import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.KycDetails;

public class CustomerUtils {

    public static String getProfilePic(Customers customers) {

        if (customers != null) {

            if (customers.getProfilePic() != null) {
                return customers.getProfilePic();
            }

            KycDetails kycDetails = customers.getKycDetails();
            if (kycDetails != null) {
                if (kycDetails.getCurrentStatus() != null &&
                        kycDetails.getCurrentStatus().equalsIgnoreCase("VERIFIED")) {
                    if (kycDetails.getIdPic() != null) {
                        return kycDetails.getIdPic();
                    }
                }
            }
        }

        return null;
    }
}
