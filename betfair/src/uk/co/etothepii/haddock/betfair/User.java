/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair;

import com.betfair.publicapi.types.exchange.v5.BetStatusEnum;
import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetsOrderByEnum;
import com.betfair.publicapi.types.exchange.v5.CancelBetsReq;
import com.betfair.publicapi.types.exchange.v5.CancelBetsResp;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsReq;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsResp;
import com.betfair.publicapi.types.exchange.v5.GetAllMarketsReq;
import com.betfair.publicapi.types.exchange.v5.GetAllMarketsResp;
import com.betfair.publicapi.types.exchange.v5.GetCompleteMarketPricesCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetCompleteMarketPricesCompressedResp;
import com.betfair.publicapi.types.exchange.v5.GetMUBetsErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMUBetsReq;
import com.betfair.publicapi.types.exchange.v5.GetMUBetsResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesCompressedResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeCompressedResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeResp;
import com.betfair.publicapi.types.exchange.v5.PlaceBetsReq;
import com.betfair.publicapi.types.exchange.v5.PlaceBetsResp;
import com.betfair.publicapi.types.global.v3.APIRequestHeader;
import com.betfair.publicapi.types.global.v3.APIResponseHeader;
import com.betfair.publicapi.types.global.v3.LoginErrorEnum;
import com.betfair.publicapi.types.global.v3.LoginReq;
import com.betfair.publicapi.types.global.v3.LoginResp;
import com.betfair.publicapi.types.global.v3.LogoutReq;
import com.betfair.publicapi.types.global.v3.LogoutResp;
import com.betfair.publicapi.v3.bfglobalservice.BFGlobalService;
import com.betfair.publicapi.v3.bfglobalservice.BFGlobalService_Service;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService_Service;
import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossResp;
import com.betfair.publicapi.types.exchange.v5.MUBet;
import com.betfair.publicapi.types.exchange.v5.SortOrderEnum;
import com.betfair.publicapi.types.exchange.v5.UpdateBets;
import com.betfair.publicapi.types.exchange.v5.UpdateBetsReq;
import com.betfair.publicapi.types.exchange.v5.UpdateBetsResp;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.betfair.data.Funds;
import uk.co.etothepii.haddock.bettingconcepts.BetType;
import uk.co.etothepii.haddock.bettingconcepts.BetfairBet;
import uk.co.etothepii.haddock.bettingconcepts.BetfairBetFactory;
import uk.co.etothepii.haddock.bettingconcepts.BookiePrice;

/**
 *
 * @author James Robinson on behalf of Ridgefield-Pennine Ltd
 */
public class User {

    private static final BetfairAPITimeConstraints GET_MARKET_TIME;
    private static final BetfairAPITimeConstraints GET_P_AND_L_TIME;
    private static final BetfairAPITimeConstraints GET_MARKET_PRICES_COMPRESSED_TIME;
    private static final BetfairAPITimeConstraints GET_MARKET_PRICES_TIME;
    private static final BetfairAPITimeConstraints PLACE_BETS_TIME;
    private static final BetfairAPITimeConstraints UPDATE_BETS_TIME;
    private static final BetfairAPITimeConstraints GET_MU_BETS_TIME;
    private static final BetfairAPITimeConstraints CANCEL_BETS_TIME;
    private static final BetfairAPITimeConstraints GET_ALL_MARKETS_TIME;
    private static final BetfairAPITimeConstraints GET_MARKET_TRADED_VOLUME_COMPRESSED_TIME;
    private static final BetfairAPITimeConstraints GET_MARKET_TRADED_VOLUME_TIME;
    private static final BetfairAPITimeConstraints GET_COMPLETE_MARKET_PRICES_COMPRESSED;
    private static final BetfairAPITimeConstraints GET_ACCOUNT_FUNDS_TIME;

