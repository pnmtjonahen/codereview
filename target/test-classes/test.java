package nl.rabobank.gict.mcv.payments.account.consumerprotection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.rabobank.gict.mcv.business.store.StateStore;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.assembler.DTOAssembler;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.business.PaymentsAccountConsumerProtection;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.business.dto.AccountConsumerProtectionDTO;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.business.dto.AccountDTO;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.business.dto.DirectDebitRestriction;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.business.dto.EDOArrangementsDTO;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.business.dto.StopPaymentDirectDebitDTO;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.business.dto.TransactionsDirectDebitDTO;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.helper.DeliverProductDirectDebitUtils;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.helper.PaymentsAccountConsumerProtectionUtils;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.impl.dto.RelationArrangementDTO;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.model.ConfiguringProductConsumerProtection;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.model.ConsumerProtectionAccount;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.model.ConsumerProtectionChannel;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.model.DirectDebitRestrictionStore;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.model.StateStoreModelConsumerProtection;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.ConvertBbanToIbanService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.DeliverEDOArrangementsEmailService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.DeliverProductDirectDebitService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.RetrieveArrangementDetailsPaymentAccountService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.RetrieveAscriptionDetailsService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.RetrieveEDOService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.RetrieveGenericProductAdjustablesService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.RetrieveProductDetailsDirectDebitService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.RetrieveProductDetailsEDOArrangementService;
import nl.rabobank.gict.mcv.payments.account.consumerprotection.service.RetrieveTransactionsDirectDebitService;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

/**
 * Payments-Account consumer protection business implementation
 *
 * Essentially a facade for other services to limit the size of this class.
 *
 */
