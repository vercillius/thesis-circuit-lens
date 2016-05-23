package ph.edu.msuiit.circuitlens.circuit.elements;

import android.graphics.Point;

import org.rajawali3d.Object3D;
import org.rajawali3d.math.vector.Vector3;

import java.util.Stack;
import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.circuit.CircuitElm;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;

public class DiodeElm extends CircuitElm {

    Diode diode;
    static final int FLAG_FWDROP = 1;
    final double defaultdrop = .805904783;
    double fwdrop, zvoltage;

    Stack<Vector3> points;


    public DiodeElm(int xx, int yy) {
        super(xx, yy);
        fwdrop = defaultdrop;
        zvoltage = 0;
    }

    public DiodeElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        fwdrop = defaultdrop;
        zvoltage = 0;
        if ((f & FLAG_FWDROP) > 0) {
            try {
                fwdrop = new Double(st.nextToken()).doubleValue();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void setSim(CircuitSimulator sim) {
        super.setSim(sim); //To change body of generated methods, choose Tools | Templates.
        diode = new Diode(sim);
        setup();
    }

    public boolean nonLinear() {
        return true;
    }

    void setup() {
        diode.setup(fwdrop, zvoltage);
    }

    public int getDumpType() {
        return 'd';
    }

    public String dump() {
        flags |= FLAG_FWDROP;
        return super.dump() + " " + fwdrop;
    }

    final int hs = 8;
//    Polygon poly;
    Point cathode[];

    public void setPoints() {
        super.setPoints();
        calcLeads(16);
        cathode = newPointArray(2);
        Point pa[] = newPointArray(2);
        interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
        interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);
        //poly = createPolygon(pa[0], pa[1], lead2);
        points = new Stack<>();
        points.add(new Vector3(pa[0].x,pa[0].y,0));
        points.add(new Vector3(pa[1].x,pa[1].y,0));
        points.add(new Vector3(lead2.x,lead2.y,0));
    }

    /*
    public void draw(Graphics g) {
        drawDiode(g);
        doDots(g);
        drawPosts(g);
    }
    */

    public void reset() {
        diode.reset();
        volts[0] = volts[1] = curcount = 0;
    }

    /*
    void drawDiode(Graphics g) {
        setBbox(point1, point2, hs);

        double v1 = volts[0];
        double v2 = volts[1];

        draw2Leads(g);

        // draw arrow thingy
        setPowerColor(g, true);
        setVoltageColor(g, v1);
        g.fillPolygon(poly);

        // draw thing arrow is pointing to
        setVoltageColor(g, v2);
        drawThickLine(g, cathode[0], cathode[1]);
    }
    */

    public void stamp() {
        diode.stamp(nodes[0], nodes[1]);
    }

    public void doStep() {
        diode.doStep(volts[0] - volts[1]);
    }

    public void calculateCurrent() {
        current = diode.calculateCurrent(volts[0] - volts[1]);
    }

    public int getShortcut() {
        return 'd';
    }

    @Override
    public Object3D generateObject3D() {
        Object3D diode3d = new Object3D();
        drawDiode(diode3d);
//        doDots(g);
        drawPosts(diode3d);
        return diode3d;
    }

    Object3D drawDiode(Object3D diode3d) {
        setBbox(point1, point2, hs);

        double v1 = volts[0];
        double v2 = volts[1];

        draw2Leads(diode3d);

        // draw arrow thingy
//        setPowerColor(true);
//        setVoltageColor(v1);
//
//        drawTriangle(diode3d,points);
//
//        // draw thing arrow is pointing to
//        setVoltageColor(v2);
        drawThickLine(diode3d, cathode[0], cathode[1]);
        return diode3d;
    }
}