    static {
        GET_MARKET_TIME =
                new BetfairAPITimeConstraints("GET_MARKETS", 5, 60000);
        PLACE_BETS_TIME =
                new BetfairAPITimeConstraints("PLACE_BETS", 100, 60000);
        UPDATE_BETS_TIME = BetfairAPITimeConstraints.getUnrestriced("UPDATE_BETS");
        GET_MU_BETS_TIME =
                new BetfairAPITimeConstraints("GET_MU_BETS", 60, 60000);
        CANCEL_BETS_TIME =
                BetfairAPITimeConstraints.getUnrestriced("CANCEL_BETS");
        GET_ALL_MARKETS_TIME =
                new BetfairAPITimeConstraints("GET_ALL_MARKETS", 1, 60000);
        GET_ACCOUNT_FUNDS_TIME =
                new BetfairAPITimeConstraints(
                "GET_ACCOUNT_FUNDS_TIME", 12, 60000);
        GET_MARKET_PRICES_COMPRESSED_TIME =
                new BetfairAPITimeConstraints(
                "GET_MARKET_PRICES_COMPRESSED", 60, 60000);
        GET_P_AND_L_TIME =
                new BetfairAPITimeConstraints("GET_P_AND_L", 60, 60000);
        GET_MARKET_TRADED_VOLUME_COMPRESSED_TIME =
                new BetfairAPITimeConstraints(
                "GET_MARKET_TRADED_VOLUME_COMPRESSED", 60, 60000);
        GET_MARKET_TRADED_VOLUME_TIME =
                new BetfairAPITimeConstraints(
                "GET_MARKET_TRADED_VOLUME", 60, 60000);
        GET_MARKET_PRICES_TIME =
                new BetfairAPITimeConstraints("GET_MARKET_PRICES", 10, 60000);
        GET_COMPLETE_MARKET_PRICES_COMPRESSED =
                new BetfairAPITimeConstraints(
                "GET_COMPLETE_MARKET_PRICES_COMPRESSED", 60, 60000);
    }

    private static final Logger LOG = Logger.getLogger(User.class);
    private static final Object SYNC = new Object();

    /**
     * Stores the BFExchangeService which is used to pass exchange requests to
     * the Betfair Server.
     */
    private final BFExchangeService exchangePort;
    /**
     * Stores the BFGlobalService which is used to pass global requests to the
     * Betfair Server
     */
    private final BFGlobalService globalPort;
    /**
     * Stores the APIRequestHeader that represents the Exchange Header used for
     * identifying who we are to the server when making an exchange request.
     */
    private final com.betfair.publicapi.types.exchange.v5.APIRequestHeader
            exchangeHeader;
    /**
     * Stores the APIRequestHeader that represents the Global Header used for
     * identifying who we are to the server when making a global request.
     */
    private final APIRequestHeader globalHeader;

    private static URL GLOBAL_SERVICE_URL;
    private static URL EXCHANGE_SERVICE_URL;

    static {
        try {
            GLOBAL_SERVICE_URL =
                    new URL(
                    "https://api.betfair.com/global/v3/BFGlobalService.wsdl");
        }
        catch (MalformedURLException mue) {
            LOG.error(mue.getMessage(), mue);
            GLOBAL_SERVICE_URL = null;
        }
        try {
            EXCHANGE_SERVICE_URL = new URL(
                    "https://api.betfair.com/exchange/v5/"
                    + "BFExchangeService.wsdl");
        }
        catch (MalformedURLException mue) {
            LOG.error(mue.getMessage(), mue);
            EXCHANGE_SERVICE_URL = null;
        }
    }

    public User(String username, String password) {
        this(username, password.toCharArray());
    }

