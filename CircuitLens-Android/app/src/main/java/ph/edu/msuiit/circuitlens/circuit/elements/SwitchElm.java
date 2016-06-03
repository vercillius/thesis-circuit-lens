package ph.edu.msuiit.circuitlens.circuit.elements;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.RotateAnimation3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import java.util.Stack;
import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.circuit.CircuitElm;

import static ph.edu.msuiit.circuitlens.circuit.Graphics.drawThickLine;
import static ph.edu.msuiit.circuitlens.circuit.Graphics.interpPoint;

public class SwitchElm extends CircuitElm {

    protected boolean momentary;
    // position 0 == closed, position 1 == open
    protected int position, posCount;

    public SwitchElm(int xx, int yy) {
        super(xx, yy);
        momentary = false;
        position = 0;
        posCount = 2;
    }

    public SwitchElm(int xx, int yy, boolean mm) {
        super(xx, yy);
        position = (mm) ? 1 : 0;
        momentary = mm;
        posCount = 2;
    }

    public SwitchElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        String str = st.nextToken();

        if (str.compareTo("true") == 0) {
            // position = (this instanceof LogicInputElm) ? 0 : 1;
            position = 1;
        } else if (str.compareTo("false") == 0) {
            // position = (this instanceof LogicInputElm) ? 1 : 0;
            position = 0;
        } else {
            position = new Integer(str).intValue();
        }
        momentary = new Boolean(st.nextToken()).booleanValue();
        posCount = 2;
    }

    public boolean isMomentary() {
        return momentary;
    }

    public int getDumpType() {
        return 's';
    }

    public String dump() {
        return super.dump() + " " + position + " " + momentary;
    }

   Point ps, ps2;

    public void setPoints() {
        super.setPoints();
        calcLeads(32);
        ps = new Point();
        ps2 = new Point();
    }

    public void calculateCurrent() {
        if (position == 1) {
            current = 0;
        }
    }

    public void stamp() {
        if (position == 0) {
            sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
        }
    }

    public int getVoltageSourceCount() {
        return (position == 1) ? 0 : 1;
    }

    public void mouseUp() {
        if (momentary) {
            toggle();
        }
    }

    public void toggle() {
        position++;
        if (position >= posCount) {
            position = 0;
        }
    }

    public void setPosition(int i) {
        position = i;
        getCS().needAnalyze();
    }

//    public void getInfo(String arr[]) {
//        arr[0] = (momentary) ? "push switch (SPST)" : "switch (SPST)";
//        if (position == 1) {
//            arr[1] = "open";
//            arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
//        } else {
//            arr[1] = "closed";
//            arr[2] = "V = " + getVoltageText(volts[0]);
//            arr[3] = "I = " + getCurrentDText(getCurrent());
//        }
//    }

    public boolean getConnection(int n1, int n2) {
        return position == 0;
    }

    public boolean isWire() {
        return true;
    }

//    public EditInfo getEditInfo(int n) {
//        if (n == 0) {
//            EditInfo ei = new EditInfo("", 0, -1, -1);
//            ei.checkbox = new Checkbox("Momentary Switch", momentary);
//            return ei;
//        }
//        return null;
//    }

//    public void setEditValue(int n, EditInfo ei) {
//        if (n == 0) {
//            momentary = ei.checkbox.getState();
//        }
//    }

    public int getShortcut() {
        return 's';
    }

    Line3D thickLine;

    @Override
    public void updateObject3D() {
        if (circuitElm3D == null) {
            circuitElm3D = generateObject3D();
        }
        update2Leads();
        updateDotCount();
        if (position == 0) {
            doDots(circuitElm3D);
        }
    }

    public Object3D generateObject3D() {
        Object3D switch3d = new Object3D();
        int openhs = 16;
        int hs1 = (position == 1) ? 0 : 2;
        int hs2 = (position == 1) ? openhs : 2;
        setBbox(point1, point2, openhs);

        drawBoundingBox(switch3d);

        draw2Leads(switch3d);

//        if (!needsHighlight()) {
//            g.setColor(whiteColor);
//        }
        interpPoint(lead1, lead2, ps, 0, hs1);
        interpPoint(lead1, lead2, ps2, 1, hs2);
//
        Material material = new Material();
        material.setColor(Color.WHITE);
        Stack<Vector3> points = new Stack<>();
        points.add(new Vector3(ps.x,ps.y,0));
        points.add(new Vector3(ps2.x,ps2.y,0));
        thickLine = new Line3D(points,6);
        thickLine.setMaterial(material);

        switch3d.addChild(thickLine);

        return switch3d;
    }
}
