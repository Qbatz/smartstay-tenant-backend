package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.BillingRules;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.ennum.BillingModel;
import com.smartstay.tenant.repository.BillingRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HostelConfigService {

    @Autowired
    private BillingRuleRepository billingRuleRepository;

    Optional<BillingRules> getBillingRuleByIdAndHostelId(Integer id, String hostelId) {
        return billingRuleRepository.findBillingRuleByIdAndHostelId(id, hostelId);
    }

    Optional<BillingRules> getBillingRuleByHostelId(String hostelId) {
        return billingRuleRepository.findByHostel_hostelId(hostelId);
    }

    public void saveBillingRule(BillingRules billingRule) {
        billingRuleRepository.save(billingRule);
    }

    public void updateExistingBillRule(BillingRules latestBillingRules) {
        billingRuleRepository.save(latestBillingRules);
    }

    public BillingRules getLatestBillRuleByHostelIdAndStartDate(String hostelId, Date date) {
        return billingRuleRepository.findByHostelIdAndStartDate(hostelId, date);
    }

    public BillingRules getNewBillRuleByHostelIdAndStartDate(String hostelId, Date date) {
        return billingRuleRepository.findNewRuleByHostelIdAndDate(hostelId, date);
    }

    public BillingRules getCurrentMonthTemplate(String hostelId) {
        return billingRuleRepository.findLatestBillingRule(hostelId, new Date());
    }

    public BillingDates getBillingRuleOnDate(String hostelId, Date date) {
        return getBillingRuleByDateAndHostelId(hostelId, date);
    }

    public BillingDates getBillingRuleByDateAndHostelId(String hostelId, Date dateJoiningDate) {
        
        BillingRules billingRules = billingRuleRepository
                .findBillingRulesOnDateAndHostelId(hostelId, dateJoiningDate);
        BillingDates billDates = null;

        int billStartDate = 1;
        int billingRuleDueDate = 5;
        int billMonth;
        boolean hasGracePeriod = false;
        int gracePeriodDays = 0;
        String typeOfBilling = null;
        String billingModel = null;
        if (billingRules != null){
            hasGracePeriod = billingRules.isHasGracePeriod();
            gracePeriodDays = billingRules.getGracePeriodDays() != null ? billingRules.getGracePeriodDays() : 0;
            typeOfBilling = billingRules.getTypeOfBilling();
            billingModel = billingRules.getBillingModel();
            if (billingRules.isInitial()) {
                List<BillingRules> listBillingRulesExceptInitial = billingRuleRepository
                        .findAllBillingRulesByHostelIdExceptInitial(hostelId);
                if (!listBillingRulesExceptInitial.isEmpty()) {
                    billingRules = listBillingRulesExceptInitial.get(0);
                }
            }
            billStartDate = billingRules.getBillingStartDate();
            billingRuleDueDate = billingRules.getBillDueDays();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateJoiningDate);
        billMonth = calendar.get(Calendar.MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, billStartDate);

        if (Utils.compareWithTwoDates(dateJoiningDate, calendar.getTime()) < 0) {
            billMonth = billMonth - 1;
        }
        calendar.set(Calendar.MONTH, billMonth);

//        Calendar calendarDueDate = Calendar.getInstance();
//        calendarDueDate.set(Calendar.DAY_OF_MONTH, billingRuleDate);

        Date dueDate = Utils.addDaysToDate(calendar.getTime(), billingRuleDueDate);

        Date findEndDate = Utils.findLastDate(billStartDate, calendar.getTime());

        if (billingRules != null) {
            billDates = new BillingDates(calendar.getTime(), findEndDate, dueDate, billingRuleDueDate,
                    hasGracePeriod, gracePeriodDays, typeOfBilling, billingModel);
        }
        
        return billDates;
    }

    public List<BillingRules> getLatestBillingRulesByHostelIds(Set<String> hostelIds) {
        return billingRuleRepository
                .findAllLatestBillingRulesByHostelIds(hostelIds);
    }

    public BillingDates computeBillingDates(BillingRules billingRules, Date requestedDate) {

        int billStartDate = billingRules != null ? billingRules.getBillingStartDate() : 1;
        int billingRuleDueDate = billingRules != null ? billingRules.getBillDueDays() : 10;

        boolean hasGracePeriod = false;
        int gracePeriodDays = 0;
        String typeOfBilling = null;
        String billingModel = null;
        if (billingRules != null){
            hasGracePeriod = billingRules.isHasGracePeriod();
            gracePeriodDays = billingRules.getGracePeriodDays() != null ? billingRules.getGracePeriodDays() : 0;
            typeOfBilling = billingRules.getTypeOfBilling();
            billingModel = billingRules.getBillingModel();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(requestedDate);

        calendar.set(Calendar.DAY_OF_MONTH, billStartDate);

        if (Utils.compareWithTwoDates(requestedDate, calendar.getTime()) < 0) {
            calendar.add(Calendar.MONTH, -1);
        }

        Date dueDate = Utils.addDaysToDate(calendar.getTime(), billingRuleDueDate - 1);
        Date endDate = Utils.findLastDate(billStartDate, calendar.getTime());

        return new BillingDates(
                calendar.getTime(),
                endDate,
                dueDate,
                billingRuleDueDate,
                hasGracePeriod,
                gracePeriodDays,
                typeOfBilling,
                billingModel
        );
    }

    public BillingDates computeJoiningBasedBillingDates(BillingRules billingRules, Date joiningDate, Date requestedDate) {

        int billStartDate = 1;
        boolean hasGracePeriod = false;
        int billingRuleDueDate = 5;
        int gracePeriodDays = 0;
        String typeOfBilling = null;
        String billingModel = null;
        if (billingRules != null) {
            billStartDate = billingRules.getBillingStartDate();
            billingRuleDueDate = billingRules.getBillDueDays();
            hasGracePeriod = billingRules.isHasGracePeriod();
            gracePeriodDays = billingRules.getGracePeriodDays() != null ? billingRules.getGracePeriodDays() : 0;
            typeOfBilling = billingRules.getTypeOfBilling();
            billingModel = billingRules.getBillingModel();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(requestedDate);
        int day = Math.min(
                Utils.getDayOfMonth(joiningDate),
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        );
        calendar.set(Calendar.DAY_OF_MONTH, day);
        if (Utils.compareWithTwoDates(requestedDate, calendar.getTime()) < 0) {
            calendar.add(Calendar.MONTH, -1);
        }

        Date dueDate = Utils.addDaysToDate(calendar.getTime(), billingRuleDueDate - 1);
        Date endDate = Utils.findLastDate(Utils.getDayOfMonth(calendar.getTime()), calendar.getTime());

        return new BillingDates(calendar.getTime(),
                endDate,
                dueDate,
                billingRuleDueDate,
                hasGracePeriod,
                gracePeriodDays,
                typeOfBilling,
                billingModel);
    }

    public BillingDates computeBillingDatesWithBillingModel(BillingRules billingRules, Date requestedDate) {

        BillingDates billingDates = null;

        if (BillingModel.PREPAID.name().equals(billingRules.getBillingModel())) {
            billingDates = computeBillingDates(billingRules, requestedDate);
        }
        else if (BillingModel.POSTPAID.name().equals(billingRules.getBillingModel())) {
            Date previousMonthDate = Utils.getPreviousMonthDate(requestedDate);
            billingDates = computeBillingDates(billingRules, previousMonthDate);
        }

        return billingDates;
    }

    public BillingDates computeJoiningBillingDatesWithBillingModel(BillingRules billingRules, Date joiningDate, Date requestedDate) {

        BillingDates billingDates = null;

        if (BillingModel.PREPAID.name().equals(billingRules.getBillingModel())) {
            billingDates = computeJoiningBasedBillingDates(billingRules, joiningDate, requestedDate);
        }
        else if (BillingModel.POSTPAID.name().equals(billingRules.getBillingModel())) {
            Date previousMonthDate = Utils.getPreviousMonthDate(requestedDate);
            billingDates = computeJoiningBasedBillingDates(billingRules, joiningDate, previousMonthDate);
        }

        return billingDates;
    }
}