    public User(String username, char[] password) {
        globalPort = new BFGlobalService_Service(GLOBAL_SERVICE_URL
                ).getBFGlobalService();
        if (globalPort == null)
            throw new RuntimeException("Global port is null");
        LOG.debug("Got Global service");
        exchangePort = new BFExchangeService_Service(EXCHANGE_SERVICE_URL
                ).getBFExchangeService();
        if (exchangePort == null)
            throw new RuntimeException("Exchange port is null");
        LOG.debug("Got Exchange service");

        LoginReq request = new LoginReq();
        request.setProductId(82);
        request.setUsername(username);
        request.setPassword(String.valueOf(password));
        LOG.debug("Sending login request");
        LoginResp result = globalPort.login(request);
        LoginErrorEnum loginError = result.getErrorCode();

        if (loginError != LoginErrorEnum.OK){
            if (LOG.isDebugEnabled()){
                    LOG.debug("We have a login in error "+loginError);
            }
            if (loginError == LoginErrorEnum.INVALID_USERNAME_OR_PASSWORD) {
                throw new IllegalArgumentException(
                        "Invalid Username or Password");
            }
            else {
                throw new RuntimeException(loginError.name());
            }
        }
        else {
            APIResponseHeader respHead = result.getHeader();
            String sessionToken = respHead.getSessionToken();
            globalHeader = new APIRequestHeader();
            globalHeader.setSessionToken(sessionToken);
            exchangeHeader =
                    new com.betfair.publicapi.types.exchange.
                    v5.APIRequestHeader();
            exchangeHeader.setSessionToken(sessionToken);
        }
    }

    /**
     * Gets the Exchange APIRequestHeader
     *
     * @return the Exchange Header
     */
    public com.betfair.publicapi.types.exchange.v5.APIRequestHeader
            getExchangeHeader() {
        synchronized (SYNC) {
            return exchangeHeader;
        }
    }

    /**
     * Gets the global APIRequestHeader
     *
     * @return the Global Header
     */
    public APIRequestHeader getGlobalHeader() {
        synchronized (SYNC) {
            return globalHeader;
        }
    }

    /**
     * Gets the BFExchangeService
     *
     * @return the exchange port
     */
    private BFExchangeService getExchangePort() {
        synchronized (SYNC) {
            return exchangePort;
        }
    }

    /**
     * Gets the BFGlobalService
     *
     * @return the global port
     */
    private BFGlobalService getGlobalPort() {
        synchronized (SYNC) {
            return globalPort;
        }
    }

    /**
     * Sends the logout signals to the Betfair server
     *
     * @return the Betfair Serves response to the logout response.
     */
    public LogoutResp logout() {
        synchronized (SYNC) {
            LogoutReq loRequest = new LogoutReq();
            loRequest.setHeader(globalHeader);
            return globalPort.logout(loRequest);
        }
    }

    /**
     * Exits the program in an unceramonious fashion printing out an error
     * message first.
     *
     * @param error the error message to be printed.
     */
    public static void emergencyExit (String error) {
        LOG.error(error);
        System.exit(0);
    }

    public GetMarketTradedVolumeCompressedResp
            getMarketTradedVolumeCompressed(
            GetMarketTradedVolumeCompressedReq gmtvcr)
			throws BetfairAccessException{
		try{
			GET_MARKET_TRADED_VOLUME_COMPRESSED_TIME.awaitResourceTime();
			GET_MARKET_TRADED_VOLUME_COMPRESSED_TIME.makeCall();
			GetMarketTradedVolumeCompressedResp toRet =
                    getExchangePort().getMarketTradedVolumeCompressed(gmtvcr);
			return toRet;
		}catch(Throwable t){
			throw new BetfairAccessException(t.getMessage(), t);
		}
    }

    public GetCompleteMarketPricesCompressedResp
            getCompleteMarketPricesCompressed(
            GetCompleteMarketPricesCompressedReq gcmpcr)
			throws BetfairAccessException{
		try{
			GET_COMPLETE_MARKET_PRICES_COMPRESSED.awaitResourceTime();
			GET_COMPLETE_MARKET_PRICES_COMPRESSED.makeCall();
			GetCompleteMarketPricesCompressedResp toRet =
                    getExchangePort().getCompleteMarketPricesCompressed(gcmpcr);
			return toRet;
		}catch(Throwable t){
			throw new BetfairAccessException(t.getMessage(), t);
		}
    }

