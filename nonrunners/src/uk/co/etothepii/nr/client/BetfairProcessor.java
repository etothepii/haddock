/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.nr.client;

import com.betfair.publicapi.types.exchange.v5.GetMarketPricesCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesCompressedResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeCompressedErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketTradedVolumeCompressedResp;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.betfair.BetfairAccessException;
import uk.co.etothepii.haddock.betfair.User;
import uk.co.etothepii.haddock.betfair.data.Exchange;
import uk.co.etothepii.haddock.betfair.data.RemovedRunner;
import uk.co.etothepii.haddock.betfair.data.Runner;
import uk.co.etothepii.haddock.betfair.data.RunnerPrice;
import uk.co.etothepii.haddock.db.MasterController;
import uk.co.etothepii.haddock.db.factories.AliasFactory;
import uk.co.etothepii.haddock.db.factories.NonrunnerFactory;
import uk.co.etothepii.haddock.db.tables.Nonrunner;
import uk.co.etothepii.haddock.db.tables.enumerations.NonrunnerResponse;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;
import uk.co.etothepii.util.ThreadController;
import uk.co.etothepii.util.ThreadControllerStopException;

/**
 *
 *
 * @author jrrpl
 */
public class BetfairProcessor {

    private static final Logger LOG = Logger.getLogger(BetfairProcessor.class);

    private final BlockingQueue<Nonrunner> queue;
    private final ArrayList<Nonrunner> processed;
    private final User user;
    private final ThreadController threadController;
    private final Map<Integer, Exchange> exchanges;

    public BetfairProcessor(User user) {
        this.user = user;
        exchanges = new TreeMap<Integer, Exchange>();
        queue = new LinkedBlockingQueue<Nonrunner>();
        processed = new ArrayList<Nonrunner>();
        threadController = new ThreadController(new Runnable() {
            public void run() {
                try {
                    if ((queue.isEmpty() && processed.isEmpty()) ||
                            !queue.isEmpty())
                        processInternal(queue.take());
                    else {
                        while (!processed.isEmpty())
                            queue.put(processed.remove(0));
                    }
                }
                catch (InterruptedException ie) {
                    throw new ThreadControllerStopException();
                }
                catch (BetfairAccessException bae) {
                    LOG.error(bae.getMessage(), bae);
                }
                catch (SQLException sqle) {
                    LOG.error(sqle.getMessage(), sqle);
                }
            }
        });
        threadController.start();
    }

    public void process(Nonrunner n) throws InterruptedException {
        LOG.debug("adding to queue");
        queue.put(n);
    }

    private void processInternal(Nonrunner n) throws BetfairAccessException,
            SQLException {
        LOG.debug("taken from queue " + n.toString());
        if (n.getRace().getMarket() == null) {
            n.setNonrunnerResponse(NonrunnerResponse.MARKET_NOT_YET_MATCHED);
            NonrunnerFactory.getFactory().save(n);
            LOG.debug("Set as Markwet not yet matched");
            return;
        }
        int bfId = n.getRace().getMarket().getBetfairId();
        GetMarketPricesCompressedReq req = new GetMarketPricesCompressedReq();
        req.setMarketId(bfId);
        req.setHeader(user.getExchangeHeader());
        GetMarketPricesCompressedResp resp = user.getMarketPricesCompressed(req);
        if (resp.getErrorCode() != GetMarketPricesErrorEnum.OK)
            throw new BetfairAccessException(resp.getErrorCode().toString() +
                    ": " + resp.getMinorErrorCode());
        LOG.debug("Got prices");
        Exchange e = exchanges.get(bfId);
        if (e == null) {
            e = Exchange.process(resp.getMarketPrices());
            exchanges.put(bfId, e);
        }
        else {
            e.updatePrices(resp.getMarketPrices());
        }
        LOG.debug("updated prices");
        GetMarketTradedVolumeCompressedReq tReq =
                new GetMarketTradedVolumeCompressedReq();
        tReq.setMarketId(bfId);
        tReq.setHeader(user.getExchangeHeader());
        GetMarketTradedVolumeCompressedResp tResp =
                user.getMarketTradedVolumeCompressed(tReq);
        if (tResp.getErrorCode() != GetMarketTradedVolumeCompressedErrorEnum.OK)
            throw new BetfairAccessException(tResp.getErrorCode().toString() +
                    ": " + tResp.getMinorErrorCode());
        LOG.debug("Got volumes");
        e.updateVolumes(tResp.getTradedVolume());
        DatabaseConduit conduit = MasterController.takeConduit();
        try {
            PreparedStatement ps = conduit.prepareStatement(
                    "INSERT INTO marketPrices (`market`, `selection`, `price`, "
                    + "`available`, `volume`, `time`) VALUES (?,?,?,?,?,?)");
            ps.setLong(1, n.getRace().getMarket().getId());
            ps.setTimestamp(6,
                    new Timestamp(System.currentTimeMillis()));
            for (Runner r : e.getRunners()) {
                ps.setLong(2, n.getAlias().getId());
                for (RunnerPrice p : r.getRunnerPrices().getPrices()) {
                    ps.setInt(3, p.index);
                    ps.setDouble(4, p.getAmount());
                    ps.setDouble(5, p.getMatched());
                    ps.addBatch();
                }
            }
            ps.execute();
        }
        finally {
            MasterController.releaseConduit(conduit);
        }
        LOG.debug("Sent price and volume data to database");
        if (n.getBFFound() == null) {
            String bfName = AliasFactory.getFactory(
                    ).getFromSelectionAndVendor(n.getAlias().getSelection(),
                    Vendor.BETFAIR_NAME).getAlias();
            for (RemovedRunner rr : e.getRemovedRunners()) {
                if (bfName == null)
                    bfName = n.getAlias().getSelection().getDisplayName();
                if (rr.selectionName.equals(bfName)) {
                    Date declared = rr.removedDate;
                    n.setBFDeclared(declared);
                    n.setBFFound(new Date());
                    LOG.debug("Found in betfair as non runner");
                    n.setNonrunnerResponse(NonrunnerResponse.FOUND);
                    NonrunnerFactory.getFactory().save(n);
                    processed.add(n);
                    return;
                }
            }
            if (n.getRace().getScheduled().getTime() > System.currentTimeMillis()) {
                LOG.debug("The race has run");
                n.setNonrunnerResponse(NonrunnerResponse.RACE_RAN);
                NonrunnerFactory.getFactory().save(n);
                return;
            }
            if (n.getNonrunnerResponse() == null) {
                boolean found = false;
                for (Runner r : e.getRunners()) {
                    if (r.getName().equals(bfName)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    LOG.debug("Found in betfair as live runner");
                    n.setNonrunnerResponse(NonrunnerResponse.LIVE_ON_BETFAIR);
                    NonrunnerFactory.getFactory().save(n);
                }
                else {
                    n.setNonrunnerResponse(
                            NonrunnerResponse.REMOVED_BEFORE_MARKET_OPENED);
                    NonrunnerFactory.getFactory().save(n);
                }
            }
            if (n.getNonrunnerResponse() == NonrunnerResponse.LIVE_ON_BETFAIR)
                processed.add(n);
        }
        else if(System.currentTimeMillis() -
                n.getBFFound().getTime() < 600000L) {
            processed.add(n);
        }
    }

}
