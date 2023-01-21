package com.example.clock.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clock.R;
import com.example.clock.pojo.CustomDataPoint;
import com.example.clock.pojo.Days;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphActivity extends AppCompatActivity {
    ImageButton backButton;
    GraphView graphView;
    Map<String, Map<Days, DataPoint[]>> nameDataPoint = new HashMap<>();
    Spinner nameSpinner;
    Spinner daySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        backButton = findViewById(R.id.back_button);
        graphView = findViewById(R.id.graph);
        nameSpinner = findViewById(R.id.nameSpinner);
        daySpinner = findViewById(R.id.daySpinner);


        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, Arrays.stream(Days.values()).map(Days::toString).collect(Collectors.toList()));
        daySpinner.setAdapter(dayAdapter);



        backButton.setOnClickListener(view -> {
            finish();
        });

        try {
            parseXls("korrelyatsia_2_1.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, new ArrayList<>(nameDataPoint.keySet()));
        nameSpinner.setAdapter(nameAdapter);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        nameSpinner.setOnItemSelectedListener(onItemSelectedListener);
        daySpinner.setOnItemSelectedListener(onItemSelectedListener);

    }


    private void updateView(){
        Days day = Days.valueOf((String)daySpinner.getSelectedItem());
        String name = nameSpinner.getSelectedItem().toString();
        graphView.removeAllSeries();
        drawGraph(nameDataPoint.get(name).get(day));
    }


    private void parseXls(String fileName) throws IOException {
        InputStream open = getApplicationContext().getAssets().open(fileName);
        Workbook workbook = new XSSFWorkbook(open);
        Sheet sheet = workbook.getSheetAt(1);
        for(int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++){
            Row currRow = sheet.getRow(i);
            if(currRow == null)
                continue;
            int lastRow = i;
            if(currRow.getCell(currRow.getFirstCellNum()).getCellType().equals(CellType.STRING)
                    && !currRow.getCell(currRow.getFirstCellNum()).getStringCellValue().equals("пульс")){
                String currName = currRow.getCell(currRow.getFirstCellNum()).getStringCellValue();
                nameDataPoint.put(currName, new HashMap<>());
                currRow = sheet.getRow(i);
                int needFirstIndex = currRow.getFirstCellNum();
                int needSecondIndex = needFirstIndex + 5;
                for(int j = 0; j < 7; j++){
                    int s = i + 2;
                    List<DataPoint> dataPoints = new ArrayList<>();
                    while(sheet.getRow(s).getCell(needFirstIndex) != null && sheet.getRow(s).getCell(needFirstIndex).getCellType().equals(CellType.NUMERIC)){
                        dataPoints.add(new DataPoint(sheet.getRow(s).getCell(needSecondIndex).getNumericCellValue(), sheet.getRow(s).getCell(needFirstIndex).getNumericCellValue()));
                        s++;
                    }
                    lastRow = Math.max(lastRow, s);
                    needFirstIndex = needSecondIndex + 1;
                    needSecondIndex = needFirstIndex + 5;
                    nameDataPoint.get(currName).put(Days.values()[j], dataPoints.toArray(new DataPoint[dataPoints.size()]));
                }
            }
            i = lastRow;
        }
    }


    private void drawGraph(DataPoint[] ds){
        graphView.clearAnimation();
        Map<Integer, Integer> phaseColor = new HashMap<>();
        phaseColor.put(1, Color.RED);
        phaseColor.put(2, Color.GREEN);
        phaseColor.put(3, Color.YELLOW);


        List<CustomDataPoint> ser = new ArrayList<>();
        ser.add(new CustomDataPoint(0, ds[0].getY(), (int)ds[0].getX()));
        for(int i = 1; i < ds.length; i++){
            if((int)ds[i].getX() == ser.get(ser.size()-1).getPhase()){
                ser.add(new CustomDataPoint(i, ds[i].getY(), (int)ds[i].getX()));
            }else{
                ser.add(new CustomDataPoint(i, ds[i].getY(), (int)ds[i].getX()));
                CustomDataPoint[] customDataPoints = ser.toArray(new CustomDataPoint[ser.size()]);
                LineGraphSeries<CustomDataPoint> series = new LineGraphSeries<>(customDataPoints);
                series.setThickness(10);
                series.setAnimated(true);
                series.setDrawAsPath(true);
                series.setColor(phaseColor.get(ser.get(ser.size()-1).getPhase()));
                graphView.addSeries(series);
                ser = new ArrayList<>();
                ser.add(new CustomDataPoint(i, ds[i].getY(), (int)ds[i].getX()));
            }

        }
    }

}