    public GetMarketTradedVolumeResp getMarketTradedVolume(
            GetMarketTradedVolumeReq gmtvcr)
			throws BetfairAccessException{
		try{
			GET_MARKET_TRADED_VOLUME_TIME.awaitResourceTime();
			GET_MARKET_TRADED_VOLUME_TIME.makeCall();
			GetMarketTradedVolumeResp toRet =
                    getExchangePort().getMarketTradedVolume(gmtvcr);
			return toRet;
		}catch(Throwable t){
			throw new BetfairAccessException(t.getMessage(), t);
		}
    }

    public PlaceBetsResp
            placeBets(PlaceBetsReq pbReq)
			throws BetfairAccessException{
		try{
			PLACE_BETS_TIME.awaitResourceTime();
			PLACE_BETS_TIME.makeCall();
			PlaceBetsResp toRet = getExchangePort().placeBets(pbReq);
			return toRet;
		}catch (Throwable t){
			throw new BetfairAccessException(t.getMessage(), t);
		}
    }

    public UpdateBetsResp updateBets(UpdateBetsReq updateBetsReq)
            throws BetfairAccessException {
        try {
            UPDATE_BETS_TIME.awaitResourceTime();
            UPDATE_BETS_TIME.makeCall();
            UpdateBetsResp toRet = getExchangePort().updateBets(updateBetsReq);
            return toRet;
        }
        catch (Throwable t) {
            throw new BetfairAccessException(t.getMessage(), t);
        }
    }

    public GetMUBetsResp
            getMUBets(GetMUBetsReq mubReq)
	throws BetfairAccessException{
		try{
			GET_MU_BETS_TIME.awaitResourceTime();
			GET_MU_BETS_TIME.makeCall();
			GetMUBetsResp toRet = getExchangePort().getMUBets(mubReq);
			return toRet;
		}catch (Throwable t){
			throw new BetfairAccessException(t.getMessage(), t);
		}
    }

    public CancelBetsResp
            cancelBets(CancelBetsReq cbReq)
			throws BetfairAccessException{
		try{
			CANCEL_BETS_TIME.awaitResourceTime();
			CANCEL_BETS_TIME.makeCall();
			CancelBetsResp toRet = getExchangePort().cancelBets(cbReq);
			return toRet;
		}catch (Throwable t){
			throw new BetfairAccessException(t.getMessage(), t);
		}
    }

    public GetMarketPricesCompressedResp
            getMarketPricesCompressed(GetMarketPricesCompressedReq gmpcReq)
	throws BetfairAccessException{
		try{
			GET_MARKET_PRICES_COMPRESSED_TIME.awaitResourceTime();
			GET_MARKET_PRICES_COMPRESSED_TIME.makeCall();
			GetMarketPricesCompressedResp toRet =
					getExchangePort().getMarketPricesCompressed(gmpcReq);
			return toRet;
		}catch (Throwable t){
			throw new BetfairAccessException(t.getMessage(), t);
		}
    }

    public GetMarketProfitAndLossResp
            getMarketProfitAndLoss(GetMarketProfitAndLossReq gmpalReq)
	throws BetfairAccessException{
		try{
			GET_P_AND_L_TIME.awaitResourceTime();
			GET_P_AND_L_TIME.makeCall();
			GetMarketProfitAndLossResp toRet =
					getExchangePort().getMarketProfitAndLoss(gmpalReq);
			return toRet;
		}catch (Throwable t){
			throw new BetfairAccessException(t.getMessage(), t);
		}
    }

    public GetMarketPricesResp
            getMarketPrices(GetMarketPricesReq gmpReq) {
        GET_MARKET_PRICES_TIME.awaitResourceTime();
        GET_MARKET_PRICES_TIME.makeCall();
        GetMarketPricesResp toRet =
                getExchangePort().getMarketPrices(gmpReq);
        return toRet;
    }

