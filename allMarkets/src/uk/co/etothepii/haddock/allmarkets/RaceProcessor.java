/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.allmarkets;

import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import uk.co.epii.betfairclient.BFExchange;
import uk.co.etothepii.haddock.db.factories.RaceFactory;
import uk.co.etothepii.haddock.db.factories.RacecourseFactory;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.Racecourse;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RaceProcessor {

    private static final Logger LOG = Logger.getLogger(RaceProcessor.class);

    private final BFExchange bfExchange;
    ArrayList<BFMarket> winMarkets;
    ArrayList<BFMarket> placeMarkets;

    public RaceProcessor(BFExchange bfExchange) {
        this.bfExchange = bfExchange;
        winMarkets = new ArrayList<BFMarket>();
        placeMarkets = new ArrayList<BFMarket>();
    }

    public void update(ArrayList<BFMarket> markets) throws SQLException {
        for (BFMarket toProcess : markets) {
            if (toProcess.getName().equalsIgnoreCase("TO BE PLACED"))
                placeMarkets.add(toProcess);
            else
                winMarkets.add(toProcess);
        }
        while (!winMarkets.isEmpty()) {
            BFMarket win = winMarkets.remove(0);
            BFMarket place = findPlace(win);
            LOG.debug("Creating winMarket for " + win.getBetfairId());
            makeRace(win, place);
        }
    }

    public void makeRace(BFMarket win, BFMarket place) throws SQLException{
        String abv = win.getParent().getName().split(" ")[0];
        Racecourse racecourse =
                RacecourseFactory.getFactory().getFromAbv(abv);
        Race race =
                RaceFactory.getFactory().
                getFromRacecourseAndScheduled(
                racecourse, win.getDate());
        int yards = 0;
        for (String s : win.getName().split(" ")) {
            if (s.matches("[1-5]m")) {
                try {
                    int miles = Integer.parseInt(
                            s.split("m")[0]);
                    yards += miles * 1760;
                }
                catch (NumberFormatException nfe) {}
            }
            else if (s.matches("[1-7]f")) {
                try {
                    int furlongs = Integer.parseInt(
                            s.split("f")[0]);
                    yards += furlongs * 220;
                }
                catch (NumberFormatException nfe) {}
            }
            else if (s.matches("[1-5]m[1-7]f")) {
                String[] parts = s.split("m");
                int miles = Integer.parseInt(parts[0]);
                int furlongs = Integer.parseInt(
                        parts[1].split("f")[0]);
                yards += miles * 1760;
                yards += furlongs * 220;
            }
        }
        if (race == null) {
            race = new Race(win, place, win.getDate(),
                    racecourse, null, yards == 0 ? null : yards,
                    null, null, null);
        }
        else {
            race.setMarket(win);
            race.setPlaceMarket(place);
            if (yards != 0)
                race.setDistance(yards);
        }
        RaceFactory.getFactory().save(race);
    }

    public BFMarket findPlace(BFMarket winMarket) {
        for (int i = 0; i < placeMarkets.size(); i++) {
            BFMarket potential = placeMarkets.get(i);
            if (potential.getDate().equals(winMarket.getDate()) &&
                    potential.getParent().equals(winMarket.getParent()))
                return placeMarkets.remove(i);
        }
        return null;
    }

}
