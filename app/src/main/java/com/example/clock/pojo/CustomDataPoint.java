package com.example.clock.pojo;

import com.jjoe64.graphview.series.DataPointInterface;

public class CustomDataPoint implements DataPointInterface {
    private final double x;
    private final double y;

    private final int phase;

    public CustomDataPoint(double x, double y, int phase){
        this.x = x;
        this.y = y;
        this.phase = phase;
    }
    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public int getPhase() {
        return phase;
    }
}