@Service
public class PaymentsAccountConsumerProtectionImpl implements PaymentsAccountConsumerProtection {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsAccountConsumerProtectionImpl.class);

    private final StateStore<StateStoreModelConsumerProtection> stateStore;

    private final ConvertBbanToIbanService convertBbanToIbanService;

    private final RetrieveArrangementDetailsPaymentAccountService retrieveArrangementDetailsPaymentAccountService;

    private final RetrieveAscriptionDetailsService retriveAscriptionDetailsPaymentAccountService;

    private final RetrieveProductDetailsDirectDebitService retrieveProductDetailsDirectDebitService;

    private final RetrieveGenericProductAdjustablesService retrieveGenericProductAdjustableService;

    private final RetrieveTransactionsDirectDebitService retrieveTransactionsDirectDebitService;

    private final RetrieveProductDetailsEDOArrangementService retrieveProductDetailsEDOArrangementService;

    private final DeliverProductDirectDebitService deliverProductDirectDebitService;

    private final DeliverEDOArrangementsEmailService deliverEDOArrangementsEmailService;

    private final RetrieveEDOService retrieveEdoService;

    private final DTOAssembler dtoAssembler;

    /**
     * constant use to select the first account as a default account when application start on initialize Product
     */
    // public static final String DEFAULT_ACCOUNT = "FirstAccount";
    /**
     * @param stateStore - parameter used to save business objects per one session
     * @param convertBbanToIbanService Autowired service bean
     * @param retrieveArrangementDetailsPaymentAccountService Autowired service bean
     * @param retriveAscriptionDetailsPaymentAccountService Autowired service bean
     * @param retrieveProductDetailsDirectDebitService Autowired service bean
     * @param retrieveGenericProductAdjustableService Autowired service bean
     * @param retrieveTransactionsDirectDebitService Autowired service bean
     * @param retrieveProductDetailsEDOArrangementService Autowired service bean
     * @param deliverDeliverProductDirectDebitService DeliverProductDirectDebitService
     * @param deliverEDOArrangementsEmailService DeliverEDOArrangementsEmailService
     * @param retrieveEdoService RetrieveEDOService
     * @param dtoAssembler DTOAssembler
     */
    @Autowired
    public PaymentsAccountConsumerProtectionImpl(final StateStore<StateStoreModelConsumerProtection> stateStore,
            final ConvertBbanToIbanService convertBbanToIbanService,
            final RetrieveArrangementDetailsPaymentAccountService retrieveArrangementDetailsPaymentAccountService,
            final RetrieveAscriptionDetailsService retriveAscriptionDetailsPaymentAccountService,
            final RetrieveProductDetailsDirectDebitService retrieveProductDetailsDirectDebitService,
            final RetrieveGenericProductAdjustablesService retrieveGenericProductAdjustableService,
            final RetrieveTransactionsDirectDebitService retrieveTransactionsDirectDebitService,
            final RetrieveProductDetailsEDOArrangementService retrieveProductDetailsEDOArrangementService,
            final DeliverProductDirectDebitService deliverDeliverProductDirectDebitService,
            final DeliverEDOArrangementsEmailService deliverEDOArrangementsEmailService,
            final RetrieveEDOService retrieveEdoService,
            final DTOAssembler dtoAssembler) {

        this.stateStore = stateStore;
        this.convertBbanToIbanService = convertBbanToIbanService;
        this.retrieveArrangementDetailsPaymentAccountService = retrieveArrangementDetailsPaymentAccountService;
        this.retriveAscriptionDetailsPaymentAccountService = retriveAscriptionDetailsPaymentAccountService;
        this.retrieveProductDetailsDirectDebitService = retrieveProductDetailsDirectDebitService;
        this.retrieveGenericProductAdjustableService = retrieveGenericProductAdjustableService;
        this.retrieveTransactionsDirectDebitService = retrieveTransactionsDirectDebitService;
        this.retrieveProductDetailsEDOArrangementService = retrieveProductDetailsEDOArrangementService;
        this.deliverProductDirectDebitService = deliverDeliverProductDirectDebitService;
        this.deliverEDOArrangementsEmailService = deliverEDOArrangementsEmailService;
        this.retrieveEdoService = retrieveEdoService;
        this.dtoAssembler = dtoAssembler;
    }

    /**
     * {@inheritDoc} create the state store object and initialize it with default values
     *
     */
    @Override
    public void initializeProduct(final ConsumerProtectionChannel channel, final String relationId,
            String accountNumber, final String employeeId,
            final Collection<RelationArrangementDTO> selectableAccounts) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER
                    .debug(String
                            .format(
                                    "PaymentsAccountConsumerProtectionImpl, initializeProduct, channel: %s ,  relationid: %s , accountNumber: %s employeeId: %s ",
                                    channel, relationId, accountNumber, employeeId != null ? employeeId : " employeeId is null"));
        }
        // initialize with first account when no account is send as parameter to this application
        if (StringUtils.isEmpty(accountNumber)) {
            accountNumber = ConfiguringProductConsumerProtection.DEFAULT_ACCOUNT;
        }
        final ConfiguringProductConsumerProtection configurationCpas
                = new ConfiguringProductConsumerProtection(channel, relationId, accountNumber, employeeId);
        configurationCpas.setAccounts(selectableAccounts);
        configurationCpas.setSelectedAccountRepresentation(ConfiguringProductConsumerProtection.DEFAULT_ACCOUNT);

        final StateStoreModelConsumerProtection stateStoreConsumerProtection
                = new StateStoreModelConsumerProtection(configurationCpas, new DirectDebitRestrictionStore());

        stateStore.put(stateStoreConsumerProtection);
    }

    /**
     * method used internally to get the whole state store object
     */
    private StateStoreModelConsumerProtection retrieveStateStoreConsumerProtection() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PaymentsAccountConsumerProtectionImpl, retrieveStateStoreConsumerProtection");
        }
        return stateStore.get();
    }

    /**
     * return the state store related to configuration (saved session values or input parameters)
     */
    @Override
    public ConfiguringProductConsumerProtection retrieveConfiguringProductConsumerProtection() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PaymentsAccountConsumerProtectionImpl, retrieveConfiguringProductDebitCard");
        }

        final StateStoreModelConsumerProtection storeObj = stateStore.get();
        return storeObj.getConfigurationConsumerProtection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountConsumerProtectionDTO retrievePaymentAccounts(final ConfiguringProductConsumerProtection configurationConsumerProtection) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format(
                    "PaymentsAccountConsumerProtectionImpl, retrieveAccountsForRelationID, relationID: %s ,"
                    + "accountnumber: %s.", configurationConsumerProtection.getRelationId(),
                    configurationConsumerProtection.getArrangementNumber()));
        }

        final List<RelationArrangementDTO> relationArrangements
                = ImmutableList.<RelationArrangementDTO>copyOf(PaymentsAccountConsumerProtectionUtils
                        .retrieveAccountsPerRelation(configurationConsumerProtection));

        if (CollectionUtils.isEmpty(relationArrangements)) {
            return new AccountConsumerProtectionDTO(configurationConsumerProtection.getRelationId(),
                    new ArrayList<AccountDTO>());
        }

        List<AccountDTO> accounts
                = PaymentsAccountConsumerProtectionUtils.fillInfoOfSelectedAccounts(convertBbanToIbanService,
                        retrieveArrangementDetailsPaymentAccountService, retriveAscriptionDetailsPaymentAccountService,
                        retrieveGenericProductAdjustableService, relationArrangements, configurationConsumerProtection);
        // Sort by IBAN
        accounts = Ordering.from(String.CASE_INSENSITIVE_ORDER).onResultOf(AccountDTO.GET_IBAN).sortedCopy(accounts);

        return new AccountConsumerProtectionDTO(configurationConsumerProtection.getRelationId(), accounts);
    }

    /**
     * {@inheritDoc} TODO call this method when state store has to be invalidated
     */
    @Override
    public void clearStateStore() {
        stateStore.clear();
    }

    /**
     * {@inheritDoc} this will return all restrictions having the status: existing/ deleted/ new updated
     */
    @Override
    public List<DirectDebitRestriction> retrieveAllRestrictions(final String iban) {
        final List<DirectDebitRestriction> existingRestriction
                = retrieveProductDetailsDirectDebitService.retrieveDirectDebitDetails(iban);
        return retrieveStateStoreConsumerProtection().getRestrictionStoreMap().mergeStateStoredWithExistingRestriction(
                existingRestriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSelectedAccount(final ConsumerProtectionAccount selectedAccount) {
        final ConfiguringProductConsumerProtection config = retrieveConfiguringProductConsumerProtection();
        config.setSelectedAccount(selectedAccount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DirectDebitRestriction> retrieveDirectDebitDetails(final String iban) {
        return retrieveProductDetailsDirectDebitService.retrieveDirectDebitDetails(iban);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionsDirectDebitDTO> retrieveHistoricalDirectDebitDetails(final String iban) {
        return retrieveTransactionsDirectDebitService.retrieveTransactionDirectDebitDetails(iban);
    }

    /**
     * {@inheritDoc} this method will update the state store with one restriction for one selected account
     */
    @Override
    public void updateStateStoreWithRestriction(final DirectDebitRestriction newRestriction, final String iban) {
        retrieveStateStoreConsumerProtection().getRestrictionStoreMap().updateRestriction(newRestriction,
                findExistionRestriction(iban, newRestriction.getDirectDebitSettingId()));
    }

    public DirectDebitRestriction findExistionRestriction(final String iban, final String directDebitSettingId) {
        final List<DirectDebitRestriction> directDebitDetails = retrieveDirectDebitDetails(iban);
        DirectDebitRestriction existingRestriction = null;
        for (final DirectDebitRestriction directDebitRestriction : directDebitDetails) {
            if (ObjectUtils.equals(directDebitRestriction.getDirectDebitSettingId(), directDebitSettingId)) {
                existingRestriction = directDebitRestriction;
            }
        }
        return existingRestriction;
    }

    /**
     *
     * it returns the status of selected account - if it has to be signed
     */
    @Override
    public boolean isSigningRequestedPerAccount() {

        final StateStoreModelConsumerProtection config = retrieveStateStoreConsumerProtection();
        return config.getRestrictionStoreMap().isSigningRequestedPerAccount();
    }

    /**
     * {@inheritDoc} this flag will help to reduce calculation on the presentation layer
     */
    @Override
    public boolean isRequiredEmail() {
        return retrieveStateStoreConsumerProtection().isRequiredEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlertEmail() {
        final StateStoreModelConsumerProtection state = retrieveStateStoreConsumerProtection();
        final ConfiguringProductConsumerProtection config = state.getConfigurationConsumerProtection();
        String alertEmail = state.getRetrievedAlertEmail();
        // when it is not yet filled, is not retrieved
        if (StringUtils.isEmpty(alertEmail)) {
            if (config.getChannel().equals(ConsumerProtectionChannel.LOCALBANK)) {
                final String edoId
                        = retrieveEdoService.retrieveEdoArrangement(state.getConfigurationConsumerProtection());
                config.setEdoId(edoId);
            }
            if (StringUtils.isEmpty(config.getEdoId())) {
                state.setRetrievedAlertEmail(PaymentsAccountConsumerProtection.CUSTOMER_NO_EDO_EMAIL);
                return state.getRetrievedAlertEmail();
            }
            alertEmail = retrieveProductDetailsEDOArrangementService.retrieveAlertEmail(config.getEdoId());
            state.setRetrievedAlertEmail(alertEmail);
        }
        return alertEmail;
    }

    /**
     * the state store list is send to the crmi service to be updated
     *
     */
    @Override
    public void updateRestrictions(final String signingTransactionID) {

        final StateStoreModelConsumerProtection stateStoreConsumerProtection = retrieveStateStoreConsumerProtection();

        final String selectedIban = stateStoreConsumerProtection.getConfigurationConsumerProtection().getSelectedIBAN();

        final List<DirectDebitRestriction> existingRestrictions
                = retrieveProductDetailsDirectDebitService.retrieveDirectDebitDetails(selectedIban);

        final StopPaymentDirectDebitDTO stopPaymentDTO
                = DeliverProductDirectDebitUtils.createStopPaymentDirectDebitDTO(stateStoreConsumerProtection,
                        existingRestrictions, signingTransactionID);
        deliverProductDirectDebitService.updateRestrictions(stopPaymentDTO);
        stateStoreConsumerProtection.setDelivered(true);
        LOGGER.debug("Email adres is : ", stateStoreConsumerProtection.getAlertEmail());
        // updating email
        if (/*
                 * stateStoreConsumerProtection.isRequiredEmail() && *todo check this
                 */!StringUtils.isEmpty(stateStoreConsumerProtection.getAlertEmail())) {
            final EDOArrangementsDTO edoArrangementsDTO
                    = dtoAssembler.assembleEdoArrangementDto(stateStoreConsumerProtection);
            deliverEDOArrangementsEmailService.updateEmailID(edoArrangementsDTO);
            LOGGER.debug("Email adres ", stateStoreConsumerProtection.getAlertEmail(), " saved succeed!");
        }

    }

    @Override
    public boolean isDelivered() {
        final StateStoreModelConsumerProtection sscp = retrieveStateStoreConsumerProtection();
        return sscp != null && sscp.isDelivered();
    }

    /**
     * {@inheritDoc} clear the changed restrictions per selected account
     */
    @Override
    public void clearStateStoreRestrictionsPerAccount() {
        final StateStoreModelConsumerProtection stateStore = retrieveStateStoreConsumerProtection();
        stateStore.resetStateStoreRestrictions();
        stateStore.setDelivered(false);
    }

    /**
     * {@inheritDoc} clear the changed restrictions per selected account and the email updated
     */
    @Override
    public void clearStateStoreChangesPerAccount() {
        final StateStoreModelConsumerProtection stateStore = retrieveStateStoreConsumerProtection();
        stateStore.resetStateStoreRestrictions();
        stateStore.clearEmailStateStore();
        stateStore.setDelivered(false);
    }

    /**
     * {@inheritDoc} return all changed restrictions list
     */
    @Override
    public List<DirectDebitRestriction> retrieveChangedRestrictions() {

        return new ArrayList<DirectDebitRestriction>(retrieveStateStoreConsumerProtection().getRestrictionStoreMap()
                .getRestrictionsStore().values());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequiredEmail(final boolean isMandatoryEmail) {
        final StateStoreModelConsumerProtection state = retrieveStateStoreConsumerProtection();
        state.setRequiredEmail(isMandatoryEmail);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStateStoreAlertEmail() {
        final StateStoreModelConsumerProtection state = retrieveStateStoreConsumerProtection();
        return state.getAlertEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStateStoreAlertEmail(final String alertEmail) {
        final StateStoreModelConsumerProtection state = retrieveStateStoreConsumerProtection();
        state.setAlertEmail(alertEmail);

    }

    private static class ANestedClass {

        /**
         * {@inheritDoc} this will return the value of alert email from retrieveEdoService.retrieveEdoArrangement when
         * this does not exists returns the value PaymentsAccountConsumerProtection.CUSTOMER_NO_EDO_EMAIL
         */
        @Override
        public String getRetrievedAlertEmail() {
            final StateStoreModelConsumerProtection state = retrieveStateStoreConsumerProtection();
            return state.getRetrievedAlertEmail();
        }

    }
}

private class SecondClass {

    /**
     * {@inheritDoc} this will return the value of alert email from retrieveEdoService.retrieveEdoArrangement when this
     * does not exists returns the value PaymentsAccountConsumerProtection.CUSTOMER_NO_EDO_EMAIL
     */
    @Override
    public String getRetrievedAlertEmail() {
        final StateStoreModelConsumerProtection state = retrieveStateStoreConsumerProtection();
        return state.getRetrievedAlertEmail();
    }

}