    public GetAllMarketsResp getAllMarkets(GetAllMarketsReq gamReq)
            throws BetfairAccessException {
        try {
            GET_ALL_MARKETS_TIME.awaitResourceTime();
            GetAllMarketsResp toRet = getExchangePort().getAllMarkets(gamReq);
            GET_ALL_MARKETS_TIME.makeCall();
            return toRet;
        }
        catch (Throwable t) {
            throw new BetfairAccessException(t.getMessage(), t);
        }
    }

    public Funds getFunds() throws BetfairAccessException {
        GetAccountFundsReq req = new GetAccountFundsReq();
        req.setHeader(exchangeHeader);
        try {
            GET_ACCOUNT_FUNDS_TIME.awaitResourceTime();
            GetAccountFundsResp resp = getExchangePort().getAccountFunds(req);
            GET_ACCOUNT_FUNDS_TIME.makeCall();
            if (resp.getErrorCode() == GetAccountFundsErrorEnum.OK) {
                return new Funds(resp.getAvailBalance(), resp.getBalance(),
                        resp.getCommissionRetain(), resp.getCreditLimit(),
                        resp.getCurrentBetfairPoints(), resp.getExpoLimit(),
                        resp.getExposure(), resp.getHolidaysAvailable(),
                        resp.getNextDiscount(), resp.getWithdrawBalance());
            }
            else {
                throw new BetfairAccessException(resp.getErrorCode().value());
            }
        }
        catch (Throwable t) {
            if (t instanceof BetfairAccessException)
                throw (BetfairAccessException)t;
            else
                throw new BetfairAccessException(t.getMessage(), t);
        }
    }

    public ArrayList<BetfairBet> getAllMatchedBetfairBets(Date matchedSince,
            Integer marketId) {
        ArrayList<BetfairBet> toRet = new ArrayList<BetfairBet>();
        BetfairBetFactory factory = new BetfairBetFactory(0, 
                null, null, null, null, null, null, null, null);
        GetMUBetsReq req = new GetMUBetsReq();
        req.setHeader(exchangeHeader);
        req.setBetStatus(BetStatusEnum.M);
        if (matchedSince != null) {
            try {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(matchedSince);
                req.setMatchedSince(DatatypeFactory.newInstance(
                        ).newXMLGregorianCalendar(c));
            }
            catch (DatatypeConfigurationException dce) {
                throw new IllegalArgumentException(dce.getMessage(), dce);
            }
        }
        if (marketId != null) {
            req.setMarketId(marketId);
        }
        req.setOrderBy(BetsOrderByEnum.MATCHED_DATE);
        req.setRecordCount(200);
        req.setSortOrder(SortOrderEnum.ASC);
        boolean carryOn = true;
        int startRecord = 0;
        while (carryOn) {
            req.setStartRecord(startRecord);
            GET_MU_BETS_TIME.awaitResourceTime();
            GetMUBetsResp resp = exchangePort.getMUBets(req);
            GET_MU_BETS_TIME.makeCall();
            if (resp.getTotalRecordCount() > startRecord + 200) {
                startRecord += 200;
            }
            else {
                carryOn = false;
            }
            if (resp.getErrorCode() == GetMUBetsErrorEnum.OK) {
               if (resp != null && resp.getBets() != null &&
                        resp.getBets().getMUBet() != null) {
                    for (MUBet b : resp.getBets().getMUBet()) {
                        factory.setAmount(b.getSize());
                        factory.setBetId(b.getBetId());
                        factory.setBetType(b.getBetType() == BetTypeEnum.B ?
                            BetType.BACK : BetType.LAY);
                        factory.setBfMarketId(b.getMarketId());
                        factory.setBfSelectionId(b.getSelectionId());
                        factory.setMatched(b.getMatchedDate(
                                ).toGregorianCalendar().getTime());
                        factory.setPlaced(b.getPlacedDate(
                                ).toGregorianCalendar().getTime());
                        factory.setPrice(BookiePrice.getPrice(b.getPrice()));
                        factory.setTransactionId(b.getTransactionId());
                        toRet.add(factory.getBet());
                    }
                }
            }
            else {
                LOG.error(resp.getErrorCode().toString());
            }
        }
        return toRet;
    }

}
