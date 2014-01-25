/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.gui;

import com.betfair.publicapi.types.exchange.v5.GetCompleteMarketPricesCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetCompleteMarketPricesCompressedResp;
import com.betfair.publicapi.types.exchange.v5.GetCompleteMarketPricesErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeCompressedErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeCompressedResp;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.betfair.BetfairAccessException;
import uk.co.etothepii.haddock.betfair.User;
import uk.co.etothepii.haddock.betfair.data.Exchange;
import uk.co.etothepii.haddock.markets.events.MarketChangedEvent;
import uk.co.etothepii.haddock.markets.events.MarketChangedListener;
import uk.co.etothepii.util.ThreadController;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BetfairReplacement extends JPanel {

    private static final Logger LOG = Logger.getLogger(BetfairReplacement.class);

    public static final long DEFAULT_REFRESH_SLEEP = 1000L;

    private final MarketList marketList;
    private final ExchangePricesPanel exchangePricesPanel;
    private final JScrollPane scroller;
    private int marketId;
    private final Object marketIdSync;
    private final ThreadController threadController;
    private User user;
    private long refreshSleep = DEFAULT_REFRESH_SLEEP;
    private final Object refreshSleepSync;
    private boolean includeSettledBets = false;
    private boolean includeBSPBets = true;
    private boolean netOfCommission = false;

    public BetfairReplacement(User user) {
        super(new GridBagLayout());
        this.user = user;
        marketIdSync = new Object();
        refreshSleepSync = new Object();
        marketList = new MarketList();
        exchangePricesPanel = new ExchangePricesPanel(null);
        scroller = new JScrollPane(exchangePricesPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(new Dimension(800, 600));
        marketList.addMarketChangedEvent(new MarketChangedListener() {
            public void marketChanged(MarketChangedEvent e) {
                int marketId = e.getMarket().getBetfairId();
                setMarketId(marketId);
            }
        });
        threadController = new ThreadController(new Runnable() {
            public void run() {
                int marketId = getMarketId();
                long start = System.currentTimeMillis();
                synchronized (marketIdSync) {
                    if (marketId != 0) {
                        if (exchangePricesPanel.getExchange(
                                ).marketId == marketId) {
                            exchangePricesPanel.getExchange(
                                    ).updatePrices(getPrices());
                        }
                        else {
                            exchangePricesPanel.setExchange(
                                    Exchange.process(getPrices()));
                        }
                        exchangePricesPanel.getExchange(
                                ).updateVolumes(getTradedVolume());
                        exchangePricesPanel.getExchange(
                                ).updateProfitAndLoss(getProfitAndLoss());
                    }
                }
                long sleep = start + 1050L - System.currentTimeMillis();
                if (sleep > 0) {
                    try {Thread.sleep(sleep);}
                    catch (InterruptedException ie) {}
                }
            }
        });
        threadController.start();
        add(marketList, new GridBagConstraints(0, 0, 1, 1, 0d, 1d,
                GridBagConstraints.NORTH, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(scroller, new GridBagConstraints(1, 0, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    private void setMarketId(int marketId) {
        synchronized (marketIdSync) {
            this.marketId = marketId;
            exchangePricesPanel.setExchange(Exchange.process(getPrices()));
        }
    }

    public int getMarketId() {
        synchronized (marketIdSync) {
            return marketId;
        }
    }

    private User getUser() {
        return user;
    }

    public long getRefreshSleep() {
        return refreshSleep;
    }

    public void setRefreshSleep(long refreshSleep) {
        this.refreshSleep = refreshSleep;
    }

    public boolean isIncludeBSPBets() {
        return includeBSPBets;
    }

    public boolean isIncludeSettledBets() {
        return includeSettledBets;
    }

    public boolean isNetOfCommission() {
        return netOfCommission;
    }

    private String getPrices() {
        GetCompleteMarketPricesCompressedReq getCompleteMarketPricesCompressed =
                new GetCompleteMarketPricesCompressedReq();
        getCompleteMarketPricesCompressed.setHeader(
                getUser().getExchangeHeader());
        getCompleteMarketPricesCompressed.setMarketId(marketId);
        try {
            GetCompleteMarketPricesCompressedResp
                    getCompleteMarketPricesCompressedResp =
                    getUser().getCompleteMarketPricesCompressed(
                    getCompleteMarketPricesCompressed);
            if (getCompleteMarketPricesCompressedResp.getErrorCode() ==
                    GetCompleteMarketPricesErrorEnum.OK) {
                return getCompleteMarketPricesCompressedResp.
                        getCompleteMarketPrices();
            }
            else {
                LOG.error("ErrorCode: " +
                        getCompleteMarketPricesCompressedResp.
                        getErrorCode());
                LOG.error("Minor Error Code: " +
                        getCompleteMarketPricesCompressedResp.
                        getMinorErrorCode());
            }
        }
        catch (BetfairAccessException bae) {
            LOG.error(bae.getMessage(), bae);
        }
        return null;
    }

    private String getTradedVolume() {
        GetMarketTradedVolumeCompressedReq getMarketTradedVolumeCompressedReq =
                new GetMarketTradedVolumeCompressedReq();
        getMarketTradedVolumeCompressedReq.setHeader(
                getUser().getExchangeHeader());
        getMarketTradedVolumeCompressedReq.setMarketId(marketId);
        try {
            GetMarketTradedVolumeCompressedResp
                    getMarketTradedVolumeCompressedResp =
                    getUser().getMarketTradedVolumeCompressed(
                    getMarketTradedVolumeCompressedReq);
            if (getMarketTradedVolumeCompressedResp.getErrorCode() ==
                    GetMarketTradedVolumeCompressedErrorEnum.OK) {
                return getMarketTradedVolumeCompressedResp.
                        getTradedVolume();
            }
            else {
                LOG.error("ErrorCode: " +
                        getMarketTradedVolumeCompressedResp.
                        getErrorCode());
                LOG.error("Minor Error Code: " +
                        getMarketTradedVolumeCompressedResp.
                        getMinorErrorCode());
            }
        }
        catch (BetfairAccessException bae) {
            LOG.error(bae.getMessage(), bae);
        }
        return null;
    }

    private GetMarketProfitAndLossResp getProfitAndLoss() {
        GetMarketProfitAndLossReq getMarketProfitAndLossReq =
                new GetMarketProfitAndLossReq();
        getMarketProfitAndLossReq.setHeader(
                getUser().getExchangeHeader());
        getMarketProfitAndLossReq.setMarketID(marketId);
        getMarketProfitAndLossReq.setIncludeBspBets(isIncludeBSPBets());
        getMarketProfitAndLossReq.setIncludeSettledBets(isIncludeSettledBets());
        getMarketProfitAndLossReq.setNetOfCommission(isNetOfCommission());
        try {
            GetMarketProfitAndLossResp
                    getMarketProfitAndLossResp =
                    getUser().getMarketProfitAndLoss(
                    getMarketProfitAndLossReq);
            if (getMarketProfitAndLossResp.getErrorCode() ==
                    GetMarketProfitAndLossErrorEnum.OK) {
                return getMarketProfitAndLossResp;
            }
            else {
                LOG.error("ErrorCode: " +
                        getMarketProfitAndLossResp.
                        getErrorCode());
                LOG.error("Minor Error Code: " +
                        getMarketProfitAndLossResp.
                        getMinorErrorCode());
            }
        }
        catch (BetfairAccessException bae) {
            LOG.error(bae.getMessage(), bae);
        }
        return null;
    }


}
