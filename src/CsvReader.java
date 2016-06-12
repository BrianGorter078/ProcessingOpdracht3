import processing.core.PVector;

import java.io.*;
import java.util.ArrayList;


class CsvReader {
    private String csvPath = "rotterdamopendata_hoogtebestandtotaal_oost.csv";

    public static float MAX_Z = Float.MIN_VALUE;
    public static float MIN_Z = Float.MAX_VALUE;

    private final ArrayList<PVector> maps = new ArrayList();


    CsvReader() {
    }

    boolean inRange(float x, float y, float distance) {
        float x_ZADKINE = 92796;
        float xZadkineKlein = x_ZADKINE - distance;
        float xZadkineGroot = x_ZADKINE + distance;

        float y_ZADKINE = 436960;
        float yZadkineKlein = y_ZADKINE - distance;
        float yZadkineGroot = y_ZADKINE + distance;

        return x >= xZadkineKlein && x <= xZadkineGroot && y >= yZadkineKlein && y <= yZadkineGroot;
    }

    ArrayList<PVector> readCsv(float distance, String pathToCsv) {
        BufferedReader br = null;
        try {
            int skipLine = 0;
            String line;
            String splitLine = ",";
            br = new BufferedReader(new FileReader(pathToCsv));
            while ((line = br.readLine()) != null) {

                if(skipLine == 0) {
                    skipLine++;
                    continue;
                }

                String[] item = line.split(splitLine);
                float x = Float.parseFloat(item[0]);
                float y = Float.parseFloat(item[1]);
                float z = Float.parseFloat(item[2]);


                if (inRange(x,y,distance))
                    maps.add(new PVector(x, y, z));

                if (MIN_Z > z) {
                    MIN_Z = z;
                }
                if (MAX_Z < z) {
                    MAX_Z = z;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return maps;
    }

    void readWrite(float distance) {
        BufferedReader br = null;
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("1000.csv"), "utf-8"));
            int skipLine = 0;
            String line;
            String splitLine = ",";
            br = new BufferedReader(new FileReader(csvPath));
            while ((line = br.readLine()) != null) {

                if(skipLine == 0) {
                    skipLine++;
                    continue;
                }

                String[] item = line.split(splitLine);

                float x = Float.parseFloat(item[0]);
                float y = Float.parseFloat(item[1]);

                if (inRange(x,y,distance)) {
                    writer.write(item[0] + "," + item[1] + "," + item[2] + "\n");
                }

            }
            System.out.println("done");
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}