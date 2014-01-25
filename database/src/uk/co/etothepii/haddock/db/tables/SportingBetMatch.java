/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;
import uk.co.etothepii.haddock.db.tables.enumerations.TeamOrder;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class SportingBetMatch extends HaddockAccessObject {

    public static final Field TEAM_A = new Field("teamA", Alias.class,
            SportingBetMatch.class);
    public static final Field TEAM_B = new Field("teamB", Alias.class,
            SportingBetMatch.class);
    public static final Field START_TIME = new Field("startTime", Date.class,
            SportingBetMatch.class);
    public static final Field A_WIN = new Field("aWin", Double.class,
            SportingBetMatch.class);
    public static final Field DRAW = new Field("draw", Double.class,
            SportingBetMatch.class);
    public static final Field B_WIN = new Field("bWin", Double.class,
            SportingBetMatch.class);
    public static final Field MARKET = new Field("market", BFMarket.class,
            SportingBetMatch.class);
    public static final Field TEAM_ORDER = new Field("teamOrder",
            TeamOrder.class, SportingBetMatch.class);

    private static final Logger LOG = Logger.getLogger(SportingBetMatch.class);

    private Alias teamA;
    private Alias teamB;
    private Date startTime;
    private double aWin;
    private double draw;
    private double bWin;
    private BFMarket market;
    private TeamOrder teamOrder;
    private boolean matchChanged;
    private boolean teamOrderChanged;

    public SportingBetMatch(int id, Alias teamA, Alias teamB, Date startTime,
            double aWin, double draw, double bWin, BFMarket market, TeamOrder teamOrder) {
        super(id);
        this.teamA = teamA;
        this.teamB = teamB;
        this.startTime = startTime;
        this.aWin = aWin;
        this.draw = draw;
        this.bWin = bWin;
        this.market = market;
        this.teamOrder = teamOrder;
        matchChanged = false;
        teamOrderChanged = false;
    }

    public SportingBetMatch(Alias teamA, Alias teamB, Date startTime, 
            double aWin, double draw, double bWin, BFMarket market,
            TeamOrder teamOrder) {
        this(0,teamA, teamB, startTime, aWin, draw, bWin, market, teamOrder);
    }

    public Alias getTeamA() {
        return teamA;
    }

    public Alias getTeamB() {
        return teamB;
    }

    public void setTeamA(Alias teamA) {
        if (different(teamA, this.teamA)) {
            Alias old = this.teamA;
            setChanged(true);
            fireFieldChangedListener(new FieldChangedEvent(this, TEAM_A, old,
                    teamA));
            this.teamA = teamA;
        }
    }

    public void setTeamB(Alias teamB) {
        if (different(teamB, this.teamB)) {
            Alias old = this.teamB;
            setChanged(true);
            fireFieldChangedListener(new FieldChangedEvent(this, TEAM_B, old,
                    teamB));
            this.teamB = teamB;
        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        if (different(startTime, this.startTime)) {
            Date old = this.startTime;
            setChanged(true);
            fireFieldChangedListener(new FieldChangedEvent(this, START_TIME,
                    old, startTime));
            this.startTime = startTime;
        }
    }

    public double getaWin() {
        return aWin;
    }

    public void setaWin(double aWin) {
        if (different(aWin, this.aWin)) {
            double old = this.aWin;
            setChanged(true);
            fireFieldChangedListener(new FieldChangedEvent(this, A_WIN, old,
                    aWin));
            this.aWin = aWin;
        }
    }

    public double getDraw() {
        return draw;
    }

    public void setDraw(double draw) {
        if (different(draw, this.draw)) {
            double old = this.draw;
            setChanged(true);
            fireFieldChangedListener(new FieldChangedEvent(this, DRAW, old,
                    draw));
            this.draw = draw;
        }
    }

    public double getbWin() {
        return bWin;
    }

    public void setbWin(double bWin) {
        if (different(bWin, this.bWin)) {
            double old = this.bWin;
            setChanged(true);
            fireFieldChangedListener(new FieldChangedEvent(this, B_WIN, old,
                    bWin));
            this.bWin = bWin;
        }
    }

    public BFMarket getMarket() {
        return market;
    }

    public void setMarket(BFMarket market) {
        if (different(market, this.market)) {
            BFMarket old = this.market;
            setChanged(true);
            setMatchChanged(true);
            LOG.debug("matchChanged: " +
                    (market == null ? "null" : market.toString()) + ", " +
                    (this.market == null ? "null" : this.market.toString()));
            this.market = market;
            fireFieldChangedListener(new FieldChangedEvent(this, MARKET, old,
                    market));
        }
    }

    public TeamOrder getTeamOrder() {
        return teamOrder;
    }

    public void setTeamOrder(TeamOrder teamOrder) {
        if (different(teamOrder, this.teamOrder)) {
            TeamOrder old = this.teamOrder;
            setChanged(true);
            setTeamOrderChanged(true);
            this.teamOrder = teamOrder;
            fireFieldChangedListener(new FieldChangedEvent(this, TEAM_ORDER,
                    old, teamOrder));
        }
    }

    @Override
    protected void setChanged(boolean changed) {
        super.setChanged(changed);
        if (!changed) {
            setMatchChanged(changed);
            setTeamOrderChanged(changed);
        }
    }

    private void setMatchChanged(boolean matchChanged) {
        this.matchChanged = matchChanged;
    }

    public boolean hasMatchChanged() {
        return matchChanged;
    }

    public void setTeamOrderChanged(boolean teamOrderChanged) {
        this.teamOrderChanged = teamOrderChanged;
    }

    public boolean hasTeamOrderChanged() {
        return teamOrderChanged;
    }

}
