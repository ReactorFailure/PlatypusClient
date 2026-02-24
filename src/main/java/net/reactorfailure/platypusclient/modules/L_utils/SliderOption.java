package net.reactorfailure.platypusclient.modules.L_utils;

import java.util.function.Consumer;

public class SliderOption implements ModuleOptions<Double> {
    private final String id;
    private final String label;

    private final double min;
    private final double max;
    private double value;

    private final double step;

    private final String minLabel;
    private final String maxLabel;

    private final Consumer<Double> onChange;


    public SliderOption(String id, String label, double min, double max, double initial, double step, String minLabel, String maxLabel, Consumer<Double> onChange) {
        this.id = id;
        this.label = label;
        this.min = min;
        this.max = max;
        this.step = Math.max(0, step);
        this.minLabel = minLabel;
        this.maxLabel = maxLabel;
        this.onChange = onChange;
        this.value = snap(clamp(initial));
    }

    public SliderOption(String id, String label, double min, double max, double initial, String minLabel, String maxLabel, Consumer<Double> onChange) {
        this(id, label, min, max, initial, 0, minLabel, maxLabel, onChange);
    }

    public SliderOption(String id, String label, double min, double max, double initial, double step, Consumer<Double> onChange) {
        this(id, label, min, max, initial, step, null, null, onChange);
    }

    public SliderOption(String id, String label, double min, double max, double initial, Consumer<Double> onChange) {
        this(id, label, min, max, initial, 0, null, null, onChange);
    }

    public SliderOption(String id, String label, double min, double max, double initial) {
        this(id, label, min, max, initial, 0, null, null, null);
    }


    @Override public String getId() { return id; }
    @Override public String getLabel() { return label; }
    @Override public Double getValue() { return value; }

    @Override
    public void setValue(Double newValue) {
        double snapped = snap(clamp(newValue));
        if (this.value == snapped) return;
        this.value = snapped;
        if (onChange != null) onChange.accept(snapped);
    }

    @Override
    public Object saveToConfig() { return value; }

    @Override
    public void loadFromConfig(Object raw) {
        if (raw instanceof Number n) setValue(n.doubleValue());
    }


    public double  getMin() { return min; }
    public double  getMax() { return max; }
    public double  getStep() { return step; }
    public boolean isStepped() { return step > 0; }

    public String  getMinLabel() { return minLabel; }
    public String  getMaxLabel() { return maxLabel; }
    public boolean hasEdgeLabels() { return minLabel != null || maxLabel != null; }

    public int getIntValue() { return (int) Math.round(value); }

    public void setFromFraction(double fraction) {
        fraction = Math.max(0.0, Math.min(1.0, fraction));
        setValue(min + fraction * (max - min));
    }

    public double getFraction() {
        return (max == min) ? 0 : (value - min) / (max - min);
    }

    public int getStepCount() {
        if (!isStepped()) return -1;
        return (int) Math.round((max - min) / step);
    }

    private double clamp(double v) {
        return Math.max(min, Math.min(max, v));
    }

    private double snap(double v) {
        if (step <= 0) return v;
        double snapped = Math.round((v - min) / step) * step + min;
        return clamp(snapped);
    }
}
