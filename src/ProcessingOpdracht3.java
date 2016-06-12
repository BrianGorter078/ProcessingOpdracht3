import processing.core.PApplet;
import processing.core.PVector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class ProcessingOpdracht3 extends PApplet {


    private int frames = 4;
    private int drawChecksX = 1000;
    private int drawChecksY = 600;

    private Button[] buttons;
    private float scope = 500;
    private String pathToCsv = "1000.csv";

    private float MIDDLE_X = 92796;
    private float MIDDLE_Y = 436960;
    private float MIN_X;
    private float MAX_X;
    private float MIN_Y;
    private float MAX_Y;

    private DecimalFormat df = new DecimalFormat();
    private HashSet<PVector> positionData = new HashSet<>();

    private float waterLevel = -2f;
    private float waterRaise = 0.1f;

    private boolean setUpRun = true;
    private boolean pause = false;
    private boolean isPressed;


    public static void main(String args[]) {
        PApplet.main("ProcessingOpdracht3");
    }

    @Override
    public void settings() {
        size(1200, 675);
    }

    @Override
    public void setup() {
        clear();
        background(0, 0, 0);
        float spaceBetween = 40;
        float leftMargin = 80;
        float secondRow = 200;
        float beginDrawing = 450;

        MIN_X = MIDDLE_X - scope;
        MAX_X = MIDDLE_X + scope;
        MIN_Y = MIDDLE_Y - scope;
        MAX_Y = MIDDLE_Y + scope;

        buttons = new Button[]{
                new Button("Pause", leftMargin, beginDrawing, 75f, 30f, "pause"),
                new Button("Play", leftMargin, beginDrawing + spaceBetween, 75f, 30f, "play"),
                new Button("Reset", leftMargin, beginDrawing + spaceBetween * 2, 75f, 30f, "reset"),
                new Button("SpeedUp", leftMargin, beginDrawing + spaceBetween * 3, 75f, 30f, "speedDown"),
                new Button("SpeedDown", secondRow, beginDrawing, 75f, 30f, "speedUp"),
                new Button("Screenshot", secondRow, beginDrawing + spaceBetween, 75f, 30f, "screenShot"),
                new Button("Expand", secondRow, beginDrawing + spaceBetween * 2, 75f, 30f, "expand"),
                new Button("Diminish", secondRow, beginDrawing + spaceBetween * 3, 75f, 30f, "diminish"),
        };

        df.setMaximumFractionDigits(2);
        CsvReader reader = new CsvReader();
        ArrayList<PVector> data = reader.readCsv(scope, pathToCsv);
        positionData.addAll(data);
    }

    public void execute(String item) {
        switch (item) {
            case "pause":
                pauseWater();
                break;
            case "play":
                startWater();
                break;
            case "reset":
                reset();
                break;
            case "speedDown":
                speedDown();
                break;
            case "speedUp":
                speedUp();
                break;
            case "screenShot":
                screenshot();
                break;
            case "expand":
                updateMap(1000);
                break;
            case "diminish":
                updateMap(500);
                break;
        }
    }

    @Override
    public void draw() {
        drawMap();

        if (!pause && frameCount % frames == 0) {
            waterLevel += waterRaise;
        }

        drawChecks();

        if (!pause) {
            fill(0, 200, 0);
            ellipse(drawChecksX + 12, drawChecksY + 21, 18, 18);
        } else {
            fill(200, 0, 0);
            ellipse(drawChecksX + 12, drawChecksY + 21, 18, 18);
        }


        for (Button btn : buttons) {
            btn.Draw();
            if (mousePressed && !isPressed) {
                if (btn.MouseIsOver()) {
                    execute(btn.callFunc);
                    isPressed = true;
                }
            }
        }
    }

    public void mouseReleased() {
        isPressed = false;
    }

    private void drawMap() {
        ArrayList<PVector> toRemove = new ArrayList<>();

        if (setUpRun) {
            for (PVector pos : positionData) {

                PVector position = getPositions(pos);
                if (position.z > 4.0f && position.z < 21.5f) {      //Color of ground and roads
                    stroke(color(145, 141, 133));
                    fill(color(180, 176, 168));
                } else if (position.z > 21.5f && position.z < 39f) {      //Color of ground and roads
                    stroke(color(196, 193, 186));
                    fill(color(211, 208, 201));
                } else {
                    stroke(color(247, 245, 239)); ///Color of top of building
                    fill(color(242, 240, 234));
                }

                rect(position.x, position.y, 2f, 2f);            //create rect at points of mapped xy
            }

            setUpRun = false;
        } else {
            for (PVector pos : positionData) {
                PVector position = getPositions(pos);

                if (waterLevel > position.z) {                     //Color of water
                    stroke(color(0, 153, 153));
                    fill(color(72,118,255));

                    ellipse(position.x, position.y, 2f, 2f);            //create ellipse at points of mapped xy
                    toRemove.add(pos);
                }
            }

            positionData.removeAll(toRemove);
        }
    }

    private void drawChecks() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        int xMap = drawChecksX;
        int yMap = drawChecksY;

        fill(255,255,255);
        rect(430,0,270,30);
        stroke(color(0,0,0));
        fill(color(255,0,0));
        textSize(20);
        text("Waterstijging Rotterdam",width/2 - 150, 20);
        textSize(12);

        fill(255, 255, 255);
        rect(xMap, yMap, 170, 50);

        fill(0, 0, 0);
        text("Water Level (m): " + df.format(waterLevel), xMap + 32, yMap + 16);

        if (scope == 500)
            text("Groote: 500x500", xMap + 32, yMap + 45);
        else
            text("Groote: 1000x1000", xMap + 32, yMap + 45);

        switch (frames) {
            case 10:
            case 9:
                text("Snelheid: Langzaam", xMap + 32, yMap + 30);
                break;
            case 8:
            case 7:
            case 6:
                text("Speed: Medium", xMap + 32, yMap + 30);
                break;
            case 5:
            case 4:
            case 3:
                text("Speed: Fast", xMap + 32, yMap + 30);
                break;
            case 2:
            case 1:
                text("Speed: Very Fast", xMap + 32, yMap + 30);
                break;
            default:
                text("Speed: Slow", xMap + 32, yMap + 30);
        }
    }

    private PVector getPositions(PVector p) {
        float x = map(p.x, MIN_X, MAX_X, 0, width);
        float y = map(p.y, MIN_Y, MAX_Y, height, 0);
        float z = map(p.z, CsvReader.MIN_Z, CsvReader.MAX_Z, 0, 216);

        return new PVector(x, y, z);
    }

    private void pauseWater() {
        pause = true;
    }

    private void startWater() {
        pause = false;
    }

    private void reset() {
        setUpRun = true;
        waterLevel = -2f;
        setup();
    }

    private void updateMap(int amount) {
        if (scope != amount) {
            scope = amount;
            waterLevel = -2f;
            pathToCsv = amount + ".csv";
            setUpRun = true;
            reset();
            redraw();
        }
    }

    private void speedDown() {
        if (frames - 1 > 1) {
            frames = frames - 1;
        }
    }

    private void speedUp() {
        if (frames + 1 < 11) {
            frames = frames + 1;
        }
    }

    private void screenshot() {
        Date date = new Date();
        saveFrame("screenshot_" + date.getTime() + ".jpg");
    }

    private class Button {
        String label;
        float x;    // top left corner x position
        float y;    // top left corner y position
        float w;    // width of button
        float h;    // height of button
        String callFunc;

        Button(String label, float x, float y, float w, float h, String callFunc) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.callFunc = callFunc;
        }

        void Draw() {
            fill(color(252, 252, 252));
            stroke(141);
            rect(x, y, w, h);
            fill(0);
            text(label, x + (w / 2) - (textWidth(label) / 2), y + (h / 2) + 5);
        }

        boolean MouseIsOver() {
            return mouseX > x && mouseX < (x + w) && mouseY > y && mouseY < (y + h);
        }
    }
}

